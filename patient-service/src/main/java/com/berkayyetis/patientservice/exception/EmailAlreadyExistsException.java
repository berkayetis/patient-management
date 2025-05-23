package com.berkayyetis.patientservice.exception;

public class EmailAlreadyExistsException extends RuntimeException {  
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

}
