package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.repository.MatchQuery;
import guinho.olympus.core.application.usecase.match.dto.MatchMapper;
import guinho.olympus.core.application.usecase.match.dto.MatchResponseDto;
import guinho.olympus.core.application.usecase.match.shared.MatchAccessDeniedException;
import guinho.olympus.core.application.usecase.match.shared.ResourceNotFoundException;
import guinho.olympus.core.domain.match.Match;
import guinho.olympus.core.domain.match.valueobject.PlayerId;

import java.util.UUID;

public class GetMatchUseCase {
    private final MatchQuery matchQuery;

    public GetMatchUseCase(MatchQuery matchQuery) {
        this.matchQuery = matchQuery;
    }

    public MatchResponseDto find(String token, UUID matchId) {
        PlayerId playerId = PlayerId.of(UUID.fromString(token));
        Match match = matchQuery.findById(matchId).orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        if(!match.getParticipants().contains(playerId)) {
            throw new MatchAccessDeniedException();
        }

        return MatchMapper.toResponse(match);
    }
}
