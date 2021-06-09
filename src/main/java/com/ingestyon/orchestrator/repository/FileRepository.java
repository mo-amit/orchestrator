package com.ingestyon.orchestrator.repository;

import com.ingestyon.orchestrator.entity.FileBatchLineItemEntity;
import com.ingestyon.orchestrator.entity.FileLineItemEntity;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class FileRepository extends BaseRepository{

    private static final String COLLECTION_NAME = "process_run_insert";
    private static AtomicBoolean initialized  = new AtomicBoolean(false);


    public FileRepository(){
        if(initialized.compareAndSet(false,true)) {
            init();
        }

    }

    private void init(){
        database.getCollection(COLLECTION_NAME).createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));

    }


   // TODO: Consider removing BSON
    public BsonValue persist(FileLineItemEntity inFileLineItemEntity){
        Document doc = new Document("id", inFileLineItemEntity.id)
                .append("command",inFileLineItemEntity.command)
                .append("process_run_id", inFileLineItemEntity.process_run_id)
                .append("created_at", inFileLineItemEntity.created_at)
                .append("updated_at", inFileLineItemEntity.updated_at);
        InsertOneResult result =  database.getCollection(COLLECTION_NAME).insertOne(doc);
        return result.getInsertedId();

    }

    public BsonValue updateStatus(FileBatchLineItemEntity inFileLineItemEntity, String command) {
        Bson filter = eq("id",inFileLineItemEntity.id);
        Bson updateOperation = set("command", command);
        UpdateResult result =  database.getCollection(COLLECTION_NAME).updateOne(filter,updateOperation);
        return result.getUpsertedId();
    }

    private ArrayList<FileLineItemEntity> find() {
        ArrayList<FileLineItemEntity> retList = new ArrayList<FileLineItemEntity>();
        MongoCursor<Document> cursor = database.getCollection(COLLECTION_NAME).find().iterator();
        while (cursor.hasNext()){
            Document doc = cursor.next();
            retList.add (new FileLineItemEntity(
                    doc.getString("id"),
                    doc.getString("process_run_id"),
                    doc.getString("command"),
                    doc.getDate("created_at"),
                    doc.getDate("updated_at")));
        }

        return retList;
    }



}
