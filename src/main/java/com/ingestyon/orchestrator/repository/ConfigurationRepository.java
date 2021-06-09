package com.ingestyon.orchestrator.repository;

import com.ingestyon.orchestrator.entity.ConfigurationEntity;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class ConfigurationRepository extends BaseRepository {

    private static final String COLLECTION_NAME = "process_configuration";
    private static AtomicBoolean initialized  = new AtomicBoolean(false);


    public ConfigurationRepository() {
        if(initialized.compareAndSet(false,true)) {
            init();
        }

    }

    private void init(){
        if( this.findFirst() == null ) {
            ConfigurationEntity initialEntry = new ConfigurationEntity("1",
                    null, "insert", new Date(), new Date());
            persist(initialEntry);
        }
        database.getCollection(COLLECTION_NAME)
                .createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));

    }




    public ArrayList<ConfigurationEntity> find() {
        ArrayList<ConfigurationEntity> retList = new ArrayList<ConfigurationEntity>();
        MongoCursor<Document> cursor  = database.getCollection(COLLECTION_NAME).find().iterator();
        while (cursor.hasNext()){
            Document doc = cursor.next();
            retList.add (new ConfigurationEntity(
                    doc.getString("id"),
                    doc.getString("processing_status"),
                    doc.getString("process"),
                    doc.getDate("created_at"),
                    doc.getDate("update_at")));
        }

            return retList;

    }


    public BsonValue persist(ConfigurationEntity inConfigurationEntity) {
        Document doc = new Document("id",inConfigurationEntity.id)
                .append("processing_status", inConfigurationEntity.getProcessing_status())
                .append("process",inConfigurationEntity.getProcess())
                .append("created_at", inConfigurationEntity.created_at)
                .append("updated_at", inConfigurationEntity.updated_at);
        InsertOneResult result =  database.getCollection(COLLECTION_NAME).insertOne(doc);
        return result.getInsertedId();
    }

    public BsonValue updateStatus(ConfigurationEntity inConfigurationEntity, String inStatus) {
        Bson filter = eq("id",inConfigurationEntity.id);
        Bson updateOperation = set("processing_status", inStatus);
        UpdateResult result =  database.getCollection(COLLECTION_NAME).updateOne(filter,updateOperation);
        return result.getUpsertedId();
    }

    public ConfigurationEntity findFirst() {
        List<ConfigurationEntity> retList = this.find();
        if (retList.size() !=0)
            return retList.get(0);

        return  null;

    }


}
