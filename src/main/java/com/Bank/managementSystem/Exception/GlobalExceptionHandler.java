package com.Bank.managementSystem.Exception;
import com.Bank.managementSystem.Response.ErrResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<ErrResponse> handleDuplicateAccountException(DuplicateAccountException ex, WebRequest request) {
        LOGGER.log(Level.SEVERE, "A duplicate account error occurred: ", ex);
        ErrResponse errorResponse = new ErrResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
        LOGGER.log(Level.WARNING, "Not found: ", ex);
        ErrResponse errorResponse = new ErrResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrResponse> handleInvalidInputException(InvalidInputException ex, WebRequest request) {
        LOGGER.log(Level.WARNING, "Invalid input: ", ex);
        ErrResponse errorResponse = new ErrResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrResponse> handleAllExceptions(Exception ex, WebRequest request) {
        LOGGER.log(Level.SEVERE, "An error occurred: ", ex);
        ErrResponse errorResponse = new ErrResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

