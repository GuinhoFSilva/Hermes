package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.application.abstractions.TokenExtractor;
import guinho.olympus.core.application.repository.MatchMutation;
import guinho.olympus.core.application.usecase.match.dto.JoinQueueResult;
import guinho.olympus.core.domain.match.Match;
import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JoinQueueUseCaseTest {
    @Mock
    private QueueService queueService;

    @Mock
    private MatchMutation matchMutation;

    @Mock
    private TokenExtractor tokenExtractor;

    @InjectMocks
    private JoinQueueUseCase joinQueueUseCase;

    @Nested
    class JoinQueue {
        @Test
        public void shouldCreateMatchWhenOpponentIsAvailable() {
            PlayerId playerId = PlayerId.of(UUID.randomUUID());
            PlayerId anotherPlayerId = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(anotherPlayerId, playerId);
            String token = "Bearer Token";

            Mockito.when(tokenExtractor.extractPlayerId(token)).thenReturn(playerId);
            Mockito.when(queueService.joinQueue(playerId)).thenReturn(Optional.of(participants));
            Mockito.when(matchMutation.save(Mockito.any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

            JoinQueueResult result = joinQueueUseCase.execute(token);

            assertTrue(result.hasMatch());
            Mockito.verify(tokenExtractor).extractPlayerId(token);
            Mockito.verify(queueService).joinQueue(playerId);
            Mockito.verify(matchMutation).save(Mockito.any(Match.class));
        }

        @Test
        public void shouldJoinQueueWithoutCreatingMatchWhenNoOpponentIsAvailable() {
            PlayerId playerId = PlayerId.of(UUID.randomUUID());
            String token = "Bearer Token";

            Mockito.when(tokenExtractor.extractPlayerId(token)).thenReturn(playerId);
            Mockito.when(queueService.joinQueue(playerId)).thenReturn(Optional.empty());

            JoinQueueResult result = joinQueueUseCase.execute(token);

            assertFalse(result.hasMatch());
            Mockito.verify(tokenExtractor).extractPlayerId(token);
            Mockito.verify(queueService).joinQueue(playerId);
            Mockito.verifyNoInteractions(matchMutation);
        }
    }
}