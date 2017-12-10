package org.gsu.brewday.dto.response;

import lombok.Data;

@Data
public class PublicRecipeInfo extends RecipeInfo{
    private Boolean isImport;
}
