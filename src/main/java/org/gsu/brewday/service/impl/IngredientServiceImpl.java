package org.gsu.brewday.service.impl;

import lombok.RequiredArgsConstructor;
import org.gsu.brewday.domain.Ingredient;
import org.gsu.brewday.domain.repository.IngredientRepository;
import org.gsu.brewday.exception.BrewDayException;
import org.gsu.brewday.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by cyeniceri on 01/12/2017.
 */
@Component("ingredientService")
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;

    @Override
    public Ingredient saveOrUpdate(Ingredient ingredient) {
        Optional<Ingredient> ingredientDb = ingredientRepository.findByTypeAndNameAndPrincipal(ingredient.getType(), ingredient.getName(), ingredient.getPrincipal());
        if(ingredientDb.isPresent() && !ingredientDb.get().getObjId().equals(ingredient.getObjId())) {
            throw new BrewDayException("Type and name must be unique for " + ingredient.getPrincipal().getUsername() + ". ");
        }
        return ingredientRepository.save(ingredient);
    }

    @Override
    public Optional<Ingredient> findByObjId(String objId) {
        return Optional.ofNullable(ingredientRepository.findOne(objId));
    }

    @Override
    public void delete(Ingredient ingredient) {
        ingredientRepository.delete(ingredient);
    }

}
