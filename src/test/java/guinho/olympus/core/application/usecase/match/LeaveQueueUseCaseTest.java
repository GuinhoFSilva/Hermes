package guinho.olympus.core.application.usecase.match;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class LeaveQueueUseCaseTest {
    @Mock
    private QueueService queueService;

    @InjectMocks
    private LeaveQueueUseCase leaveQueueUseCase;

    @Nested
    class LeaveQueue {
        @Test
        public void shouldLeaveQueueWithSuccess() {
            PlayerId playerId = PlayerId.of(UUID.randomUUID());
            String token = playerId.getValue().toString();

            leaveQueueUseCase.execute(token);

            Mockito.verify(queueService).leaveQueue(playerId);
        }
    }

}