package com.ingestyon.orchestrator.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


public abstract class BaseRepository {

    protected static final String MONGO_URI = "mongodb+srv://orchestrator:3JMSppHY23ywfWp@cluster0.1fdg7.mongodb.net";
    protected static final String MONGO_DB_NAME = "orchestrator";

    protected static final MongoClient mongoClient = MongoClients.create(MONGO_URI);
    protected static final MongoDatabase database = mongoClient.getDatabase(MONGO_DB_NAME);


}
