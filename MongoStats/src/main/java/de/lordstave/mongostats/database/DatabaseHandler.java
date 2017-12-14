package de.lordstave.mongostats.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class DatabaseHandler {

    private final String host;
    private final String user;
    private final String database;
    private final String password;
    private final String statsCollectionName;
    private final int port;
    private final int threadPoolSize;
    private final int connectionPoolSize;

    private final ExecutorService executorService;

    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;

    private MongoCollection<Document> statsCollection;

    public DatabaseHandler(String host, int port, String user, String database, String password, String statsCollectionName, int threadPoolSize, int connectionPoolSize) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.database = database;
        this.password = password;
        this.statsCollectionName = statsCollectionName;
        this.threadPoolSize = threadPoolSize;
        this.connectionPoolSize = connectionPoolSize;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public boolean openConnection() {
        try {
            MongoClientOptions.Builder clientOptions = MongoClientOptions.builder();
            clientOptions.connectionsPerHost(connectionPoolSize);

            MongoClientURI mongoClientURI = new MongoClientURI("mongodb://" + user + ":" + password + "@" + host + ":" + port + "/?authSource=" + database + "&authMechanism=SCRAM-SHA-1", clientOptions);
            this.mongoClient = new MongoClient(mongoClientURI);
            this.mongoDatabase = mongoClient.getDatabase(database);

            this.statsCollection = mongoDatabase.getCollection(statsCollectionName);

            Document index = new Document("UUID", 1);
            this.statsCollection.createIndex(index, new IndexOptions().unique(true));
            return true;
        } catch(MongoException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public void executeAsync(Runnable runnable) {
        executorService.execute(runnable);
    }

    public void close(MongoCursor<Document> cursor, Document... documents) {
        try {
            if(cursor != null) {
                cursor.close();
            }
            Stream.of(documents).forEach((Document object) -> {
                if(object != null) {
                    object.clear();
                }
            });
        } catch(MongoException exception) {
            exception.printStackTrace();
        }
    }

    public MongoCollection<Document> getStatsCollection() {
        return statsCollection;
    }
}
