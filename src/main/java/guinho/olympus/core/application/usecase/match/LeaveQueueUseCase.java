package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.domain.match.valueobject.PlayerId;

import java.util.UUID;

public class LeaveQueueUseCase {
    private final QueueService queueService;

    public LeaveQueueUseCase(QueueService queueService) {
        this.queueService = queueService;
    }

    public void execute(String token){
        PlayerId playerId = PlayerId.of(UUID.fromString(token));
        queueService.leaveQueue(playerId);
    }
}
