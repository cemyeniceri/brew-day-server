package org.gsu.brewday.domain.repository;

import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.Recipe;
import org.gsu.brewday.domain.ShopList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by cyeniceri on 03/12/2017.
 */
public interface ShopListRepository extends JpaRepository<ShopList, String> {
    Optional<ShopList> findByNameAndPrincipal(String name, Principal principal);
}
