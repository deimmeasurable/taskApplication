package org.example.execptions;


import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(HttpStatus forbidden, String message) {
        super(message);
    }
}
