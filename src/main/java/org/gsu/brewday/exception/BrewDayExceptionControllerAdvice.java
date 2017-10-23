package org.gsu.brewday.exception;

import org.gsu.brewday.dto.response.ResponseInfo;
import org.gsu.brewday.dto.response.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by cyeniceri on 10/10/2017.
 */
@ControllerAdvice
@RestController
public class BrewDayExceptionControllerAdvice extends ResponseEntityExceptionHandler{

    private static final Logger LOG = LoggerFactory.getLogger(BrewDayExceptionControllerAdvice.class);

    @ExceptionHandler(BrewDayException.class)
    public ResponseEntity exception(BrewDayException e) {
        LOG.error("Error: {}", e.getMessage());
        return new ResponseEntity(new ResponseInfo(e.getMessage(), ResponseStatus.FAILURE), e.getHttpStatus());
    }
}
