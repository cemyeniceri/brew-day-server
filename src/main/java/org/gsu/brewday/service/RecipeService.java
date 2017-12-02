package org.gsu.brewday.service;

import org.gsu.brewday.domain.Recipe;

import java.util.Optional;

/**
 * Created by cyeniceri on 02/12/2017.
 */
public interface RecipeService {

    Recipe saveOrUpdate(Recipe recipe);

    Optional<Recipe> findByObjId(String objId);

    void delete(Recipe recipe);
}



