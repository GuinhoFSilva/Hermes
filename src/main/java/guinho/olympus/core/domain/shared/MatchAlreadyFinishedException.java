package guinho.olympus.core.domain.shared;

public class MatchAlreadyFinishedException extends RuntimeException {
    public MatchAlreadyFinishedException() {
        super("The match has already been finished");
    }
}
