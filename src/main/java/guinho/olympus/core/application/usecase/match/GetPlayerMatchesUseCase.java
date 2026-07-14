package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.abstractions.TokenExtractor;
import guinho.olympus.core.application.repository.MatchQuery;
import guinho.olympus.core.application.usecase.match.dto.MatchMapper;
import guinho.olympus.core.application.usecase.match.dto.MatchResponseDto;
import guinho.olympus.core.domain.match.Match;
import guinho.olympus.core.domain.match.valueobject.PlayerId;

import java.util.List;

public class GetPlayerMatchesUseCase {
    private final TokenExtractor tokenExtractor;
    private final MatchQuery matchQuery;

    public GetPlayerMatchesUseCase(TokenExtractor tokenExtractor, MatchQuery matchQuery) {
        this.tokenExtractor = tokenExtractor;
        this.matchQuery = matchQuery;
    }

    public List<MatchResponseDto> findMatches(String token) {
        PlayerId playerId = tokenExtractor.extractPlayerId(token);
        List<Match> matches = matchQuery.findByPlayerId(playerId);

        return MatchMapper.toResponse(matches);
    }
}
