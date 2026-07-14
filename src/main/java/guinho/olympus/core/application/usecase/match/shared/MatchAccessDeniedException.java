package guinho.olympus.core.application.usecase.match.shared;

public class MatchAccessDeniedException extends RuntimeException {
    public MatchAccessDeniedException() {
        super("You do not have permission to access this match");
    }
}
