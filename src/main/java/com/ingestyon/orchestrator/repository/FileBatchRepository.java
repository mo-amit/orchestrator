package com.ingestyon.orchestrator.repository;

import com.ingestyon.orchestrator.entity.FileBatchLineItemEntity;
import com.ingestyon.orchestrator.processor.BaseProcessor;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class FileBatchRepository extends BaseRepository {

    private static final String COLLECTION_NAME = "process_run";
    private static Object lock = new Object();
    private static ArrayList<FileBatchLineItemEntity> workList;
    private static AtomicBoolean  initialized  = new AtomicBoolean(false);

    public FileBatchRepository(){
        if(initialized.compareAndSet(false,true)) {
            init();
        }

    }

    private void init(){
        database.getCollection(COLLECTION_NAME).createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));
        resetFileStatusOnRestart();
    }


    public ArrayList<FileBatchLineItemEntity> find(Bson query) {
        ArrayList<FileBatchLineItemEntity> retList = new ArrayList<FileBatchLineItemEntity>();
        MongoCursor<Document> cursor = null;
        if(query==null)
             cursor = database.getCollection(COLLECTION_NAME).find().iterator();
        else
            cursor = database.getCollection(COLLECTION_NAME).find(query).iterator();

        while (cursor.hasNext()){
            Document doc = cursor.next();
            FileBatchLineItemEntity entity = new FileBatchLineItemEntity(
                    doc.getString("id"),
                    doc.getInteger("fileID"),
                    doc.getString("file_path"),
                    doc.getString("configurationID"),
                    doc.getString("processing_status"),
                    doc.getDate("created_at"),
                    doc.getDate("updated_at"));
            retList.add(entity);
        }

        return retList;
    }

    // TODO: This is simply ignoring  the file status. It it was shut down before the file finished processing

    public ArrayList<FileBatchLineItemEntity> findAll() {
        return  find(null);
    }

    public ArrayList<FileBatchLineItemEntity> findUnprocessed() {
        Bson query = Filters.or(eq("processing_status",""),eq("processing_status",null));
        return  find(query);
    }

    public ArrayList<FileBatchLineItemEntity> findOrphaned() {
        Bson query = eq("processing_status", BaseProcessor.IN_PROGRESS_PROCESSING_STATUS);
        return  find(query);

    }

    public BsonValue persist(FileBatchLineItemEntity inFileBatchLineItemEntity) {
        Document doc = new Document("id",inFileBatchLineItemEntity.id)
                .append("fileID", inFileBatchLineItemEntity.fileId)
                .append("file_path", inFileBatchLineItemEntity.file_path)
                .append("configurationID", inFileBatchLineItemEntity.configurationID)
                .append("processing_status", inFileBatchLineItemEntity.processing_status)
                .append("created_at", inFileBatchLineItemEntity.created_at)
                .append("updated_at", inFileBatchLineItemEntity.updated_at);
        InsertOneResult result =  database.getCollection(COLLECTION_NAME).insertOne(doc);
        return result.getInsertedId();
    }


    public BsonValue updateStatus(FileBatchLineItemEntity inFileBatchLineItemEntity, String inStatus) {
        Bson filter = eq("id",inFileBatchLineItemEntity.id);
        Bson updateOperation = set("processing_status", inStatus);
        UpdateResult result =  database.getCollection(COLLECTION_NAME).updateOne(filter,updateOperation);
        return result.getUpsertedId();
    }

    // This method will get hit by multiple threads.

    public  FileBatchLineItemEntity pickOne(){
        FileBatchLineItemEntity retEntity = null;
        synchronized (lock) {
            if (workList == null || workList.size() == 0)
                workList = this.findUnprocessed();
            if (workList.size() != 0) {
                retEntity = workList.get(0);
                workList.remove(0);
            }
        }
        return retEntity;

    }


    private  void resetFileStatusOnRestart(){
        for ( FileBatchLineItemEntity orphanedEntity: findOrphaned()){
            this.updateStatus(orphanedEntity,null);
        }

    }

}
