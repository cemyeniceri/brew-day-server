package org.gsu.brewday.integration.domain;

import org.gsu.brewday.domain.Ingredient;
import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.repository.IngredientRepository;
import org.gsu.brewday.domain.repository.PrincipalRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IngredientRepositoryIT {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private PrincipalRepository principalRepository;

    private Principal principal;
    private Principal invalidPrincipal;

    private Ingredient ingredient;

    private String ingredientObjId;

    @Before
    public void setup() {
        principal = new Principal();
        principal.setUsername("user1");
        ingredient = new Ingredient();
        ingredient.setType("Type1");
        ingredient.setName("Name1");
        ingredient.setPrincipal(principal);
        principal.getIngredients().add(ingredient);
        principalRepository.save(principal);
        ingredientObjId = ingredient.getObjId();

        invalidPrincipal = new Principal();
        principalRepository.save(invalidPrincipal);
    }

    @After
    public void tearDown() throws Exception {
        principalRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    @Test
    public void findByTypeAndNameAndPrincipalValid() {
        Optional<Ingredient> ingredientOpt = ingredientRepository.findByTypeAndNameAndPrincipal("Type1", "Name1", principal);
        Assert.assertTrue(ingredientOpt.isPresent());
        Assert.assertTrue(ingredientOpt.get().getObjId().equals(ingredientObjId));
    }

    @Test
    public void findByTypeAndNameAndPrincipalInvalidType() {
        Optional<Ingredient> ingredientOpt = ingredientRepository.findByTypeAndNameAndPrincipal("InvalidType", "Name1", principal);
        Assert.assertFalse(ingredientOpt.isPresent());
    }

    @Test
    public void findByTypeAndNameAndPrincipalInvalidName() {
        Optional<Ingredient> ingredientOpt = ingredientRepository.findByTypeAndNameAndPrincipal("Type1", "InvalidName", principal);
        Assert.assertFalse(ingredientOpt.isPresent());
    }

    @Test
    public void findByTypeAndNameAndPrincipalInvalid() {
        Optional<Ingredient> ingredientOpt = ingredientRepository.findByTypeAndNameAndPrincipal("Type1", "Name1", invalidPrincipal);
        Assert.assertFalse(ingredientOpt.isPresent());
    }
}
