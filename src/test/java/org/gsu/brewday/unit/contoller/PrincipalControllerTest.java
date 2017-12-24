package org.gsu.brewday.unit.contoller;


import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.dto.response.PrincipalInfo;
import org.gsu.brewday.service.PrincipalService;
import org.gsu.brewday.web.PrincipalController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;


@RunWith(SpringRunner.class)
@WebMvcTest(value = PrincipalController.class, secure = false)
public class PrincipalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrincipalService principalService;

    @Mock
    private PrincipalService principalService2;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() {
        PrincipalInfo principalInfo1 = new PrincipalInfo();
        principalInfo1.setObjId("1");
        principalInfo1.setName("Name1");
        principalInfo1.setEmail("Email1");
        principalInfo1.setGsm("Gsm1");
        principalInfo1.setPassword("Password1");
        principalInfo1.setSurname("Surname1");
        principalInfo1.setTitle("Title1");
        principalInfo1.setUsername("UserName1");


        PrincipalInfo principalInfo2 = new PrincipalInfo();
        principalInfo2.setObjId("2");
        principalInfo2.setName("Name2");
        principalInfo2.setEmail("Email2");
        principalInfo2.setGsm("Gsm2");
        principalInfo2.setPassword("Password2");
        principalInfo2.setSurname("Surname2");
        principalInfo2.setTitle("Title2");
        principalInfo2.setUsername("UserName2");

        Principal principal = new Principal();
        principal.setUsername("User1");
        principal.setName("Name1");
        principal.setSurname("Surname1");
        principal.setEmail("email1");
        principal.setGsm("gsm");
        principal.setPassword("pasword1");
        principal.setTitle("title1");
        principal.setObjId("1");



        List<PrincipalInfo> prinipalInfoList = new ArrayList<PrincipalInfo>() {{
            add(principalInfo1);
            add(principalInfo2);
        }};


        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();

        Mockito.when(principalService.findAll()).thenReturn(prinipalInfoList);

        doNothing().when(principalService).delete(principal);

        Mockito.when(
                principalService.findAll()).thenReturn(prinipalInfoList);

        Mockito.when(
                principalService.findAll()).thenReturn(prinipalInfoList);

        Mockito.when(principalService2.saveOrUpdate((Principal) any())).thenReturn(principal);


    }

    @Test
    public void testLogin() throws Exception {

        Principal principal = new Principal();
        principal.setUsername("User");
        principal.setName("Name1");
        principal.setSurname("Surname1");
        principal.setEmail("email1");
        principal.setGsm("gsm");
        principal.setPassword("Password");
        principal.setTitle("title1");
        principal.setObjId("1");
        Optional<Principal> opt = Optional.of(principal);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/principals/login").param("admin","admin").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Mockito.when(principalService.findByUsernameAndPassword("User","Password")).thenReturn(opt);



    }

    @Test
    public void testPrincipals() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/principals").header("Bearer", "").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        System.out.println(result.getResponse());
        String jsonPrincipalList = "[{\"username\":\"UserName1\",\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\",\"objId\":\"1\"}," +
                "{\"username\":\"UserName2\",\"name\":\"Name2\",\"surname\":\"Surname2\",\"email\":\"Email2\",\"gsm\":\"Gsm2\",\"password\":\"Password2\",\"title\":\"Title2\",\"objId\":\"2\"}]";
        JSONAssert.assertEquals(jsonPrincipalList, result.getResponse()
                .getContentAsString(), JSONCompareMode.STRICT);

    }

    @Test
    public void testCreate() throws Exception {
        Principal principal = new Principal();
        principal.setUsername("User1");
        principal.setName("Name1");
        principal.setSurname("Surname1");
        principal.setEmail("email1");
        principal.setGsm("gsm");
        principal.setPassword("pasword1");
        principal.setTitle("title1");
        principal.setObjId("1");

        String jsonPrincipal = "{\"username\":\"UserName1\",\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\",\"objId\":\"\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/principals")
                .accept(MediaType.APPLICATION_JSON).content(jsonPrincipal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonPrincipal = "{\"username\":\"\",\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\",\"objId\":\"\"}";

        requestBuilder = MockMvcRequestBuilders
                .post("/principals")
                .accept(MediaType.APPLICATION_JSON).content(jsonPrincipal)
                .contentType(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        jsonPrincipal = "{\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\",\"objId\":\"\"}";
        requestBuilder = MockMvcRequestBuilders
                .post("/principals")
                .accept(MediaType.APPLICATION_JSON).content(jsonPrincipal)
                .contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testUpdate() throws Exception {

        Principal principal = new Principal();
        principal.setUsername("User1");
        principal.setName("Name1");
        principal.setSurname("Surname1");
        principal.setEmail("email1");
        principal.setGsm("gsm");
        principal.setPassword("pasword1");
        principal.setTitle("title1");
        principal.setObjId("1");

        String jsonPrincipal = "{\"username\":\"UserName1\",\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\",\"objId\":\"1\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/principals")
                .accept(MediaType.APPLICATION_JSON).content(jsonPrincipal)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(principalService.findByObjId("1")).thenReturn(Optional.of(principal));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

       jsonPrincipal = "{\"username\":\"\",\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\",\"objId\":\"1\"}";

        requestBuilder = MockMvcRequestBuilders
                .put("/principals")
                .accept(MediaType.APPLICATION_JSON).content(jsonPrincipal)
                .contentType(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        jsonPrincipal = "{\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\",\"objId\":\"1\"}";

        requestBuilder = MockMvcRequestBuilders
                .put("/principals")
                .accept(MediaType.APPLICATION_JSON).content(jsonPrincipal)
                .contentType(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        jsonPrincipal = "{\"username\":\"UserName1\",\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\",\"objId\":\"\"}";

        requestBuilder = MockMvcRequestBuilders
                .put("/principals")
                .accept(MediaType.APPLICATION_JSON).content(jsonPrincipal)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(principalService.findByObjId("")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        jsonPrincipal = "{\"username\":\"UserName1\",\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\"}";

        requestBuilder = MockMvcRequestBuilders
                .put("/principals")
                .accept(MediaType.APPLICATION_JSON).content(jsonPrincipal)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(principalService.findByObjId(null)).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    @Test
    public void testDelete() throws Exception {

        Principal principal = new Principal();
        principal.setUsername("User1");
        principal.setName("Name1");
        principal.setSurname("Surname1");
        principal.setEmail("email1");
        principal.setGsm("gsm");
        principal.setPassword("pasword1");
        principal.setTitle("title1");
        principal.setObjId("1");

        String jsonPrincipal = "{\"username\":\"UserName1\",\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\",\"objId\":\"1\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/principals/1")
                .accept(MediaType.APPLICATION_JSON).content(jsonPrincipal)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(principalService.findByObjId("1")).thenReturn(Optional.of(principal));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        jsonPrincipal = "{\"username\":\"UserName1\",\"name\":\"Name1\",\"surname\":\"Surname1\",\"email\":\"Email1\",\"gsm\":\"Gsm1\",\"password\":\"Password1\",\"title\":\"Title1\",\"objId\":\"1\"}";

        requestBuilder = MockMvcRequestBuilders
                .delete("/principals/1")
                .accept(MediaType.APPLICATION_JSON).content(jsonPrincipal)
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.when(principalService.findByObjId("1")).thenReturn(Optional.empty());
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }


}
