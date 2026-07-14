package guinho.olympus.core.application.repository;

import guinho.olympus.core.domain.match.Match;
import guinho.olympus.core.domain.match.valueobject.PlayerId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchQuery {
    Optional<Match> findById(UUID id);

    List<Match> findByPlayerId(PlayerId playerId);
}
