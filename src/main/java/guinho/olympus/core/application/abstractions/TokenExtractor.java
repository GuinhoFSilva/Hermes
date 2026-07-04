package guinho.olympus.core.application.abstractions;

import guinho.olympus.core.domain.match.valueobject.PlayerId;

public interface TokenExtractor {
    PlayerId extractPlayerId(String token);
}
