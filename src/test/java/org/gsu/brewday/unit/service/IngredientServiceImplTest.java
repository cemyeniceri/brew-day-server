package org.gsu.brewday.unit.service;

import org.gsu.brewday.domain.Ingredient;
import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.repository.IngredientRepository;
import org.gsu.brewday.service.impl.IngredientServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.Matchers.any;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IngredientServiceImplTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    @Before
    public void setUp(){
        Ingredient ingredient = new Ingredient();
        ingredient.setObjId("1");
        ingredient.setName("ingredientName1");
        ingredient.setAmount("ingredientAmount1");
        ingredient.setType("ingredientType1");
        ingredient.setUnit("ingredientUnit1");

        Principal principal = new Principal();
        principal.setUsername("principalUser1");
        principal.setName("principalName1");
        principal.setSurname("principalSurname1");
        principal.setEmail("principalemail1");
        principal.setGsm("principalGsm");
        principal.setPassword("principalPasword1");
        principal.setTitle("principalTitle1");
        principal.setObjId("principal1");

        ingredient.setPrincipal(principal);
        Optional<Ingredient> opt = Optional.of(ingredient);

        Mockito.when(ingredientRepository.findByTypeAndNameAndPrincipal("ingredientType1","ingredientName1",principal))
                .thenReturn(opt);

        Mockito.when(ingredientRepository.save((Ingredient)any())).thenReturn(ingredient);

        Mockito.when(ingredientRepository.findOne("1")).thenReturn(ingredient);
    }

    @Test
    public void testFindByObjId(){
        Ingredient i = ingredientService.findByObjId("1").get();
        Assert.assertTrue(i.getObjId().equals("1"));
        Assert.assertTrue(i.getName().equals("ingredientName1"));
        Assert.assertTrue(i.getPrincipal() != null);
        Assert.assertTrue(i.getPrincipal().getObjId().equals("principal1"));

    }

    @Test
    public void testSaveOrUpdate(){
        Ingredient tmp = new Ingredient();
        tmp.setType("ingredientType1");
        tmp.setName("ingredientName1");
        tmp.setObjId("1");

        Principal principal = new Principal();
        principal.setUsername("principalUser1");
        principal.setName("principalName1");
        principal.setSurname("principalSurname1");
        principal.setEmail("principalemail1");
        principal.setGsm("principalGsm");
        principal.setPassword("principalPasword1");
        principal.setTitle("principalTitle1");
        principal.setObjId("principal1");
        tmp.setPrincipal(principal);
        Ingredient i = ingredientService.saveOrUpdate(tmp);
        Assert.assertTrue(i.getObjId().equals("1"));
    }

    //TODO
    @Test
    public void testDelete(){
        Ingredient i = new Ingredient();

    }

}
