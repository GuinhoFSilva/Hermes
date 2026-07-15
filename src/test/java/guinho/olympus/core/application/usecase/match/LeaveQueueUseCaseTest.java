package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.application.abstractions.TokenExtractor;
import guinho.olympus.core.application.repository.MatchMutation;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LeaveQueueUseCaseTest {
    @Mock
    private QueueService queueService;

    @Mock
    private TokenExtractor tokenExtractor;

    @InjectMocks
    private LeaveQueueUseCase leaveQueueUseCase;

    @Nested
    class LeaveQueue {
        @Test
        public void shouldLeaveQueueWithSuccess() {
            PlayerId playerId = PlayerId.of(UUID.randomUUID());
            String token = "Bearer token";

            Mockito.when(tokenExtractor.extractPlayerId(token)).thenReturn(playerId);

            leaveQueueUseCase.execute(token);

            Mockito.verify(tokenExtractor).extractPlayerId(token);
            Mockito.verify(queueService).leaveQueue(playerId);
        }
    }

}