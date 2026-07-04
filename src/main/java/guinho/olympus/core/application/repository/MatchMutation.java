package guinho.olympus.core.application.repository;

import guinho.olympus.core.domain.match.Match;

public interface MatchMutation {
    Match save(Match match);

    Match update(Match match);
}
