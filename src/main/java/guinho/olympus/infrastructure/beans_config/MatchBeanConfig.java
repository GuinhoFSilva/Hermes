package guinho.olympus.infrastructure.beans_config;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.application.repository.MatchMutation;
import guinho.olympus.core.application.repository.MatchQuery;
import guinho.olympus.core.application.usecase.match.GetMatchUseCase;
import guinho.olympus.core.application.usecase.match.GetPlayerMatchesUseCase;
import guinho.olympus.core.application.usecase.match.JoinQueueUseCase;
import guinho.olympus.core.application.usecase.match.LeaveQueueUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MatchBeanConfig {
    @Bean
    public GetMatchUseCase getMatchUseCase(MatchQuery matchQuery) {
        return new GetMatchUseCase(matchQuery);
    }

    @Bean
    public GetPlayerMatchesUseCase getPlayerMatchesUseCase(MatchQuery matchQuery) {
        return new GetPlayerMatchesUseCase(matchQuery);
    }

    @Bean
    public JoinQueueUseCase joinQueueUseCase(QueueService queueService, MatchMutation matchMutation) {
        return new JoinQueueUseCase(queueService, matchMutation);
    }

    @Bean
    public LeaveQueueUseCase leaveQueueUseCase(QueueService queueService) {
        return new LeaveQueueUseCase(queueService);
    }

}
