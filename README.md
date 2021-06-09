
1. Before compliing: Update com.ingestyon.orchestrator.repository.BaseRepository. Replace following to parameters
2. MONGO_URI (Fully qualified URI including user name and password example  "mongodb+srv://<username>:<password>@cluster0.3Gf5.mongodb.net"
3. MONGO_DB_NAME (name of the database eg. orchestrator) 
4. Build: mvn clean install 
5. Run:  java -jar target/orchestrator-1.0-SNAPSHOT-jar-with-dependencies.jar
