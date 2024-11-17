package vistager.exception_handling;

import vistager.exception_handling.exception.LoginAuthException;
import vistager.exception_handling.exception.NoEntityFoundException;
import vistager.exception_handling.exception.UnknownOriginException;
import vistager.exception_handling.exception.UserExistsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // you can't find entity in db
    @ExceptionHandler(NoEntityFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoEntityFoundException(NoEntityFoundException ex,
                                                                       WebRequest request) {
        Map<String, String> body = createDefaultHeadersForResponse(ex, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // if such user already exists (during registration)
    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleUserExistsException(UserExistsException ex,
                                                                       WebRequest request) {
        Map<String, String> body = createDefaultHeadersForResponse(ex, HttpStatus.CONFLICT);

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // failed authentication via login
    @ExceptionHandler(LoginAuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handlerLoginAuthException(LoginAuthException ex,
                                                            WebRequest request) {
        Map<String, String> body = createDefaultHeadersForResponse(ex, HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnknownOriginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handlerUnknownOriginException(UnknownOriginException ex) {
        Map<String, String> body = createDefaultHeadersForResponse(ex, HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    private Map<String, String> createDefaultHeadersForResponse(Exception ex, HttpStatus status) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", String.valueOf(status.value()));
        body.put("error-message", ex.getMessage());

        return body;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::getErrorMessage)
                .collect(Collectors.toList());
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String field = ((FieldError) e).getField();
            return field + " " + e.getDefaultMessage();
        }
        return e.getDefaultMessage();
    }
}
