package com.awesome.awesomenotes.authentication;

import org.springframework.http.HttpStatus;

public class AuthException extends Exception {
    public HttpStatus status = HttpStatus.UNAUTHORIZED;

    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable err) {
        super(message, err);
    }

    public AuthException(Throwable err) {
        super(err);
    }

    public AuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

}
