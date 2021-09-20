package com.clane.app.core.exception;

import com.clane.app.core.data.ClaneResponse;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<?> handlePasswordUnMatchException(HttpServletRequest request, DataNotFoundException exception) {
        ClaneResponse data = ClaneResponse.builder().status(HttpStatus.NO_CONTENT.value()).errorMessage(exception.getMessage()).build();
        HttpStatus status = getStatus(request);
        exception.printStackTrace();
        return ResponseEntity.status(status).body(data);
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handlePasswordUnMatchException(HttpServletRequest request, NumberFormatException exception) {
        ClaneResponse data = ClaneResponse.builder().status(HttpStatus.NOT_ACCEPTABLE.value()).errorMessage(exception.getMessage()).build();
        HttpStatus status = getStatus(request);
        return ResponseEntity.status(status).body(data);
    }

    @ExceptionHandler(SQLGrammarException.class)
    public ResponseEntity<?> handleUserNameNotFoundException(HttpServletRequest request, SQLGrammarException exception) {
        ClaneResponse data = ClaneResponse.builder().status(HttpStatus.EXPECTATION_FAILED.value()).errorMessage(exception.getMessage()).build();
        HttpStatus status = getStatus(request);
        return ResponseEntity.status(status).body(data);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleUserNameNotFoundException(HttpServletRequest request, UsernameNotFoundException exception) {
        ClaneResponse data = ClaneResponse.builder().status(HttpStatus.NOT_FOUND.value()).errorMessage(exception.getMessage()).build();
        HttpStatus status = getStatus(request);
        return ResponseEntity.status(status).body(data);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleUserNameNotFoundException(HttpServletRequest request, BadCredentialsException exception) {
        ClaneResponse data = ClaneResponse.builder().status(HttpStatus.BAD_REQUEST.value()).errorMessage(exception.getMessage()).build();
        HttpStatus status = getStatus(request);
        return ResponseEntity.status(status).body(data);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleUserNameNotFoundException(HttpServletRequest request, ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        String message = errors.stream().filter(Objects::nonNull).collect(Collectors.joining(",\n"));
        ClaneResponse data = ClaneResponse.builder().status(HttpStatus.BAD_REQUEST.value()).errorMessage(message).build();
        HttpStatus status = getStatus(request);
        return ResponseEntity.status(status).body(data);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleConstraintException(HttpServletRequest request, Exception exception) {
        ClaneResponse data = ClaneResponse.builder().status(HttpStatus.BAD_REQUEST.value()).errorMessage(exception.getMessage()).build();
        exception.printStackTrace();
        HttpStatus status = getStatus(request);
        return ResponseEntity.status(status).body(data);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException exception) {
        HttpStatus status = getStatus(request);
        List<String> errors = exception.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        String message = errors.stream().filter(Objects::nonNull).collect(Collectors.joining(",\n"));
        ClaneResponse data = ClaneResponse.builder().status(HttpStatus.BAD_REQUEST.value()).errorMessage(message).build();
        return ResponseEntity.status(status).body(data);
    }


    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
