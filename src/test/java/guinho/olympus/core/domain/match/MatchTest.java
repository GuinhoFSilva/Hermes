package guinho.olympus.core.domain.match;

import guinho.olympus.core.domain.match.enums.Status;
import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import guinho.olympus.core.domain.shared.MatchAlreadyFinishedException;
import guinho.olympus.core.domain.shared.UnchangedFieldException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {
    @Nested
    class Create {
        @Test
        public void shouldCreateAMatchWithSuccess() {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);

            Match match = Match.create(participants);

            assertNotNull(match);
            assertNotNull(match.getId());
            assertTrue(match.getParticipants().contains(playerOne));
            assertTrue(match.getParticipants().contains(playerTwo));
            assertNotNull(match.getCreatedAt());
            assertEquals(Status.STARTED, match.getStatus());
        }

    }

    @Nested
    class ChangeStatus {
        @Test
        public void shouldChangeStatusWithSuccess() {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);
            Match match = Match.create(participants);

            match.changeStatus(Status.FINISHED);
            assertEquals(Status.FINISHED, match.getStatus());
        }

        @Test
        public void shouldThrowUnchangedFieldExceptionWhenStatusIsTheSame() {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);
            Match match = Match.create(participants);

            UnchangedFieldException exception = assertThrows(UnchangedFieldException.class, () -> match.changeStatus(Status.STARTED));

            assertEquals("The new status must be different from the current status", exception.getMessage());
        }

        @Test
        public void shouldNotAllowChangingStatusOfAFinishedMatch() {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);
            Match match = Match.create(participants);

            match.changeStatus(Status.FINISHED);
            MatchAlreadyFinishedException exception = assertThrows(MatchAlreadyFinishedException.class, () -> match.changeStatus(Status.STARTED));

            assertEquals("The match has already been finished", exception.getMessage());
        }
    }
}