package guinho.olympus.core.domain.match.valueobject;

import guinho.olympus.core.domain.shared.InvalidArgumentException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerIdTest {
    @Nested
    class Create {
        @Test
        public void shouldCreatePlayerIdFromValidUuid() {
            UUID id = UUID.randomUUID();
            PlayerId playerId = PlayerId.of(id);
            assertEquals(id, playerId.getValue());
        }

        @Test
        public void shouldThrowWhenUuidIsNull() {
            InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> PlayerId.of(null));

            assertEquals("Invalid Player ID format", exception.getMessage());
        }
    }

}