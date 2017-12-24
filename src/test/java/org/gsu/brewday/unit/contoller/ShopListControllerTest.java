package org.gsu.brewday.unit.contoller;


import org.gsu.brewday.domain.Ingredient;
import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.ShopList;
import org.gsu.brewday.domain.ShopListIngredient;
import org.gsu.brewday.service.PrincipalService;
import org.gsu.brewday.service.ShopListService;
import org.gsu.brewday.web.ShopListController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ShopListController.class, secure = false)
public class ShopListControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopListService shopListService;

    @MockBean
    private PrincipalService principalService;

    @Spy
    private ModelMapper modelMapper;


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

        ShopList shopList1 = new ShopList();
        shopList1.setName("ShoplistName1");
        shopList1.setObjId("ShopList1");
        shopList1.setPrincipal(principal);

        ShopList shopList2 = new ShopList();
        shopList2.setName("ShoplistName2");
        shopList2.setObjId("ShopList2");
        shopList2.setPrincipal(principal);

        ShopListIngredient shopListIngredient1 = new ShopListIngredient();
        shopListIngredient1.setShopList(shopList1);
        shopListIngredient1.setAmount("10");
        shopListIngredient1.setType("type1");
        shopListIngredient1.setUnit("unit1");
        shopListIngredient1.setName("shopIngName1");
        shopListIngredient1.setObjId("shopIng1");

        ShopListIngredient shopListIngredient2 = new ShopListIngredient();
        shopListIngredient2.setShopList(shopList2);
        shopListIngredient2.setAmount("5");
        shopListIngredient2.setType("type2");
        shopListIngredient2.setUnit("unit2");
        shopListIngredient2.setName("shopIngName2");
        shopListIngredient2.setObjId("shopIng2");

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("shopIngName1");
        ingredient1.setPrincipal(principal);
        ingredient1.setObjId("Ingredient1");
        ingredient1.setType("type1");
        ingredient1.setUnit("unit1");
        ingredient1.setAmount("3");

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("shopIngName2");
        ingredient2.setPrincipal(principal);
        ingredient2.setObjId("Ingredient3");
        ingredient2.setType("type2");
        ingredient2.setUnit("unit2");
        ingredient2.setAmount("4");


        Set<ShopList> shopListSet = new HashSet<ShopList>(){{
            add(shopList1);
            add(shopList2);
        }};

        Set<ShopListIngredient> shopListIngredientSet = new HashSet<ShopListIngredient>(){{
            add(shopListIngredient1);
            add(shopListIngredient2);
        }};

        Set<Ingredient> ingredientSet = new HashSet<Ingredient>(){{
           add(ingredient1);
           add(ingredient2);
        }};

        principal.setShopLists(shopListSet);
        principal.setIngredients(ingredientSet);

        shopList1.setShopListIngredients(shopListIngredientSet);

        Optional<ShopList> opt = Optional.of(shopList1);


        Mockito.when(principalService.userLoggedOn((HttpServletRequest) any())).thenReturn(principal);


        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();

    }

    @Test
    public void testShopLists() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/shop-lists").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        System.out.println(result.getResponse());
        String jsonShopListList = "[{\"name\":\"ShoplistName2\",\"objId\":\"ShopList2\",\"ingredientInfo\":null},{\"name\":\"ShoplistName1\",\"objId\":\"ShopList1\",\"ingredientInfo\":null}]";
        JSONAssert.assertEquals(jsonShopListList, result.getResponse()
                .getContentAsString(), JSONCompareMode.STRICT);
    }



    @Test
    public void testShopListByObjId() throws Exception{
        ShopList shopList1 = new ShopList();
        shopList1.setName("ShoplistName1");
        shopList1.setObjId("ShopList1");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/shop-lists/ShopList1").accept(
                MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());
        String jsonShopListList = "{\"name\":\"ShoplistName1\",\"objId\":\"ShopList1\",\"ingredientInfo\":null}";
        JSONAssert.assertEquals(jsonShopListList, result.getResponse()
                .getContentAsString(), JSONCompareMode.STRICT);

        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.empty());
        requestBuilder = MockMvcRequestBuilders.get(
                "/shop-lists/ShopList1").accept(
                MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        System.out.println(result.getResponse());
        Assert.assertTrue(result.getResponse().getContentAsString().contains("FAILURE"));
    }

    @Test
    public void testCreate() throws Exception{
        String jsonShopList = "{\"name\":\"ShoplistName1\",\"objId\":\"ShopList1\",\"ingredientInfo\":null}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/shop-lists")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonShopList = "{\"name\":\"\",\"objId\":\"ShopList1\",\"ingredientInfo\":null}";

        requestBuilder = MockMvcRequestBuilders
                .post("/shop-lists")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());


        jsonShopList = "{\"objId\":\"ShopList1\",\"ingredientInfo\":null}";
        requestBuilder = MockMvcRequestBuilders
                .post("/shop-lists")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    }

    @Test
    public void testUpdate() throws Exception{
        ShopList shopList1 = new ShopList();
        shopList1.setName("ShoplistName1");
        shopList1.setObjId("ShopList1");

        String jsonShopList = "{\"name\":\"ShoplistName1\",\"objId\":\"ShopList1\",\"ingredientInfo\":null}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/shop-lists")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonShopList = "{\"name\":\"\",\"objId\":\"ShopList1\",\"ingredientInfo\":null}";

        requestBuilder = MockMvcRequestBuilders
                .put("/shop-lists")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        jsonShopList = "{\"objId\":\"ShopList1\",\"ingredientInfo\":null}";

        requestBuilder = MockMvcRequestBuilders
                .put("/shop-lists")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        jsonShopList = "{\"name\":\"ShoplistName1\",\"objId\":null,\"ingredientInfo\":null}";

        requestBuilder = MockMvcRequestBuilders
                .put("/shop-lists")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId(null)).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testDelete() throws Exception{
        ShopList shopList1 = new ShopList();
        shopList1.setName("ShoplistName1");
        shopList1.setObjId("ShopList1");

        String jsonShopList = "{\"name\":\"ShoplistName1\",\"objId\":\"ShopList1\",\"ingredientInfo\":null}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/shop-lists/ShopList1")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());


        requestBuilder = MockMvcRequestBuilders
                .delete("/shop-lists/ShopList1")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();

        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    @Test
    public void testShopListIngredientList() throws Exception{
        ShopList shopList1 = new ShopList();
        shopList1.setName("ShoplistName1");
        shopList1.setObjId("ShopList1");

        ShopListIngredient shopListIngredient1 = new ShopListIngredient();
        shopListIngredient1.setShopList(shopList1);
        shopListIngredient1.setAmount("10");
        shopListIngredient1.setType("type1");
        shopListIngredient1.setUnit("unit1");
        shopListIngredient1.setName("shopIngName1");
        shopListIngredient1.setObjId("shopIng1");

        ShopListIngredient shopListIngredient2 = new ShopListIngredient();
        shopListIngredient2.setShopList(shopList1);
        shopListIngredient2.setAmount("5");
        shopListIngredient2.setType("type2");
        shopListIngredient2.setUnit("unit2");
        shopListIngredient2.setName("shopIngName2");
        shopListIngredient2.setObjId("shopIng2");



        Set<ShopListIngredient> shopListIngredientSet = new HashSet<ShopListIngredient>(){{
            add(shopListIngredient1);
            add(shopListIngredient2);
        }};


        shopList1.setShopListIngredients(shopListIngredientSet);

        String jsonShopList = "{\"name\":\"ShoplistName1\",\"objId\":\"ShopList1\",\"ingredientInfo\":null}";
        String jsonShopListIngredientsList = "[{\"type\":\"type2\",\"name\":\"shopIngName2\",\"unit\":\"unit2\",\"amount\":\"5\",\"objId\":\"shopIng2\"},{\"type\":\"type1\",\"name\":\"shopIngName1\",\"unit\":\"unit1\",\"amount\":\"10\",\"objId\":\"shopIng1\"}]";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/shop-lists/ShopList1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        JSONAssert.assertEquals(jsonShopListIngredientsList, result.getResponse()
                .getContentAsString(), JSONCompareMode.STRICT);


        requestBuilder = MockMvcRequestBuilders
                .get("/shop-lists/ShopList1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();

        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());


    }


    @Test
    public void testShopListIngredientByObjId() throws Exception{

        ShopList shopList1 = new ShopList();
        shopList1.setName("ShoplistName1");
        shopList1.setObjId("ShopList1");

        ShopListIngredient shopListIngredient1 = new ShopListIngredient();
        shopListIngredient1.setShopList(shopList1);
        shopListIngredient1.setAmount("10");
        shopListIngredient1.setType("type1");
        shopListIngredient1.setUnit("unit1");
        shopListIngredient1.setName("shopIngName1");
        shopListIngredient1.setObjId("shopIng1");

        ShopListIngredient shopListIngredient2 = new ShopListIngredient();
        shopListIngredient2.setShopList(shopList1);
        shopListIngredient2.setAmount("5");
        shopListIngredient2.setType("type2");
        shopListIngredient2.setUnit("unit2");
        shopListIngredient2.setName("shopIngName2");
        shopListIngredient2.setObjId("shopIng2");



        Set<ShopListIngredient> shopListIngredientSet = new HashSet<ShopListIngredient>(){{
            add(shopListIngredient1);
            add(shopListIngredient2);
        }};


        shopList1.setShopListIngredients(shopListIngredientSet);

        String jsonShopList = "{\"name\":\"ShoplistName1\",\"objId\":\"ShopList1\",\"ingredientInfo\":null,\"shopListIngredients\":[{\"type\":\"type2\",\"name\":\"shopIngName2\",\"unit\":\"unit2\",\"amount\":\"5\",\"objId\":\"shopIng2\"},{\"type\":\"type1\",\"name\":\"shopIngName1\",\"unit\":\"unit1\",\"amount\":\"10\",\"objId\":\"shopIng1\"}]}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/shop-lists/ShopList1/ingredients/shopIng2")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());


        requestBuilder = MockMvcRequestBuilders
                .get("/shop-lists/ShopList1/ingredients/shopIng2")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Shop List is Not Found"));


        requestBuilder = MockMvcRequestBuilders
                .get("/shop-lists/ShopList1/ingredients/shopIng27")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Ingredient is Not Found"));
    }

    @Test
    public void testCreateShopListIngredientByObjId() throws Exception{
        ShopList shopList1 = new ShopList();
        shopList1.setName("ShoplistName1");
        shopList1.setObjId("ShopList1");

        ShopListIngredient shopListIngredient1 = new ShopListIngredient();
        shopListIngredient1.setShopList(shopList1);
        shopListIngredient1.setAmount("10");
        shopListIngredient1.setType("type1");
        shopListIngredient1.setUnit("unit1");
        shopListIngredient1.setName("shopIngName1");
        shopListIngredient1.setObjId("shopIng1");

        ShopListIngredient shopListIngredient2 = new ShopListIngredient();
        shopListIngredient2.setShopList(shopList1);
        shopListIngredient2.setAmount("5");
        shopListIngredient2.setType("type2");
        shopListIngredient2.setUnit("unit2");
        shopListIngredient2.setName("shopIngName2");
        shopListIngredient2.setObjId("shopIng2");



        Set<ShopListIngredient> shopListIngredientSet = new HashSet<ShopListIngredient>(){{
            add(shopListIngredient1);
            add(shopListIngredient2);
        }};


        shopList1.setShopListIngredients(shopListIngredientSet);
        String jsonShopList = "{\"name\":\"ShoplistName1\",\"objId\":\"ShopList1\",\"ingredientInfo\":null,\"shopListIngredients\":[{\"type\":\"type2\",\"name\":\"shopIngName2\",\"unit\":\"unit2\",\"amount\":\"5\",\"objId\":\"shopIng2\"},{\"type\":\"type1\",\"name\":\"shopIngName1\",\"unit\":\"unit1\",\"amount\":\"10\",\"objId\":\"shopIng1\"}]}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/shop-lists/ShopList1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());


        requestBuilder = MockMvcRequestBuilders
                .post("/shop-lists/ShopList1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopList)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());



    }

    public void testUpdateShopListIngredientByObjId() throws Exception{
        ShopList shopList1 = new ShopList();
        shopList1.setName("ShoplistName1");
        shopList1.setObjId("ShopList1");

        ShopListIngredient shopListIngredient1 = new ShopListIngredient();
        shopListIngredient1.setShopList(shopList1);
        shopListIngredient1.setAmount("10");
        shopListIngredient1.setType("type1");
        shopListIngredient1.setUnit("unit1");
        shopListIngredient1.setName("shopIngName1");
        shopListIngredient1.setObjId("shopIng1");

        ShopListIngredient shopListIngredient2 = new ShopListIngredient();
        shopListIngredient2.setShopList(shopList1);
        shopListIngredient2.setAmount("5");
        shopListIngredient2.setType("type2");
        shopListIngredient2.setUnit("unit2");
        shopListIngredient2.setName("shopIngName2");
        shopListIngredient2.setObjId("shopIng2");



        Set<ShopListIngredient> shopListIngredientSet = new HashSet<ShopListIngredient>(){{
            add(shopListIngredient1);
            add(shopListIngredient2);
        }};


        shopList1.setShopListIngredients(shopListIngredientSet);
        String jsonShopListIngredient = "{\"type\":\"type1\",\"name\":\"shopIngName1\",\"unit\":\"unit1\",\"amount\":\"10\",\"objId\":\"shopIng1\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/shop-lists/ShopList1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopListIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonShopListIngredient = "{\"type\":\"type1\",\"name\":\"shopIngName5\",\"unit\":\"unit1\",\"amount\":\"10\",\"objId\":\"shopIng1\"}";
        requestBuilder = MockMvcRequestBuilders
                .put("/shop-lists/ShopList1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopListIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Shop List is Not Found"));

        jsonShopListIngredient = "{\"type\":\"type1\",\"name\":\"shopIngName1\",\"unit\":\"unit1\",\"amount\":\"10\",\"objId\":\"shopIng51\"}";
        requestBuilder = MockMvcRequestBuilders
                .put("/shop-lists/ShopList1/ingredients")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopListIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Ingredient is Not Found"));
    }


    public void testDeleteShopListIngredientByObjId() throws Exception{
        ShopList shopList1 = new ShopList();
        shopList1.setName("ShoplistName1");
        shopList1.setObjId("ShopList1");

        ShopListIngredient shopListIngredient1 = new ShopListIngredient();
        shopListIngredient1.setShopList(shopList1);
        shopListIngredient1.setAmount("10");
        shopListIngredient1.setType("type1");
        shopListIngredient1.setUnit("unit1");
        shopListIngredient1.setName("shopIngName1");
        shopListIngredient1.setObjId("shopIng1");

        ShopListIngredient shopListIngredient2 = new ShopListIngredient();
        shopListIngredient2.setShopList(shopList1);
        shopListIngredient2.setAmount("5");
        shopListIngredient2.setType("type2");
        shopListIngredient2.setUnit("unit2");
        shopListIngredient2.setName("shopIngName2");
        shopListIngredient2.setObjId("shopIng2");



        Set<ShopListIngredient> shopListIngredientSet = new HashSet<ShopListIngredient>(){{
            add(shopListIngredient1);
            add(shopListIngredient2);
        }};


        shopList1.setShopListIngredients(shopListIngredientSet);
        String jsonShopListIngredient = "{\"type\":\"type1\",\"name\":\"shopIngName5\",\"unit\":\"unit1\",\"amount\":\"10\",\"objId\":\"shopIng1\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/shop-lists/ShopList1/ingredients/shopIng1")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopListIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonShopListIngredient = "{\"type\":\"type1\",\"name\":\"shopIngName5\",\"unit\":\"unit1\",\"amount\":\"10\",\"objId\":\"\"}";
        requestBuilder = MockMvcRequestBuilders
                .delete("/shop-lists/ShopList1/ingredients/shopIng1")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopListIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Shop List is Not Found"));

        jsonShopListIngredient = "{\"type\":\"type1\",\"name\":\"shopIngName5\",\"unit\":\"unit1\",\"amount\":\"10\",\"objId\":\"shopIng51\"}";
        requestBuilder = MockMvcRequestBuilders
                .delete("/shop-lists/ShopList1/ingredients/shopIng1")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopListIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Ingredient is Not Found"));
    }


    @Test
    public void testDoneShopListByObjId() throws Exception{
        ShopList shopList1 = new ShopList();
        shopList1.setName("ShoplistName1");
        shopList1.setObjId("ShopList1");

        ShopListIngredient shopListIngredient1 = new ShopListIngredient();
        shopListIngredient1.setShopList(shopList1);
        shopListIngredient1.setAmount("10");
        shopListIngredient1.setType("type1");
        shopListIngredient1.setUnit("unit1");
        shopListIngredient1.setName("shopIngName1");
        shopListIngredient1.setObjId("shopIng1");

        ShopListIngredient shopListIngredient2 = new ShopListIngredient();
        shopListIngredient2.setShopList(shopList1);
        shopListIngredient2.setAmount("5");
        shopListIngredient2.setType("type2");
        shopListIngredient2.setUnit("unit2");
        shopListIngredient2.setName("shopIngName2");
        shopListIngredient2.setObjId("shopIng2");

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
        ingredient1.setName("shopIngName1");
        ingredient1.setPrincipal(principal);
        ingredient1.setObjId("Ingredient1");
        ingredient1.setType("type1");
        ingredient1.setUnit("unit1");
        ingredient1.setAmount("3");

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("shopIngName2");
        ingredient2.setPrincipal(principal);
        ingredient2.setObjId("Ingredient3");
        ingredient2.setType("type2");
        ingredient2.setUnit("unit2");
        ingredient2.setAmount("4");

        Set<ShopList> shopListSet = new HashSet<ShopList>(){{
            add(shopList1);
        }};

        Set<ShopListIngredient> shopListIngredientSet = new HashSet<ShopListIngredient>(){{
            add(shopListIngredient1);
            add(shopListIngredient2);
        }};

        Set<Ingredient> ingredientSet = new HashSet<Ingredient>(){{
            add(ingredient1);
            add(ingredient2);
        }};

        principal.setShopLists(shopListSet);
        principal.setIngredients(ingredientSet);

        shopList1.setShopListIngredients(shopListIngredientSet);
        String jsonShopListIngredient = "{\"type\":\"type1\",\"name\":\"shopIngName5\",\"unit\":\"unit1\",\"amount\":\"10\",\"objId\":\"shopIng1\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/shop-lists/ShopList1/done")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopListIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.of(shopList1));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        requestBuilder = MockMvcRequestBuilders
                .get("/shop-lists/ShopList1/done")
                .accept(MediaType.APPLICATION_JSON).content(jsonShopListIngredient)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(shopListService.findByObjId("ShopList1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


}
