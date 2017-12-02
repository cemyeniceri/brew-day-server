package org.gsu.brewday.service.impl;

import lombok.RequiredArgsConstructor;
import org.gsu.brewday.domain.Recipe;
import org.gsu.brewday.domain.repository.RecipeRepository;
import org.gsu.brewday.exception.BrewDayException;
import org.gsu.brewday.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by cyeniceri on 02/12/2017.
 */
@Component("recipeService")
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    @Override
    public Recipe saveOrUpdate(Recipe recipe) {
        Optional<Recipe> ingredientDb = recipeRepository.findByNameAndPrincipal(recipe.getName(), recipe.getPrincipal());
        if(ingredientDb.isPresent() && !ingredientDb.get().getObjId().equals(recipe.getObjId())) {
            throw new BrewDayException("Name must be unique for " + recipe.getPrincipal().getUsername() + ". ");
        }
        return recipeRepository.save(recipe);
    }

    @Override
    public Optional<Recipe> findByObjId(String objId) {
        return Optional.ofNullable(recipeRepository.findOne(objId));
    }

    @Override
    public void delete(Recipe recipe) {
        recipeRepository.delete(recipe);
    }

}
