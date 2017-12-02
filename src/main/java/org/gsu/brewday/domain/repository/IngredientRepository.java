package org.gsu.brewday.domain.repository;

import org.gsu.brewday.domain.Ingredient;
import org.gsu.brewday.domain.Principal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by cyeniceri on 01/12/2017.
 */
public interface IngredientRepository extends JpaRepository<Ingredient, String> {

    Optional<Ingredient> findByTypeAndNameAndPrincipal(String type, String name, Principal principal);
}
