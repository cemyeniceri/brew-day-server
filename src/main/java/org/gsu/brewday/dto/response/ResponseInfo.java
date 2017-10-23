package org.gsu.brewday.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseInfo {

    private String message;
    private ResponseStatus responseType;
}
