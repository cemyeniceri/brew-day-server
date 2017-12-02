package org.gsu.brewday.domain.repository;

import org.gsu.brewday.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by cyeniceri on 02/12/2017.
 */
public interface RecipeRepository extends JpaRepository<Recipe, String> {
}
