package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.abstractions.TokenExtractor;
import guinho.olympus.core.application.repository.MatchQuery;
import guinho.olympus.core.application.usecase.match.dto.MatchMapper;
import guinho.olympus.core.application.usecase.match.dto.MatchResponseDto;
import guinho.olympus.core.domain.match.Match;
import guinho.olympus.core.domain.match.enums.Status;
import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetPlayerMatchesUseCaseTest {
    @Mock
    private MatchQuery matchQuery;

    @Mock
    private TokenExtractor tokenExtractor;

    @InjectMocks
    private GetPlayerMatchesUseCase getPlayerMatchesUseCase;

    @Nested
    class GetPlayerMatches {
        @Test
        public void shouldFindAllPlayerMatches() {
            UUID matchId = UUID.randomUUID();
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);
            Match match = Match.reconstitute(matchId, participants, Status.FINISHED, LocalDateTime.now(), LocalDateTime.now());
            List<Match> matches = List.of(match);
            String token = "Bearer token";

            Mockito.when(tokenExtractor.extractPlayerId(token)).thenReturn(playerOne);

            Mockito.when(matchQuery.findByPlayerId(playerOne)).thenReturn(matches);

            List<MatchResponseDto> response = getPlayerMatchesUseCase.findMatches(token);

            assertEquals(matches.getFirst().getId(), response.getFirst().matchId());
            assertTrue(matches.getFirst().getParticipants().contains(playerOne));
            assertTrue(matches.getFirst().getParticipants().contains(playerTwo));
            Mockito.verify(tokenExtractor).extractPlayerId(token);
            Mockito.verify(matchQuery).findByPlayerId(playerOne);
        }
    }
}