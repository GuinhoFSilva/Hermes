package guinho.olympus.core.domain.match.valueobject;

import guinho.olympus.core.domain.shared.InvalidArgumentException;

import java.util.Objects;

public class Participants {
    private final PlayerId first;
    private final PlayerId second;

    private Participants(PlayerId first, PlayerId second) {
        this.first = first;
        this.second = second;
    }

    public static Participants of(PlayerId first, PlayerId second) {
        if (first == null || second == null) {
            throw new InvalidArgumentException("Participants cannot be null");
        }
        if (first.equals(second)) {
            throw new InvalidArgumentException("A player cannot play against themselves");
        }

        return new Participants(first, second);
    }

    public boolean contains(PlayerId playerId) {
        return first.equals(playerId) || second.equals(playerId);
    }

    public PlayerId opponentOf(PlayerId playerId) {
        if (first.equals(playerId)) return second;
        if (second.equals(playerId)) return first;

        throw new InvalidArgumentException("Player is not a participant");
    }

    public PlayerId getFirst() {
        return first;
    }

    public PlayerId getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Participants that = (Participants) o;
        return Objects.equals(first, that.first) && Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
