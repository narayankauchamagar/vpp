package np.com.thapanarayan.vpp.exceptions.handlers;


import jakarta.servlet.http.HttpServletRequest;
import np.com.thapanarayan.vpp.dto.ErrorResponse;
import np.com.thapanarayan.vpp.dto.ServerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException ex, HttpServletRequest request) {

        Map<String, List<String>> errors = new HashMap<>();
        /* Handling field level Error */
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            collectFieldErrors(errors, fieldError);
        }

        /* Handling class level error */
        for (ObjectError objectError : ex.getBindingResult().getGlobalErrors()) {
            collectGlobalErrors(errors, objectError);
        }
        return new ResponseEntity<>(buildErrorResponse("Invalid Data", errors), BAD_REQUEST);
    }

    private void collectGlobalErrors(Map<String, List<String>> errors, ObjectError objectError) {
        List<String> errorMessages;
        if (errors.containsKey(objectError.getCode().toLowerCase())) {
            errorMessages = errors.get(objectError.getCode().toLowerCase());
            errorMessages.add(objectError.getDefaultMessage());
            errors.replace(objectError.getCode().toLowerCase(), errorMessages);
        } else {
            errors.put(objectError.getCode().toLowerCase(), new ArrayList<String>(Set.of(objectError.getDefaultMessage())));
        }
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handleHandlerException(HandlerMethodValidationException ex, HttpServletRequest request) {

        Map<String, List<String>> errorMap = new HashMap<>();

        for (ParameterValidationResult parameterValidationResult : ex.getParameterValidationResults()) {
           for(FieldError fieldError:  ((ParameterErrors) parameterValidationResult).getFieldErrors()) {
               collectFieldErrors(errorMap, fieldError);
           }
        }

        for (ParameterValidationResult parameterValidationResult : ex.getParameterValidationResults()) {
            /* Handling class level error */
            for (ObjectError objectError : ((ParameterErrors) parameterValidationResult).getGlobalErrors()) {
                collectGlobalErrors(errorMap, objectError);
            }
        }
        return new ResponseEntity<>(buildErrorResponse("Invalid Request - missing data in list", errorMap), BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllException(Exception ex, HttpServletRequest request) {
        ErrorResponse<String> errorResponse = ErrorResponse.<String>builder()
                .message("Some error occurred")
                .metadata(ex.getMessage())
                .build();
        return new ResponseEntity<>(buildErrorResponse("Invalid request", errorResponse ), BAD_REQUEST);
    }

    private void collectFieldErrors(Map<String, List<String>> errorMap, FieldError fieldError) {
        List<String> errorMessages;
        if (errorMap.containsKey(fieldError.getField().toLowerCase())) {
            errorMessages = errorMap.get(fieldError.getField().toLowerCase());
            errorMessages.add(fieldError.getDefaultMessage());
            errorMap.replace(fieldError.getField().toLowerCase(), errorMessages);
        } else {
            errorMap.put(fieldError.getField().toLowerCase(), new ArrayList<String>(Set.of(fieldError.getDefaultMessage())));
        }
    }

    private ServerResponse<?,?> buildErrorResponse(String message, Object object) {
        return ServerResponse
                .builder()
                .error(object)
                .message(message)
                .build();
    }


    private ServerResponse<?,?> buildErrorResponse(String message) {
        return ServerResponse
                .builder()
                .message(message)
                .build();
    }
}
