package org.gsu.brewday.unit.service;


import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.Recipe;
import org.gsu.brewday.domain.repository.RecipeRepository;
import org.gsu.brewday.dto.response.RecipeInfo;
import org.gsu.brewday.service.impl.RecipeServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private RecipeServiceImpl recipeService;


    @Before
    public void setUp(){
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();

        Principal principal1 = new Principal();
        principal1.setUsername("User1");
        principal1.setName("Name1");
        principal1.setSurname("Surname1");

        principal1.setEmail("email1");
        principal1.setGsm("gsm");
        principal1.setPassword("pasword1");
        principal1.setTitle("title1");
        principal1.setObjId("1");

        Principal principal2 = new Principal();
        principal2.setUsername("User1");
        principal2.setName("Name1");
        principal2.setSurname("Surname1");
        principal2.setEmail("email1");
        principal2.setGsm("gsm");
        principal2.setPassword("pasword1");
        principal2.setTitle("title1");
        principal2.setObjId("2");

        recipe1.setName("recipeName1");
        recipe2.setName("recipeName2");

        recipe1.setPrincipal(principal1);
        recipe2.setPrincipal(principal2);

        recipe1.setDetail("Detail1");
        recipe2.setDetail("Detail2");



        recipe1.setObjId("1");
        recipe2.setObjId("2");


        List<Recipe> principalList = new ArrayList<Recipe>() {{
            add(recipe1);
            add(recipe2);
        }};

        Mockito.when(recipepository.findAll()).thenReturn(principalList);
        Optional<Recipe> opt = Optional.of(recipe1);

        Mockito.when(recipepository.findByNameAndPrincipal("recipeName1",principal1)).thenReturn(opt);

        Mockito.when(recipepository.findOne("1")).thenReturn(recipe1);

        Mockito.when(recipepository.save((Recipe) any())).thenReturn(recipe1);

    }


    @Test
    public void testFindAll() {
        List<RecipeInfo> recipeInfos = recipeService.findAll();
        Assert.assertTrue(recipeInfos.get(0).getObjId().equalsIgnoreCase("1") &&
                recipeInfos.get(1).getObjId().equals("2"));
    }


    @Test
    public void findByObjId(){
        Recipe r = recipeService.findByObjId("1").get();
        Assert.assertTrue(r.getObjId().equals("1"));
        Assert.assertTrue(r.getName().equals("recipeName1"));
    }

    @Test
    public void testSaveOrUpdate(){
        Recipe recipe = new Recipe();
        recipe.setName("recipeName1");
        recipe.setObjId("1");

        Principal principal1 = new Principal();
        principal1.setUsername("User1");
        principal1.setName("Name1");
        principal1.setSurname("Surname1");
        principal1.setEmail("email1");
        principal1.setGsm("gsm");
        principal1.setPassword("pasword1");
        principal1.setTitle("title1");
        principal1.setObjId("1");

        recipe.setPrincipal(principal1);

        Recipe r = recipeService.saveOrUpdate(recipe);
        Assert.assertTrue(r.getObjId().equals("1"));
    }
}
