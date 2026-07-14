package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.application.abstractions.TokenExtractor;
import guinho.olympus.core.domain.match.valueobject.PlayerId;

public class LeaveQueueUseCase {
    private final TokenExtractor tokenExtractor;
    private final QueueService queueService;

    public LeaveQueueUseCase(TokenExtractor tokenExtractor, QueueService queueService) {
        this.tokenExtractor = tokenExtractor;
        this.queueService = queueService;
    }

    public void execute(String token){
        PlayerId playerId = tokenExtractor.extractPlayerId(token);
        queueService.leaveQueue(playerId);
    }
}
