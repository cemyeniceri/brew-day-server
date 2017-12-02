package org.gsu.brewday.domain.repository;

import org.gsu.brewday.domain.ShopListIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by cyeniceri on 03/12/2017.
 */
public interface ShopListIngredientRepository extends JpaRepository<ShopListIngredient, String> {
}
