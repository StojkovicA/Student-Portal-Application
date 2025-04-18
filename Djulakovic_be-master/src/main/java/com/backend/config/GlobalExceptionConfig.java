package com.backend.config;

import com.backend.exceptions.SimpleException;
import io.jsonwebtoken.JwtException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionConfig {

    private static final Log LOG = LogFactory.getLog(GlobalExceptionConfig.class);

    @ExceptionHandler(value = JwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleJwtException(JwtException e) {
        LOG.error("Handle JWT Exception error", e);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(BAD_REQUEST)
    public Map<String, Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        List<SimpleException> errors = new ArrayList<>();

        for (ObjectError error : e.getBindingResult()
                .getAllErrors()) {
            if (error instanceof FieldError) {
                processFieldError(errors, error);
            } else {
                processUnknownError(errors, error);
            }
        }
        LOG.info("handling MethodArgumentNotValidException: " + errors);
        return Collections.singletonMap("errors", errors);
    }

    private void processFieldError(List<SimpleException> errors, ObjectError error) {
        FieldError er = (FieldError) error;
        SimpleException exception = new SimpleException();
        exception.setScope(er.getField());
        exception.setMessage(error.getDefaultMessage());
        errors.add(exception);
    }

    private void processUnknownError(List<SimpleException> errors, ObjectError error) {
        String[] split = requireNonNull(error.getDefaultMessage())
                .split("\n");
        for (String s : split) {
            String[] singleError = s.split(":");
            if (singleError.length == 2) {
                SimpleException exception = new SimpleException();
                exception.setScope(singleError[0]);
                exception.setMessage(singleError[1]);
                errors.add(exception);
            } else if (singleError.length == 1) {
                SimpleException exception = new SimpleException();
                exception.setScope("global");
                exception.setMessage(singleError[0]);
                errors.add(exception);
            }
        }
    }
}
