package org.gsu.brewday.dto.response;

import lombok.Data;

@Data
public class ShopListInfo {
    private String name;
    private String objId;
    private IngredientInfo ingredientInfo;
}
