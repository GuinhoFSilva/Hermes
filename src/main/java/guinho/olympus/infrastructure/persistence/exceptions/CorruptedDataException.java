package guinho.olympus.infrastructure.persistence.exceptions;

public class CorruptedDataException extends RuntimeException {
    public CorruptedDataException(String message) {
        super(message);
    }
}
