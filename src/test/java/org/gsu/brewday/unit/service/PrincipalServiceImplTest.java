package org.gsu.brewday.unit.service;


import io.jsonwebtoken.Claims;
import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.repository.PrincipalRepository;
import org.gsu.brewday.dto.response.PrincipalInfo;
import org.gsu.brewday.service.impl.PrincipalServiceImpl;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
@RunWith(SpringRunner.class)

public class PrincipalServiceImplTest {

    @Mock
    private PrincipalRepository principalRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private PrincipalServiceImpl principalService;


    @Before
    public void setUp() {
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
        List<Principal> principalList = new ArrayList<Principal>() {{
            add(principal1);
            add(principal2);
        }};

        Mockito.when(principalRepository.findAll()).thenReturn(principalList);
        Optional<Principal> opt = Optional.of(principal1);

        Mockito.when(principalRepository.findByUsernameAndPassword("User1","password1")).thenReturn(opt);

        Mockito.when(principalRepository.findOne("1")).thenReturn(principal1);

        Mockito.when(principalRepository.save((Principal) any())).thenReturn(principal1);

        Mockito.when(principalRepository.findByUsername("User1")).thenReturn(opt);


        Principal n = new Principal();

    }

   @Test
    public void testFindAll() {
        List<PrincipalInfo> principalInfos = principalService.findAll();
        Assert.assertTrue(principalInfos.get(0).getObjId().equalsIgnoreCase("1") &&
        principalInfos.get(1).getObjId().equals("2"));
    }

    @Test
    public void testFindByUsernameAndPassword(){
        Principal p = principalService.findByUsernameAndPassword("User1", "password1").get();
        Assert.assertTrue(p.getObjId().equals("1"));
    }


    @Test
    public void findByObjId(){
        Principal p = principalService.findByObjId("1").get();
        Assert.assertTrue(p.getObjId().equals("1"));
        Assert.assertTrue(p.getUsername().equals("User1"));
    }

    @Test
    public void testSaveOrUpdate(){
        Principal principal = new Principal();
        principal.setUsername("User1");
        principal.setName("Name1");
        principal.setSurname("principalSurname1");
        principal.setEmail("principalemail1");
        principal.setGsm("principalGsm");
        principal.setPassword("principalPasword1");
        principal.setTitle("principalTitle1");
        principal.setObjId("1");


        Principal p = principalService.saveOrUpdate(principal);
        Assert.assertTrue(p.getObjId().equals("1"));
    }

    @Test
    public void testUserLoggedOn(){
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        Claims claims = mock(Claims.class);
        Mockito.when(httpServletRequest.getAttribute("claims")).thenReturn(claims);
        Mockito.when(claims.get("oid")).thenReturn("1");
        Principal p = principalService.userLoggedOn(httpServletRequest);
        Assert.assertTrue(p.getObjId().equals("1"));
    }
}