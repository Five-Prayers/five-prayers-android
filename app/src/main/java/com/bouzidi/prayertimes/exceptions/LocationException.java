package com.bouzidi.prayertimes.exceptions;

public class LocationException extends Throwable {

    public LocationException(String message) {
        super(message);
    }

    public LocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
