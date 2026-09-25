package guinho.olympus.infrastructure.beans_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import java.util.List;

@Configuration
public class RedisBeanConfig {
    @Bean
    public RedisScript<List> script() {
        return RedisScript.of(
                new ClassPathResource("matchmaking.lua"),
                List.class
        );
    }
}
