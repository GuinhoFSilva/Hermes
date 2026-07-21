package guinho.olympus.infrastructure.integration;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.atomic.AtomicBoolean;

public class IntegrationTestContainers implements BeforeAllCallback {
    private static final AtomicBoolean CONTAINERS_STARTED = new AtomicBoolean(false);
    private static final RedisContainer REDIS_CONTAINER = new RedisContainer(DockerImageName.parse("redis"));

    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_hermes")
            .withUsername("test_user")
            .withPassword("root");


    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!CONTAINERS_STARTED.get()) {
            MY_SQL_CONTAINER.start();
            REDIS_CONTAINER.start();
            CONTAINERS_STARTED.set(true);
        }
    }

    public static String getHost() {
        return REDIS_CONTAINER.getHost();
    }

    public static Integer getPort() {
        return REDIS_CONTAINER.getFirstMappedPort();
    }
}
