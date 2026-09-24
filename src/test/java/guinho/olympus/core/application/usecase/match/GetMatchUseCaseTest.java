package guinho.olympus.core.application.usecase.match;

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
            String token = playerOne.getValue().toString();

            Mockito.when(matchQuery.findById(matchId)).thenReturn(Optional.of(match));

            MatchResponseDto response = getMatchUseCase.find(token, matchId);

            assertNotNull(response);
            assertEquals(matchId, response.matchId());
            Mockito.verify(matchQuery).findById(matchId);
        }

        @Test
        public void shouldThrowWhenMatchDoesNotExist() {
            UUID matchId = UUID.randomUUID();
            String token = UUID.randomUUID().toString();

            Mockito.when(matchQuery.findById(matchId)).thenReturn(Optional.empty());
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> getMatchUseCase.find(token, matchId));

            assertEquals("Match not found", exception.getMessage());
            Mockito.verify(matchQuery).findById(matchId);
        }

        @Test
        public void shouldThrowWhenPlayerIsNotAParticipant() {
            UUID matchId = UUID.randomUUID();
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            PlayerId anotherPlayer = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);
            Match match = Match.reconstitute(matchId, participants, Status.FINISHED, LocalDateTime.now(), LocalDateTime.now());
            String token = anotherPlayer.getValue().toString();

            Mockito.when(matchQuery.findById(matchId)).thenReturn(Optional.of(match));
            MatchAccessDeniedException exception = assertThrows(MatchAccessDeniedException.class, () -> getMatchUseCase.find(token, matchId));

            assertEquals("You do not have permission to access this match", exception.getMessage());
            Mockito.verify(matchQuery).findById(matchId);
        }
    }
}