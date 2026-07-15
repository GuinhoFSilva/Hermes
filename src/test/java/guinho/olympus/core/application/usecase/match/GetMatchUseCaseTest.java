package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.abstractions.TokenExtractor;
import guinho.olympus.core.application.repository.MatchQuery;
import guinho.olympus.core.application.usecase.match.dto.MatchResponseDto;
import guinho.olympus.core.application.usecase.match.shared.MatchAccessDeniedException;
import guinho.olympus.core.application.usecase.match.shared.ResourceNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetMatchUseCaseTest {
    @Mock
    private TokenExtractor tokenExtractor;

    @Mock
    private MatchQuery matchQuery;

    @InjectMocks
    private GetMatchUseCase getMatchUseCase;

    @Nested
    class GetMatch {
        @Test
        public void shouldFindAMatchWithSuccess() {
            UUID matchId = UUID.randomUUID();
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);
            Match match = Match.reconstitute(matchId, participants, Status.FINISHED, LocalDateTime.now(), LocalDateTime.now());
            String token = "Bearer token";

            Mockito.when(tokenExtractor.extractPlayerId(token)).thenReturn(playerOne);
            Mockito.when(matchQuery.findById(matchId)).thenReturn(Optional.of(match));

            MatchResponseDto response = getMatchUseCase.find(token, matchId);

            assertNotNull(response);
            assertEquals(matchId, response.matchId());
            Mockito.verify(matchQuery).findById(matchId);
            Mockito.verify(tokenExtractor).extractPlayerId(token);
        }

        @Test
        public void shouldThrowWhenMatchDoesNotExist() {
            UUID matchId = UUID.randomUUID();
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            String token = "Bearer token";

            Mockito.when(tokenExtractor.extractPlayerId(token)).thenReturn(playerOne);
            Mockito.when(matchQuery.findById(matchId)).thenReturn(Optional.empty());
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> getMatchUseCase.find(token, matchId));

            assertEquals("Match not found", exception.getMessage());
            Mockito.verify(matchQuery).findById(matchId);
            Mockito.verify(tokenExtractor).extractPlayerId(token);
        }

        @Test
        public void shouldThrowWhenPlayerIsNotAParticipant() {
            UUID matchId = UUID.randomUUID();
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            PlayerId anotherPlayer = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);
            Match match = Match.reconstitute(matchId, participants, Status.FINISHED, LocalDateTime.now(), LocalDateTime.now());
            String token = "Bearer token";

            Mockito.when(tokenExtractor.extractPlayerId(token)).thenReturn(anotherPlayer);
            Mockito.when(matchQuery.findById(matchId)).thenReturn(Optional.of(match));
            MatchAccessDeniedException exception = assertThrows(MatchAccessDeniedException.class, () -> getMatchUseCase.find(token, matchId));

            assertEquals("You do not have permission to access this match", exception.getMessage());
            Mockito.verify(matchQuery).findById(matchId);
            Mockito.verify(tokenExtractor).extractPlayerId(token);
        }
    }
}