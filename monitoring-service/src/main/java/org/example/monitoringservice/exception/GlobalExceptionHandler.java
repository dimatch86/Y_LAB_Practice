package org.example.monitoringservice.exception;

import org.example.monitoringservice.exception.custom.*;
import org.example.monitoringservice.util.ResponseUtil;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidDataException.class,
            TooRecentReadingException.class,
            ReadingTypeAlreadyExistsException.class,
            UserAlreadyExistException.class,
            ParameterMissingException.class})
    public ResponseEntity<Object> handleExceptionsForBadRequest(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.errorResponse(e.getLocalizedMessage()));
    }

    @ExceptionHandler({NotAvailableReadingException.class, UserNotFoundException.class})
    public ResponseEntity<Object> handleNotAvailableReadingException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.errorResponse(e.getLocalizedMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String errorMessage = String.join(";", errorMessages);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.errorResponse(errorMessage));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Object> handleDuplicateKeyException(UserAlreadyExistException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.errorResponse(e.getLocalizedMessage()));
    }
}
