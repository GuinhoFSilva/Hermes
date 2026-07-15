package guinho.olympus.core.domain.match.valueobject;

import guinho.olympus.core.domain.shared.InvalidArgumentException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantsTest {
    @Nested
    class Create {
        @Test
        public void shouldCreateParticipantsWithSuccess() {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);

            assertNotNull(participants);
            assertTrue(participants.contains(playerOne));
            assertTrue(participants.contains(playerTwo));
            assertEquals(playerTwo, participants.opponentOf(playerOne));
            assertEquals(playerOne, participants.opponentOf(playerTwo));
        }

        @Test
        public void shouldThrowInvalidArgumentExceptionWhenAPlayerIsNull() {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());

            InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> Participants.of(playerOne, null));

            assertEquals("Participants cannot be null", exception.getMessage());
        }

        @Test
        public void shouldThrowWhenParticipantsAreTheSame() {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());

            InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> Participants.of(playerOne, playerOne));

            assertEquals("A player cannot play against themselves", exception.getMessage());
        }
    }

    @Nested
    class OpponentOf {
        @Test
        public void shouldReturnOpponent() {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);

            PlayerId opponent = participants.opponentOf(playerOne);
            assertEquals(playerTwo, opponent);
        }

        @Test
        public void shouldThrowWhenPlayerIsNotAParticipant() {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            PlayerId playerThree = PlayerId.of(UUID.randomUUID());
            Participants participants = Participants.of(playerOne, playerTwo);

            InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> participants.opponentOf(playerThree));

            assertEquals("Player is not a participant", exception.getMessage());
        }
    }

}