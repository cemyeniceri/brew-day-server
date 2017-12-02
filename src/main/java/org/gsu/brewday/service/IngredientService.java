package org.gsu.brewday.service;

import org.gsu.brewday.domain.Ingredient;

import java.util.Optional;

/**
 * Created by cyeniceri on 01/12/2017.
 */
public interface IngredientService {

    Ingredient saveOrUpdate(Ingredient ingredient);

    Optional<Ingredient> findByObjId(String objId);

    void delete(Ingredient ingredient);
}



