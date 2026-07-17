package guinho.olympus.core.domain.shared;

public class PlayerNotInQueueException extends RuntimeException {
    public PlayerNotInQueueException() {
        super("Player with ID '%s' is not currently in the matchmaking queue");
    }
}
