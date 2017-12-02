package org.gsu.brewday.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class IngredientInfo implements Serializable{
    private String type;
    private String name;
    private String unit;
    private String amount;
    private String objId;
}
