# Instructions 

1. Before compliing: Update com.ingestyon.orchestrator.repository.BaseRepository. Update following to parameters
    A. MONGO_URI (Fully qualified URI including user name and password example  "mongodb+srv://<username>:<password>@cluster0.3Gf5.mongodb.net"
    B. MONGO_DB_NAME (name of the database eg. orchestrator)  

2. Build: mvn clean install 
3. Run:  java -jar target/orchestrator-1.0-SNAPSHOT-jar-with-dependencies.jar
