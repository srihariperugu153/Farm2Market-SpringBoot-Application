package com.farmtomarket.application.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String ERROR = "Oops!";

    @ExceptionHandler({RuntimeException.class,ApplicationException.class})
    public ResponseEntity<ErrorMessage> handleBaseException(RuntimeException ex){
        log.error(ERROR,ex);
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode("500");
        errorMessage.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = SystemAuthException.class)
    public ResponseEntity<ErrorMessage> handleBaseException(SystemAuthException ex){
        log.error(ERROR,ex);
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode("401");
        errorMessage.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleRuntimeException(Exception ex){
        log.error(ERROR,ex);
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode("500");
        errorMessage.setMessage("Server failed to Respond!!");
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationExceptions(MethodArgumentNotValidException ex){
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode("400");
        errorMessage.setMessage(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> handleConstraintViolationException(ConstraintViolationException ex){
        log.error(ERROR,ex);
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode("400");
        errorMessage.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
