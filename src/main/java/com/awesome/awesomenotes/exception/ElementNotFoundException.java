package com.awesome.awesomenotes.exception;

import org.springframework.http.HttpStatus;

public class ElementNotFoundException extends Exception {
    public HttpStatus status = HttpStatus.NOT_FOUND;

    public ElementNotFoundException() {
        super();
    }

    public ElementNotFoundException(String message) {
        super(message);
    }

    public ElementNotFoundException(String message, Throwable err) {
        super(message, err);
    }

    public ElementNotFoundException(Throwable err) {
        super(err);
    }

    public ElementNotFoundException(String message, HttpStatus status) {
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