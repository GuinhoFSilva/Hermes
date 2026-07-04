package guinho.olympus.core.domain.match.valueobject;

import guinho.olympus.core.domain.shared.InvalidArgumentException;

import java.util.Objects;
import java.util.UUID;

public class PlayerId {
    private final UUID value;

    private PlayerId(UUID value) {
        this.value = value;
    }

    public static PlayerId of(UUID value) {
        if (value == null) {
            throw new InvalidArgumentException("Invalid Player ID format");
        }
        return new PlayerId(value);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerId playerId = (PlayerId) o;
        return Objects.equals(value, playerId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
