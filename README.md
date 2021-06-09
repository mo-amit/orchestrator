
1. Before compliing: Update **com.ingestyon.orchestrator.repository.BaseRepository**. Replace following to parameters
2. **MONGO_URI** (Fully qualified URI including user name and password example  "mongodb+srv://_{username}_:_{password}_@cluster0.3Gf5.mongodb.net"
3. **MONGO_DB_NAME** (name of the database eg. orchestrator) 
4. Build: _mvn clean install_ 
5. Run:  _java -jar target/orchestrator-1.0-SNAPSHOT-jar-with-dependencies.jar_
