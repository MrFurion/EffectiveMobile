package by.effective.mobile.eb.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final String DESCRIPTION = "description";

    @ExceptionHandler(BadCredentialsException.class)
    private ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The username or password is incorrect");
        return errorDetail;
    }

    @ExceptionHandler(AccountStatusException.class)
    private ProblemDetail handleAccountStatusException(AccountStatusException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The account is locked");
        return errorDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    private ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "You are not authorized to access this resource");
        return errorDetail;
    }

    @ExceptionHandler(SignatureException.class)
    private ProblemDetail handleSignatureException(SignatureException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The JWT signature is invalid");
        return errorDetail;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    private ProblemDetail handleExpiredJwtException(ExpiredJwtException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The JWT token has expired");
        return errorDetail;
    }

    @ExceptionHandler(Exception.class)
    private ProblemDetail handleUnknownException(Exception exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Unknown internal server error.");
        return errorDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException exception, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid data");
        errorDetail.setProperty("description", "An error occurred while saving the data. Please check the entered values.");
        errorDetail.setProperty("errors", errors);
        errorDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));

        return errorDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid data");
        errorDetail.setProperty(DESCRIPTION, "An error occurred while saving the data. Please check the entered values.");
        return errorDetail;
    }

    @ExceptionHandler(UserNotFoundException.class)
    private ProblemDetail handleUserNotFoundException(UserNotFoundException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "User not found");
        return errorDetail;
    }

    @ExceptionHandler(CardNotFoundException.class)
    private ProblemDetail handleCardNotFoundException(CardNotFoundException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Card not found");
        return errorDetail;
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    private ProblemDetail handleCardAlreadyExists(CardAlreadyExistsException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Card already exists");
        return errorDetail;
    }

    @ExceptionHandler(CardNotActiveException.class)
    private ProblemDetail handleCardNotActiveException(CardNotActiveException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Card not active");
        return errorDetail;
    }

    @ExceptionHandler(CardNegativeBalanceException.class)
    private ProblemDetail handleCardNegativeBalanceException(CardNegativeBalanceException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Insufficient balance on card");
        return errorDetail;
    }

    @ExceptionHandler(LimitDayNotSetException.class)
    private ProblemDetail handleLimitDayNotSetException(LimitDayNotSetException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Limit day not set for this card");
        return errorDetail;
    }

    @ExceptionHandler(LimitMonthNotSetException.class)
    private ProblemDetail handleLimitMonthNotSetException(LimitMonthNotSetException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Limit month not set for this card");
        return errorDetail;
    }
}
