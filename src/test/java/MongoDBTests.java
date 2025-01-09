import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ServerSettings;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class MongoDBTests {

    @Test
    public void testConnection() {
        ServerAddress seed1 = new ServerAddress("localhost", 27017);
//        ServerAddress seed2 = new ServerAddress("host2", 27017);
//        ServerAddress seed3 = new ServerAddress("host3", 27017);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Arrays.asList(seed1)))
                .build();
        try (MongoClient mongoClient = MongoClients.create(settings)) {


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
