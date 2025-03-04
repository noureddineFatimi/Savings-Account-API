package com.example.Exceptions;

import java.time.DateTimeException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.example.ErrorDetails.ErrorDetails;

@RestControllerAdvice
public class GlobalException {

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message); // Appelle le constructeur de RuntimeException avec le message
        }
    }
    @ExceptionHandler(ResourceNotFoundException.class) //Exception du id entré non trouvé
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails("RESOURCE_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    
    @ExceptionHandler(HttpMessageNotReadableException.class) //Exception du objet entré est null ou de format incorrect
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorDetails errorDetails = new ErrorDetails("INCORRECT_DATA", "Missing or Incorrect request body: " + ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) //Exception au cas du erreur du chaque element du l'objet 
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<String> errorMessages = ex.getBindingResult().getAllErrors().stream()
        .map(error -> ((FieldError) error).getDefaultMessage())
        .collect(Collectors.toList());

        // Join all error messages into a single string or return as a list
        String combinedErrorMessage = String.join(", ", errorMessages);
        ErrorDetails errorDetails = new ErrorDetails("VALIDATION_FAILED", combinedErrorMessage);

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataAccessException.class) // Exception lors de la recuperation du data du la base de donnees
    public ResponseEntity<ErrorDetails> handleDatabaseExceptions(DataAccessException ex) {
    ErrorDetails errorDetails = new ErrorDetails("DATABASE_ERROR", "SQL Error/Exception from Database.");
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class) //Exception des pathVariables
    public ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    ErrorDetails errorDetails = new ErrorDetails("ARGUMENT_ERROR", "Incorrect or Type Incorrect of the argument in the request");
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    public static class HoureException extends RuntimeException {
        public HoureException(String message) {
            super(message); // Appelle le constructeur de RuntimeException avec le message
        }
    }
    @ExceptionHandler(HoureException.class) //Exception du id entré non trouvé
    public ResponseEntity<ErrorDetails> handleHoureException(HoureException ex) {
        ErrorDetails errorDetails = new ErrorDetails("ARGUMENT_ERROR", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    public static class AlreadyExistException extends RuntimeException {
        public AlreadyExistException(String message) {
            super(message); // Appelle le constructeur de RuntimeException avec le message
        }
    }
    @ExceptionHandler(AlreadyExistException.class) //Exception du id entré non trouvé
    public ResponseEntity<ErrorDetails> handleAlreadyExistException(AlreadyExistException ex) {
        ErrorDetails errorDetails = new ErrorDetails("ARGUMENT_ERROR", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    public static class RoleNotFoundException extends RuntimeException {
        public RoleNotFoundException(String message) {
            super(message); // Appelle le constructeur de RuntimeException avec le message
        }
    }
    @ExceptionHandler(RoleNotFoundException.class) //Exception du role entré non trouvé
    public ResponseEntity<ErrorDetails> handleRoleNotFoundException(RoleNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails("ARGUMENT_ERROR", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeException.class) //Exception des pathVariables
    public ResponseEntity<ErrorDetails> handleDateTimeException(DateTimeException ex) {
    ErrorDetails errorDetails = new ErrorDetails("HEADER_ERROR", "Incorrect or Missing TimeZone input");
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    public static class PasswordLenghtException extends RuntimeException {
        public PasswordLenghtException(String message) {
            super(message); // Appelle le constructeur de RuntimeException avec le message
        }
    }
    @ExceptionHandler(PasswordLenghtException.class) //Exception du role entré non trouvé
    public ResponseEntity<ErrorDetails> handlePasswordLenghtException(PasswordLenghtException ex) {
        ErrorDetails errorDetails = new ErrorDetails("ARGUMENT_ERROR", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleAuthenticationException(Exception ex) {
        return new Result(false, StatusCode.UNAUTHORIZED, "username or password is incorrect.", ex.getMessage());
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        return new Result(false, StatusCode.UNAUTHORIZED, "Login credentials are missing.", ex.getMessage());
    }

    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleAccountStatusException(AccountStatusException ex) {
        return new Result(false, StatusCode.UNAUTHORIZED, "User account is abnormal.", ex.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return new Result(false, StatusCode.NOT_FOUND, "This API endpoint is not found.", ex.getMessage());
    }


}
/*public static class ObjectNullException extends RuntimeException {
        public ObjectNullException(String message) {
            super(message); // Appelle le constructeur de RuntimeException avec le message
        }
    }
    @ExceptionHandler(ObjectNullException.class) //Exception du objet entré non trouvé
    public ResponseEntity<ErrorDetails> handleObjectNullException(ObjectNullException ex) {
        ErrorDetails errorDetails = new ErrorDetails("INCORRECT_DATA", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    } */