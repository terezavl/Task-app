package com.example.taskmanagment.controller;

import com.example.taskmanagment.model.DTOs.ErrorDTO;
import com.example.taskmanagment.model.exceptions.BadRequestException;
import com.example.taskmanagment.model.exceptions.NotFoundException;
import com.example.taskmanagment.model.exceptions.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(UnauthorizedException e ){
        final ErrorDTO errorDTO=new ErrorDTO(e.getMessage(),HttpStatus.UNAUTHORIZED.value(),LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException e ){
        final ErrorDTO errorDTO=new ErrorDTO(e.getMessage(),HttpStatus.NOT_FOUND.value(),LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException e ){
        final ErrorDTO errorDTO=new ErrorDTO(e.getMessage(),HttpStatus.BAD_REQUEST.value(),LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        final Map<String, String> errors = new HashMap<>();
        e.getBindingResult()
                .getAllErrors()
                .forEach((error) -> {
                    final String fieldName = ((FieldError) error).getField();
                    final String message = error.getDefaultMessage();
                    errors.put(fieldName, message);
                });
        final ErrorDTO errorDTO = new ErrorDTO(errors, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllOthers(Exception e ){
        ErrorDTO errorDTO=new ErrorDTO(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value(),LocalDateTime.now());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

}
