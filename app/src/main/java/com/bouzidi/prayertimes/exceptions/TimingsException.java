package com.bouzidi.prayertimes.exceptions;

public class TimingsException extends Throwable {

    public TimingsException(String message) {
        super(message);
    }

    public TimingsException(String message, Throwable cause) {
        super(message, cause);
    }
}
