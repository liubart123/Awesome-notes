package com.awesome.awesomenotes.exception;

import org.springframework.http.HttpStatus;

public class ElementCreationException extends Exception {
    public HttpStatus status = HttpStatus.UNAUTHORIZED;

    public ElementCreationException() {
        super();
    }

    public ElementCreationException(String message) {
        super(message);
    }

    public ElementCreationException(String message, Throwable err) {
        super(message, err);
    }

    public ElementCreationException(Throwable err) {
        super(err);
    }

    public ElementCreationException(String message, HttpStatus status) {
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
