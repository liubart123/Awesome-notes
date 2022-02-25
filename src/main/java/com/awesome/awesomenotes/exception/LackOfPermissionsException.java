package com.awesome.awesomenotes.exception;

import org.springframework.http.HttpStatus;

public class LackOfPermissionsException extends Exception {
    public HttpStatus status = HttpStatus.FORBIDDEN;

    public LackOfPermissionsException() {
        super();
    }

    public LackOfPermissionsException(String message) {
        super(message);
    }

    public LackOfPermissionsException(String message, Throwable err) {
        super(message, err);
    }

    public LackOfPermissionsException(Throwable err) {
        super(err);
    }

    public LackOfPermissionsException(String message, HttpStatus status) {
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
