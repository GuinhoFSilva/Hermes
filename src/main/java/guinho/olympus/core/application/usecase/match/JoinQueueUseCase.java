package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.application.abstractions.TokenExtractor;
import guinho.olympus.core.application.repository.MatchMutation;
import guinho.olympus.core.application.usecase.match.dto.JoinQueueResult;
import guinho.olympus.core.domain.match.Match;
import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;

import java.util.Optional;

public class JoinQueueUseCase {
    private final TokenExtractor tokenExtractor;
    private final QueueService queueService;
    private final MatchMutation matchMutation;

    public JoinQueueUseCase(TokenExtractor tokenExtractor, QueueService queueService, MatchMutation matchMutation) {
        this.tokenExtractor = tokenExtractor;
        this.queueService = queueService;
        this.matchMutation = matchMutation;
    }

    public JoinQueueResult execute(String token) {
        PlayerId playerId = tokenExtractor.extractPlayerId(token);

        Optional<Participants> participants = queueService.joinQueue(playerId);

        if(participants.isPresent()) {
            Match match = Match.create(participants.get());
            matchMutation.save(match);
            return new JoinQueueResult(true);
        }

        return new JoinQueueResult(false);
    }
}
