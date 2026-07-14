package guinho.olympus.core.application.abstractions;

import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;

import java.util.Optional;

public interface QueueService {
    Optional<Participants> joinQueue(PlayerId playerId);
    void leaveQueue(PlayerId playerId);
}
