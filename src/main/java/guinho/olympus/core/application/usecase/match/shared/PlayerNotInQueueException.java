package guinho.olympus.core.application.usecase.match.shared;

public class PlayerNotInQueueException extends RuntimeException {
    public PlayerNotInQueueException() {
        super("Player is not currently in the matchmaking queue");
    }
}
