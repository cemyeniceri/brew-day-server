package org.gsu.brewday.unit.contoller;

import org.gsu.brewday.domain.Ingredient;
import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.service.IngredientService;
import org.gsu.brewday.service.PrincipalService;
import org.gsu.brewday.web.IngredientController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@WebMvcTest(value = IngredientController.class, secure = false)
public class IngredientControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrincipalService principalService;

    @MockBean
    private IngredientService ingredientService;

   @Before public void setUp(){
       Principal principal = new Principal();
       principal.setUsername("User1");
       principal.setName("Name1");
       principal.setSurname("Surname1");
       principal.setEmail("email1");
       principal.setGsm("gsm");
       principal.setPassword("pasword1");
       principal.setTitle("title1");
       principal.setObjId("1");

       Ingredient ingredient1 = new Ingredient();
       ingredient1.setObjId("1");
       ingredient1.setPrincipal(principal);
       ingredient1.setName("ingredientName1");
       ingredient1.setType("ingredientType1");
       ingredient1.setUnit("ingredientUit1");
       ingredient1.setAmount("ingredientAmount1");

       Ingredient ingredient2 = new Ingredient();
       ingredient2.setObjId("2");
       ingredient2.setPrincipal(principal);
       ingredient2.setName("ingredientName2");
       ingredient2.setType("ingredientType2");
       ingredient2.setUnit("ingredientUit2");
       ingredient2.setAmount("ingredientAmount2");

       Set<Ingredient> ingredientSet = new HashSet<Ingredient>() {{
           add(ingredient1);
           add(ingredient2);
       }};
       principal.setIngredients(ingredientSet);

       Mockito.when(principalService.userLoggedOn(any())).thenReturn(principal);

       DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
       this.mockMvc = builder.build();
   }

    @Test
    public void testIngredientList() throws Exception {
       RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
               "/ingredients").accept(
                       MediaType.APPLICATION_JSON);

       MvcResult result = mockMvc.perform(requestBuilder).andReturn();
       System.out.println(result.getResponse());
       String jsonIngredientList = "[{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"ingredientAmount2\",\"objId\":\"2\"},{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":\"1\"}]";
       JSONAssert.assertEquals(jsonIngredientList, result.getResponse()
               .getContentAsString(), JSONCompareMode.STRICT);
    }

   @Test
    public void  testIngredientByObjId() throws Exception {
       Ingredient ingredient1 = new Ingredient();
       ingredient1.setObjId("1");
       ingredient1.setName("ingredientName1");
       ingredient1.setType("ingredientType1");
       ingredient1.setUnit("ingredientUit1");
       ingredient1.setAmount("ingredientAmount1");

       RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
               "/ingredients/1").accept(
                       MediaType.APPLICATION_JSON);
       Mockito.when(ingredientService.findByObjId("1")).thenReturn(Optional.of(ingredient1));
       MvcResult result = mockMvc.perform(requestBuilder).andReturn();

       String jsonIngredientList = "{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":\"1\"}";
       JSONAssert.assertEquals(jsonIngredientList, result.getResponse()
               .getContentAsString(), JSONCompareMode.STRICT);

       Mockito.when(ingredientService.findByObjId("1")).thenReturn(Optional.empty());
       requestBuilder = MockMvcRequestBuilders.get(
               "/ingredients/1").accept(
                       MediaType.APPLICATION_JSON);

       result = mockMvc.perform(requestBuilder).andReturn();
       Assert.assertTrue(result.getResponse().getContentAsString().contains("FAILURE"));
    }

    @Test
    public void testCreate() throws Exception {
       String jsonIngredient = "{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":\"1\"}";

       RequestBuilder requestBuilder = MockMvcRequestBuilders
               .post("/ingredients")
               .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
               .contentType(MediaType.APPLICATION_JSON);

       MvcResult result = mockMvc.perform(requestBuilder).andReturn();
       MockHttpServletResponse response = result.getResponse();
       assertEquals(HttpStatus.OK.value(), response.getStatus());

       jsonIngredient = "{\"type\":\"ingredientType1\",\"name\":\"\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":\"1\"}";

       requestBuilder = MockMvcRequestBuilders
               .post("/ingredients")
               .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
               .contentType(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        jsonIngredient = "{\"type\":\"ingredientType1\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":\"1\"}";
        requestBuilder = MockMvcRequestBuilders
                .post("/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    public void testUpdate() throws Exception {

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setObjId("1");
        ingredient1.setName("ingredientName1");
        ingredient1.setType("ingredientType1");
        ingredient1.setUnit("ingredientUit1");
        ingredient1.setAmount("ingredientAmount1");
        String jsonIngredient = "{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":\"1\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(ingredientService.findByObjId("1")).thenReturn(Optional.of(ingredient1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonIngredient = "{\"type\":\"ingredientType1\",\"name\":\"\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":\"1\"}";

        requestBuilder = MockMvcRequestBuilders
                .put("/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        jsonIngredient = "{\"type\":\"ingredientType1\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":\"1\"}";

        requestBuilder = MockMvcRequestBuilders
                .put("/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        jsonIngredient = "{\"type\":\"ingredientType1\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":null}";

        requestBuilder = MockMvcRequestBuilders
                .put("/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(ingredientService.findByObjId(null)).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    public void testDelete() throws Exception {
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setObjId("1");
        ingredient1.setName("ingredientName1");
        ingredient1.setType("ingredientType1");
        ingredient1.setUnit("ingredientUit1");
        ingredient1.setAmount("ingredientAmount1");

        String jsonIngredient = "{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":\"1\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/ingredients/1")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(ingredientService.findByObjId("1")).thenReturn(Optional.of(ingredient1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonIngredient = "{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"ingredientAmount1\",\"objId\":\"1\"}";

        requestBuilder = MockMvcRequestBuilders
                .delete("/ingredients/1")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(ingredientService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();

        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

}
