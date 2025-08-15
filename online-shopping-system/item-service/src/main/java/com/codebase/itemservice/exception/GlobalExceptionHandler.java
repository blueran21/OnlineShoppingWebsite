package com.codebase.itemservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handleConflictException(ConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
//    @ExceptionHandler(EntityNotFoundException.class)
//    public ResponseEntity<String> handleNotFound(EntityNotFoundException e) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//    }

    // handle not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }


    // handle duplicate upc
    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicate(org.springframework.dao.DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Item with same UPC already exists.");
    }

//    @ExceptionHandler(DuplicateKeyException.class)
//    public ResponseEntity<String> handleDuplicate(DuplicateKeyException ex) {
//        return ResponseEntity.status(HttpStatus.CONFLICT).body("Item with same UPC already exists.");
//    }

    // handle other error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
    }

}
