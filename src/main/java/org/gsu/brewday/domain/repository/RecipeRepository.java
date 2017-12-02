package org.gsu.brewday.domain.repository;

import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by cyeniceri on 02/12/2017.
 */
public interface RecipeRepository extends JpaRepository<Recipe, String> {
    Optional<Recipe> findByNameAndPrincipal(String name, Principal principal);
}
