package guinho.olympus.infrastructure.beans_config;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.application.abstractions.TokenExtractor;
import guinho.olympus.core.application.repository.MatchMutation;
import guinho.olympus.core.application.repository.MatchQuery;
import guinho.olympus.core.application.usecase.match.GetMatchUseCase;
import guinho.olympus.core.application.usecase.match.GetPlayerMatchesUseCase;
import guinho.olympus.core.application.usecase.match.JoinQueueUseCase;
import guinho.olympus.core.application.usecase.match.LeaveQueueUseCase;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.UUID;

@Configuration
public class MatchBeanConfig {
    @Bean
    public GetMatchUseCase getMatchUseCase(TokenExtractor tokenExtractor, MatchQuery matchQuery) {
        return new GetMatchUseCase(tokenExtractor, matchQuery);
    }

    @Bean
    public GetPlayerMatchesUseCase getPlayerMatchesUseCase(TokenExtractor tokenExtractor, MatchQuery matchQuery) {
        return new GetPlayerMatchesUseCase(tokenExtractor, matchQuery);
    }

    @Bean
    public JoinQueueUseCase joinQueueUseCase(TokenExtractor tokenExtractor, QueueService queueService, MatchMutation matchMutation) {
        return new JoinQueueUseCase(tokenExtractor, queueService, matchMutation);
    }

    @Bean
    public LeaveQueueUseCase leaveQueueUseCase(TokenExtractor tokenExtractor, QueueService queueService) {
        return new LeaveQueueUseCase(tokenExtractor, queueService);
    }

    @Bean
    public RedisTemplate<String, UUID> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UUID> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

}
