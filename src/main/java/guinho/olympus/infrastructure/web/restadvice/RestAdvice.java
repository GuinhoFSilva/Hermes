package guinho.olympus.infrastructure.web.restadvice;

import guinho.olympus.core.application.usecase.match.shared.MatchAccessDeniedException;
import guinho.olympus.core.application.usecase.match.shared.PlayerNotInQueueException;
import guinho.olympus.core.application.usecase.match.shared.ResourceNotFoundException;
import guinho.olympus.core.domain.shared.InvalidArgumentException;
import guinho.olympus.core.domain.shared.MatchAlreadyFinishedException;
import guinho.olympus.core.domain.shared.UnchangedFieldException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class RestAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        var error = ApiError.of(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MatchAccessDeniedException.class)
    public ResponseEntity<ApiError> handleMatchAccessDenied(MatchAccessDeniedException ex) {
        var error = ApiError.of(ex.getMessage(), HttpStatus.FORBIDDEN.value());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(PlayerNotInQueueException.class)
    public ResponseEntity<ApiError> handlePlayerNotInQueue(PlayerNotInQueueException ex) {
        var error = ApiError.of(ex.getMessage(), HttpStatus.CONFLICT.value());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ApiError> handleInvalidArgument(InvalidArgumentException ex) {
        var error = ApiError.of(ex.getMessage(), HttpStatus.UNPROCESSABLE_CONTENT.value());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(error);
    }

    @ExceptionHandler(UnchangedFieldException.class)
    public ResponseEntity<ApiError> handleUnchangedField(UnchangedFieldException ex) {
        var error = ApiError.of(ex.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MatchAlreadyFinishedException.class)
    public ResponseEntity<ApiError> handleMatchAlreadyFinished(MatchAlreadyFinishedException ex) {
        var error = ApiError.of(ex.getMessage(), HttpStatus.UNPROCESSABLE_CONTENT.value());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        var error = ApiError.of(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        var error = ApiError.of("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


}
