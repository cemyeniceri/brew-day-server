package org.gsu.brewday.unit.contoller;


import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.Recipe;
import org.gsu.brewday.dto.response.RecipeInfo;
import org.gsu.brewday.service.PrincipalService;
import org.gsu.brewday.service.RecipeService;
import org.gsu.brewday.web.PublicRecipeController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PublicRecipeController.class, secure = false)
public class PublicRecipeControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private PrincipalService principalService;


    @Before
    public void setUp(){
        Principal principal = new Principal();
        principal.setUsername("User1");
        principal.setName("Name1");
        principal.setSurname("Surname1");
        principal.setEmail("email1");
        principal.setGsm("gsm");
        principal.setPassword("pasword1");
        principal.setTitle("title1");
        principal.setObjId("1");

        RecipeInfo recipeInfo1 = new RecipeInfo();
        recipeInfo1.setPrincipal("2");
        recipeInfo1.setDetail("Detail1");
        recipeInfo1.setName("RecipeInfo1");
        recipeInfo1.setObjId("RI1");

        RecipeInfo recipeInfo2 = new RecipeInfo();
        recipeInfo2.setPrincipal("2");
        recipeInfo2.setDetail("Detail1");
        recipeInfo2.setName("RecipeInfo2");
        recipeInfo2.setObjId("RI2");
        List<RecipeInfo> recipeInfoList = new ArrayList<RecipeInfo>(){{
            add(recipeInfo1);
            add(recipeInfo2);
        }};


        Mockito.when(principalService.userLoggedOn(any())).thenReturn(principal);

        Mockito.when(recipeService.findAll( )).thenReturn(recipeInfoList);
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    public void testRecipeList() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/public-recipes").accept(
                MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testClone() throws Exception{
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                "/public-recipes/1").accept(
                MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        requestBuilder = MockMvcRequestBuilders.post(
                "/public-recipes/1").accept(
                MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
