import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBTests {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBTests.class);

    @Test
    public void testConnection() {


        try (MongoClient mongoClient = MongoClients.create("mongodb://admin:12345678@192.168.5.2:27017")) {


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
