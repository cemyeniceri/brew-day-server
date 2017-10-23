package org.gsu.brewday.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by cyeniceri on 09/10/2017.
 */
public class BrewDayException extends RuntimeException {

    private HttpStatus httpStatus;

    public BrewDayException() {
        setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public BrewDayException(String message) {
        super(message);
        setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public BrewDayException(String message, HttpStatus httpStatus) {
        super(message);
        setHttpStatus(httpStatus);
    }

    public BrewDayException(Throwable throwable, HttpStatus httpStatus) {
        super(throwable);
        setHttpStatus(httpStatus);
    }

    public BrewDayException(String message, Throwable throwable, HttpStatus httpStatus) {
        super(message, throwable);
        setHttpStatus(httpStatus);
    }


    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
