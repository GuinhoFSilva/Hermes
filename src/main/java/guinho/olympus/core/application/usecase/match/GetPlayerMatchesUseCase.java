package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.repository.MatchQuery;
import guinho.olympus.core.application.usecase.match.dto.MatchMapper;
import guinho.olympus.core.application.usecase.match.dto.MatchResponseDto;
import guinho.olympus.core.domain.match.Match;
import guinho.olympus.core.domain.match.valueobject.PlayerId;

import java.util.List;
import java.util.UUID;

public class GetPlayerMatchesUseCase {
    private final MatchQuery matchQuery;

    public GetPlayerMatchesUseCase(MatchQuery matchQuery) {
        this.matchQuery = matchQuery;
    }

    public List<MatchResponseDto> findMatches(String token) {
        PlayerId playerId = PlayerId.of(UUID.fromString(token));
        List<Match> matches = matchQuery.findByPlayerId(playerId);

        return MatchMapper.toResponse(matches);
    }
}
