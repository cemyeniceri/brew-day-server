package org.gsu.brewday.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseStatus{
    SUCCESS("Success"),
    FAILURE("Failure");

    private String type;
}