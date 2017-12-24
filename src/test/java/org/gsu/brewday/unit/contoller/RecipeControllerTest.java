package org.gsu.brewday.unit.contoller;

import org.gsu.brewday.domain.*;
import org.gsu.brewday.service.PrincipalService;
import org.gsu.brewday.service.RecipeService;
import org.gsu.brewday.web.RecipeController;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@WebMvcTest(value = RecipeController.class, secure = false)
public class RecipeControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrincipalService principalService;

    @MockBean
    private RecipeService recipeService;

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

        RecipeIngredient ingredient1 = new RecipeIngredient();
        ingredient1.setObjId("ingredient1");
        ingredient1.setName("ingredientName1");
        ingredient1.setType("ingredientType1");
        ingredient1.setUnit("ingredientUit1");
        ingredient1.setAmount("10");

        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient2.setObjId("ingredient2");
        ingredient2.setName("ingredientName2");
        ingredient2.setType("ingredientType2");
        ingredient2.setUnit("ingredientUit2");
        ingredient2.setAmount("10");

        Ingredient Iingredient = new Ingredient();
        Iingredient.setAmount("4");
        Iingredient.setUnit("ingredientUit1");
        Iingredient.setType("ingredientType1");
        Iingredient.setName("ingredientName1");
        Iingredient.setObjId("1");
        Iingredient.setPrincipal(principal);

        Ingredient Iingredient2 = new Ingredient();
        Iingredient2.setAmount("5");
        Iingredient2.setUnit("ingredientUit2");
        Iingredient2.setType("ingredientType2");
        Iingredient2.setName("ingredientName2");
        Iingredient2.setObjId("2");
        Iingredient2.setPrincipal(principal);

        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setPrincipal(principal);
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");


        Recipe recipe2 = new Recipe();
        recipe2.setObjId("2");
        recipe2.setPrincipal(principal);
        recipe2.setName("Name2");
        recipe2.setDetail("Detail2");

        RecipePost recipePost = new RecipePost();
        recipePost.setRecipe(recipe1);
        recipePost.setPost("Post1");
        recipePost.setObjId("recipePost1");


        RecipePost recipePost2 = new RecipePost();
        recipePost2.setRecipe(recipe1);
        recipePost2.setPost("Post2");
        recipePost2.setObjId("recipePost2");


        Set<RecipeIngredient> ingredientSet = new HashSet<RecipeIngredient>() {{
            add(ingredient1);
            add(ingredient2);
        }};

        Set<Ingredient> IingredientSet = new HashSet<Ingredient>() {{
            add(Iingredient);
            add(Iingredient2);
        }};


        Set<Recipe> recipeSet = new HashSet<Recipe>() {{
            add(recipe1);
            add(recipe2);
        }};

        Set<RecipePost> postSet = new HashSet<RecipePost>() {{
            add(recipePost);
            add(recipePost2);
        }};

        principal.setRecipes(recipeSet);
        principal.setIngredients(IingredientSet);
        recipe1.setRecipeIngredients(ingredientSet);
        recipe1.setRecipePosts(postSet);

        Mockito.when(principalService.userLoggedOn(any())).thenReturn(principal);
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();

    }

    @Test
    public void testRecipeList() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/recipes").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        System.out.println(result.getResponse());
        String jsonRecipeList = "[{\"name\":\"Name2\",\"detail\":\"Detail2\",\"objId\":\"2\",\"principal\":\"1\"},{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":\"1\"}]";
        JSONAssert.assertEquals(jsonRecipeList, result.getResponse()
                .getContentAsString(), JSONCompareMode.STRICT);

    }

    @Test
    public void testRecipeByObjId() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/recipes/1").accept(
                MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());
        String jsonRecipeList = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";
        JSONAssert.assertEquals(jsonRecipeList, result.getResponse()
                .getContentAsString(), JSONCompareMode.STRICT);


        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        requestBuilder = MockMvcRequestBuilders.get(
                "/recipes/1").accept(
                MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        System.out.println(result.getResponse());
        Assert.assertTrue(result.getResponse().getContentAsString().contains("FAILURE"));
    }


   @Test
   public void testCreate() throws Exception {

       String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";
       RequestBuilder requestBuilder = MockMvcRequestBuilders
               .post("/recipes")
               .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
               .contentType(MediaType.APPLICATION_JSON);

       MvcResult result = mockMvc.perform(requestBuilder).andReturn();
       MockHttpServletResponse response = result.getResponse();
       assertEquals(HttpStatus.OK.value(), response.getStatus());


       jsonRecipe = "{\"name\":\"\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";
       requestBuilder = MockMvcRequestBuilders
               .post("/principals")
               .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
               .contentType(MediaType.APPLICATION_JSON);

       result = mockMvc.perform(requestBuilder).andReturn();
       response = result.getResponse();
       assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

       jsonRecipe = "{\"name\":null,\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";
       requestBuilder = MockMvcRequestBuilders
               .post("/principals")
               .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
               .contentType(MediaType.APPLICATION_JSON);

       result = mockMvc.perform(requestBuilder).andReturn();
       response = result.getResponse();
       assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());


       jsonRecipe = "{\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";
       requestBuilder = MockMvcRequestBuilders
               .post("/principals")
               .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
               .contentType(MediaType.APPLICATION_JSON);
       result = mockMvc.perform(requestBuilder).andReturn();
       response = result.getResponse();
       assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
   }


   @Test
   public void testUpdate() throws Exception{
       Recipe recipe1 = new Recipe();
       recipe1.setObjId("1");
       recipe1.setName("Name1");
       recipe1.setDetail("Detail1");

       String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";
       RequestBuilder requestBuilder = MockMvcRequestBuilders
               .put("/recipes")
               .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
               .contentType(MediaType.APPLICATION_JSON);

       Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
       MvcResult result = mockMvc.perform(requestBuilder).andReturn();
       MockHttpServletResponse response = result.getResponse();
       assertEquals(HttpStatus.OK.value(), response.getStatus());


       jsonRecipe = "{\"name\":\"\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";
       requestBuilder = MockMvcRequestBuilders
               .put("/recipes")
               .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
               .contentType(MediaType.APPLICATION_JSON);

       result = mockMvc.perform(requestBuilder).andReturn();
       response = result.getResponse();
       assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

       jsonRecipe = "{\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";
       requestBuilder = MockMvcRequestBuilders
               .put("/recipes")
               .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
               .contentType(MediaType.APPLICATION_JSON);

       result = mockMvc.perform(requestBuilder).andReturn();
       response = result.getResponse();
       assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

       jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":null,\"principal\":null}";
       requestBuilder = MockMvcRequestBuilders
               .put("/recipes")
               .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
               .contentType(MediaType.APPLICATION_JSON);
       Mockito.when(recipeService.findByObjId(null)).thenReturn(Optional.empty());
       result = mockMvc.perform(requestBuilder).andReturn();
       response = result.getResponse();
       assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
   }



   @Test
   public void testDelete() throws Exception{
       Recipe recipe1 = new Recipe();
       recipe1.setObjId("1");
       recipe1.setName("Name1");
       recipe1.setDetail("Detail1");
       String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";

       RequestBuilder requestBuilder = MockMvcRequestBuilders
               .delete("/recipes/1")
               .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
               .contentType(MediaType.APPLICATION_JSON);
       Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
       MvcResult result = mockMvc.perform(requestBuilder).andReturn();

       MockHttpServletResponse response = result.getResponse();
       assertEquals(HttpStatus.OK.value(), response.getStatus());

       jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";

       requestBuilder = MockMvcRequestBuilders
               .delete("/recipes/1")
               .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
               .contentType(MediaType.APPLICATION_JSON);
       Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
       result = mockMvc.perform(requestBuilder).andReturn();

       response = result.getResponse();
       assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

   }

    @Test
    public void testRecipePostList() throws Exception{
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipePost recipePost = new RecipePost();
        recipePost.setRecipe(recipe1);
        recipePost.setPost("Post1");
        recipePost.setObjId("recipePost1");


        RecipePost recipePost2 = new RecipePost();
        recipePost2.setRecipe(recipe1);
        recipePost2.setPost("Post2");
        recipePost2.setObjId("recipePost2");

        Set<RecipePost> postSet = new HashSet<RecipePost>() {{
            add(recipePost);
            add(recipePost2);
        }};
        recipe1.setRecipePosts(postSet);


        String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";
        String jsonRecipePostList = "[{\"post\":\"Post1\",\"objId\":\"recipePost1\"},{\"post\":\"Post2\",\"objId\":\"recipePost2\"}]";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/posts")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        JSONAssert.assertEquals(jsonRecipePostList, result.getResponse()
                .getContentAsString(), JSONCompareMode.STRICT);

        jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";

        requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/posts")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();

        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testRecipePostByObjId() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipePost recipePost = new RecipePost();
        recipePost.setRecipe(recipe1);
        recipePost.setPost("Post1");
        recipePost.setObjId("recipePost1");


        RecipePost recipePost2 = new RecipePost();
        recipePost2.setRecipe(recipe1);
        recipePost2.setPost("Post2");
        recipePost2.setObjId("recipePost2");

        Set<RecipePost> postSet = new HashSet<RecipePost>() {{
            add(recipePost);
            add(recipePost2);
        }};
        recipe1.setRecipePosts(postSet);

        String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipePosts\":[{\"post\":\"Post1\",\"objId\":\"recipePost1\"},{\"post\":\"Post2\",\"objId\":\"recipePost2\"}]}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/posts/recipePost1")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipePosts\":[{\"post\":\"Post1\",\"objId\":\"recipePost1\"},{\"post\":\"Post2\",\"objId\":\"recipePost2\"}]}";
        requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/posts/recipePost1")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Recipe is Not Found"));

        jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipePosts\":[{\"post\":\"Post1\",\"objId\":\"recipePost1\"},{\"post\":\"Post2\",\"objId\":\"recipePost2\"}]}";
        requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/posts/recipePost3")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Post is Not Found"));
    }

    @Test
    public void testCreateRecipePostByObjId() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipePost recipePost = new RecipePost();
        recipePost.setRecipe(null);
        recipePost.setPost("Post1");
        recipePost.setObjId("recipePost1");


        RecipePost recipePost2 = new RecipePost();
        recipePost2.setRecipe(null);
        recipePost2.setPost("Post2");
        recipePost2.setObjId("recipePost2");

        Set<RecipePost> postSet = new HashSet<RecipePost>() {{
            add(recipePost);
            add(recipePost2);
        }};
        recipe1.setRecipePosts(postSet);

        String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipePosts\":[{\"post\":\"Post1\",\"objId\":\"recipePost1\"},{\"post\":\"Post2\",\"objId\":\"recipePost2\"}]}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/recipes/1/posts")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipePosts\":[{\"post\":\"Post1\",\"objId\":\"recipePost1\"},{\"post\":\"Post2\",\"objId\":\"recipePost2\"}]}";
        requestBuilder = MockMvcRequestBuilders
                .post("/recipes/1/posts")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());


    }
    @Test
    public void testUpdateRecipePostByObjId() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipePost recipePost = new RecipePost();
        recipePost.setRecipe(recipe1);
        recipePost.setPost("Post1");
        recipePost.setObjId("recipePost1");


        RecipePost recipePost2 = new RecipePost();
        recipePost2.setRecipe(recipe1);
        recipePost2.setPost("Post2");
        recipePost2.setObjId("recipePost2");

        Set<RecipePost> postSet = new HashSet<RecipePost>() {{
            add(recipePost);
            add(recipePost2);
        }};
        recipe1.setRecipePosts(postSet);

        //String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipePosts\":[{\"post\":\"Post1\",\"objId\":\"recipePost1\"},{\"post\":\"Post1\",\"objId\":\"recipePost2\"}]}";
        String jsonPosts = "{\"post\":\"Post4\",\"objId\":\"recipePost1\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/recipes/1/posts")
                .accept(MediaType.APPLICATION_JSON).content(jsonPosts)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonPosts = "{\"post\":\"Post4\",\"objId\":\"recipePost1\"}";
        requestBuilder = MockMvcRequestBuilders
                .put("/recipes/1/posts")
                .accept(MediaType.APPLICATION_JSON).content(jsonPosts)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Recipe is Not Found"));

        jsonPosts = "{\"post\":\"Post4\",\"objId\":\"recipePos51\"}";
        requestBuilder = MockMvcRequestBuilders
                .put("/recipes/1/posts")
                .accept(MediaType.APPLICATION_JSON).content(jsonPosts)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Post is Not Found"));
    }


    @Test
    public void testDeleteRecipePostByObjId() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipePost recipePost = new RecipePost();
        recipePost.setRecipe(recipe1);
        recipePost.setPost("Post1");
        recipePost.setObjId("recipePost1");


        RecipePost recipePost2 = new RecipePost();
        recipePost2.setRecipe(recipe1);
        recipePost2.setPost("Post2");
        recipePost2.setObjId("recipePost2");

        Set<RecipePost> postSet = new HashSet<RecipePost>() {{
            add(recipePost);
            add(recipePost2);
        }};
        recipe1.setRecipePosts(postSet);

        //String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipePosts\":[{\"post\":\"Post1\",\"objId\":\"recipePost1\"},{\"post\":\"Post1\",\"objId\":\"recipePost2\"}]}";
        String jsonPosts = "{\"post\":\"Post4\",\"objId\":\"recipePost1\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/recipes/1/posts/recipePost1")
                .accept(MediaType.APPLICATION_JSON).content(jsonPosts)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonPosts = "{\"post\":\"Post4\",\"objId\":\"recipePost1\"}";
        requestBuilder = MockMvcRequestBuilders
                .delete("/recipes/1/posts/recipePost1")
                .accept(MediaType.APPLICATION_JSON).content(jsonPosts)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Recipe is Not Found"));

        jsonPosts = "{\"post\":\"Post4\",\"objId\":\"recipePos51\"}";
        requestBuilder = MockMvcRequestBuilders
                .delete("/recipes/1/posts/recipePost1")
                .accept(MediaType.APPLICATION_JSON).content(jsonPosts)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Post is Not Found"));

    }

    @Test
    public void testRecipeIngredientList() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipeIngredient ingredient1 = new RecipeIngredient();
        ingredient1.setObjId("ingredient1");
        ingredient1.setName("ingredientName1");
        ingredient1.setType("ingredientType1");
        ingredient1.setUnit("ingredientUit1");
        ingredient1.setAmount("10");

        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient2.setObjId("ingredient2");
        ingredient2.setName("ingredientName2");
        ingredient2.setType("ingredientType2");
        ingredient2.setUnit("ingredientUit2");
        ingredient2.setAmount("10");

        Set<RecipeIngredient> ingredientSet = new HashSet<RecipeIngredient>() {{
            add(ingredient1);
            add(ingredient2);
        }};
        recipe1.setRecipeIngredients(ingredientSet);


        String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";
        String jsonRecipeIngredientList = "[{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient2\"},{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"10\",\"objId\":\"ingredient1\"}]";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        JSONAssert.assertEquals(jsonRecipeIngredientList, result.getResponse()
                .getContentAsString(), JSONCompareMode.STRICT);

        jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null}";

        requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();

        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testRecipeIngredientByObjId() throws Exception{
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipeIngredient ingredient1 = new RecipeIngredient();
        ingredient1.setObjId("ingredient1");
        ingredient1.setName("ingredientName1");
        ingredient1.setType("ingredientType1");
        ingredient1.setUnit("ingredientUit1");
        ingredient1.setAmount("10");

        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient2.setObjId("ingredient2");
        ingredient2.setName("ingredientName2");
        ingredient2.setType("ingredientType2");
        ingredient2.setUnit("ingredientUit2");
        ingredient2.setAmount("10");

        Set<RecipeIngredient> ingredientSet = new HashSet<RecipeIngredient>() {{
            add(ingredient1);
            add(ingredient2);
        }};
        recipe1.setRecipeIngredients(ingredientSet);



        String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipeIngredients\":[{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient2\"},{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"10\",\"objId\":\"ingredient1\"}]}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/ingredients/ingredient1")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipeIngredients\":[{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient2\"},{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"10\",\"objId\":\"ingredient1\"}]}";
        requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/ingredients/ingredient1")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Recipe is Not Found"));

        jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipeIngredients\":[{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient2\"},{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"10\",\"objId\":\"ingredient1\"}]}";
        requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/ingredients/ingredient5")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Ingredient is Not Found"));
    }

    @Test
    public void testCreateRecipeIngredientByObjId() throws Exception{
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipeIngredient ingredient1 = new RecipeIngredient();
        ingredient1.setObjId("ingredient1");
        ingredient1.setName("ingredientName1");
        ingredient1.setType("ingredientType1");
        ingredient1.setUnit("ingredientUit1");
        ingredient1.setAmount("10");

        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient2.setObjId("ingredient2");
        ingredient2.setName("ingredientName2");
        ingredient2.setType("ingredientType2");
        ingredient2.setUnit("ingredientUit2");
        ingredient2.setAmount("10");

        Set<RecipeIngredient> ingredientSet = new HashSet<RecipeIngredient>() {{
            add(ingredient1);
            add(ingredient2);
        }};
        recipe1.setRecipeIngredients(ingredientSet);



        String jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipeIngredients\":[{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient2\"},{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"10\",\"objId\":\"ingredient1\"}]}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/recipes/1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonRecipe = "{\"name\":\"Name1\",\"detail\":\"Detail1\",\"objId\":\"1\",\"principal\":null,\"recipeIngredients\":[{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient2\"},{\"type\":\"ingredientType1\",\"name\":\"ingredientName1\",\"unit\":\"ingredientUit1\",\"amount\":\"10\",\"objId\":\"ingredient1\"}]}";
        requestBuilder = MockMvcRequestBuilders
                .post("/recipes/1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonRecipe)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testUpdateRecipeIngredientByObjId() throws Exception{
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipeIngredient ingredient1 = new RecipeIngredient();
        ingredient1.setObjId("ingredient1");
        ingredient1.setName("ingredientName1");
        ingredient1.setType("ingredientType1");
        ingredient1.setUnit("ingredientUit1");
        ingredient1.setAmount("10");

        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient2.setObjId("ingredient2");
        ingredient2.setName("ingredientName2");
        ingredient2.setType("ingredientType2");
        ingredient2.setUnit("ingredientUit2");
        ingredient2.setAmount("10");

        Set<RecipeIngredient> ingredientSet = new HashSet<RecipeIngredient>() {{
            add(ingredient1);
            add(ingredient2);
        }};
        recipe1.setRecipeIngredients(ingredientSet);

        String jsonIngredient = "{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient2\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/recipes/1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonIngredient = "{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient2\"}";
        requestBuilder = MockMvcRequestBuilders
                .put("/recipes/1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Recipe is Not Found"));

        jsonIngredient = "{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient22\"}";
        requestBuilder = MockMvcRequestBuilders
                .put("/recipes/1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Ingredient is Not Found"));
    }

    @Test
    public void testDeleteRecipeIngredientByObjId() throws Exception{
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipeIngredient ingredient1 = new RecipeIngredient();
        ingredient1.setObjId("ingredient1");
        ingredient1.setName("ingredientName1");
        ingredient1.setType("ingredientType1");
        ingredient1.setUnit("ingredientUit1");
        ingredient1.setAmount("10");

        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient2.setObjId("ingredient2");
        ingredient2.setName("ingredientName2");
        ingredient2.setType("ingredientType2");
        ingredient2.setUnit("ingredientUit2");
        ingredient2.setAmount("10");

        Set<RecipeIngredient> ingredientSet = new HashSet<RecipeIngredient>() {{
            add(ingredient1);
            add(ingredient2);
        }};
        recipe1.setRecipeIngredients(ingredientSet);

        String jsonIngredient = "{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient2\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/recipes/1/ingredients/ingredient2")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonIngredient = "{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient2\"}";
        requestBuilder = MockMvcRequestBuilders
                .delete("/recipes/1/ingredients/ingredient2")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Recipe is Not Found"));

        jsonIngredient = "{\"type\":\"ingredientType2\",\"name\":\"ingredientName2\",\"unit\":\"ingredientUit2\",\"amount\":\"10\",\"objId\":\"ingredient25\"}";
        requestBuilder = MockMvcRequestBuilders
                .delete("/recipes/1/ingredients/ingredient2")
                .accept(MediaType.APPLICATION_JSON).content(jsonIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Ingredient is Not Found"));
    }

    @Test
    public void testcheckIsRecipeAvailableByObjId() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipeIngredient ingredient1 = new RecipeIngredient();
        ingredient1.setObjId("ingredient1");
        ingredient1.setName("ingredientName1");
        ingredient1.setType("ingredientType1");
        ingredient1.setUnit("ingredientUit1");
        ingredient1.setAmount("10");

        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient2.setObjId("ingredient2");
        ingredient2.setName("ingredientName2");
        ingredient2.setType("ingredientType2");
        ingredient2.setUnit("ingredientUit2");
        ingredient2.setAmount("10");

        Set<RecipeIngredient> ingredientSet = new HashSet<RecipeIngredient>() {{
            add(ingredient1);
            add(ingredient2);
        }};
        recipe1.setRecipeIngredients(ingredientSet);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/check")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue("{\"ingredient2\":{\"amount\":\"5\",\"state\":false},\"ingredient1\":{\"amount\":\"6\",\"state\":false}}".equals(response.getContentAsString()));

        requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/check")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Recipe is Not Found"));

        recipe1.setRecipeIngredients(new HashSet<>());
        requestBuilder = MockMvcRequestBuilders
                .get("/recipes/1/check")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Recipe Ingredient List is Empty"));
    }

    @Test
    public void testWhatShouldIBrewToday() throws Exception{
        Recipe recipe1 = new Recipe();
        recipe1.setObjId("1");
        recipe1.setName("Name1");
        recipe1.setDetail("Detail1");

        RecipeIngredient ingredient1 = new RecipeIngredient();
        ingredient1.setObjId("ingredient1");
        ingredient1.setName("ingredientName1");
        ingredient1.setType("ingredientType1");
        ingredient1.setUnit("ingredientUit1");
        ingredient1.setAmount("10");

        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient2.setObjId("ingredient2");
        ingredient2.setName("ingredientName2");
        ingredient2.setType("ingredientType2");
        ingredient2.setUnit("ingredientUit2");
        ingredient2.setAmount("10");

        Set<RecipeIngredient> ingredientSet = new HashSet<RecipeIngredient>() {{
            add(ingredient1);
            add(ingredient2);
        }};
        recipe1.setRecipeIngredients(ingredientSet);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/recipes/what-should-i-brew-today")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(recipeService.findByObjId("1")).thenReturn(Optional.of(recipe1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("{\"2\":{\"name\":\"Name2\",\"detail\":\"Detail2\",\"objId\":\"2\",\"principal\":\"1\"}}",response.getContentAsString());


        requestBuilder = MockMvcRequestBuilders
                .get("/recipes/what-should-i-brew-today")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        Principal p = new Principal();
        p.setRecipes(new HashSet<>());
        Mockito.when(principalService.userLoggedOn(any())).thenReturn(p);
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
