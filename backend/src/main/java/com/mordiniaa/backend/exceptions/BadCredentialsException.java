package com.mordiniaa.backend.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadCredentialsException extends RuntimeException {

    private boolean status;

    public BadCredentialsException() {
        super("Bad Credentials");
        this.status = false;
    }

    public BadCredentialsException(String message) {
        super(message);
        this.status = false;
    }
}
