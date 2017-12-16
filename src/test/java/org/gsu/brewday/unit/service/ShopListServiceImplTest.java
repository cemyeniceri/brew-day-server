package org.gsu.brewday.unit.service;

import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.ShopList;
import org.gsu.brewday.domain.repository.ShopListRepository;
import org.gsu.brewday.service.impl.ShopListServiceImpl;
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
public class ShopListServiceImplTest {
    @Mock
    private ShopListRepository shopListRepository;


    @InjectMocks
    private ShopListServiceImpl shopListService;


    @Before
    public void setUp() {
        Principal principal = new Principal();
        principal.setUsername("principalUser1");
        principal.setName("principalName1");
        principal.setSurname("principalSurname1");
        principal.setEmail("principalemail1");
        principal.setGsm("principalGsm");
        principal.setPassword("principalPasword1");
        principal.setTitle("principalTitle1");
        principal.setObjId("principal1");

        ShopList shopList = new ShopList();
        shopList.setName("Name1");
        shopList.setPrincipal(principal);
        shopList.setObjId("1");


        Optional<ShopList> opt = Optional.of(shopList);

        Mockito.when(shopListRepository.findByNameAndPrincipal("Name1", principal))
                .thenReturn(opt);

        Mockito.when(shopListRepository.save((ShopList) any())).thenReturn(shopList);

        Mockito.when(shopListRepository.findOne("1")).thenReturn(shopList);

    }

    @Test
    public void findByObjId() {
        ShopList s = shopListService.findByObjId("1").get();
        Assert.assertTrue(s.getObjId().equals("1"));
        Assert.assertTrue(s.getName().equals("Name1"));
    }

    @Test
    public void testSaveOrUpdate() {
        ShopList shopList = new ShopList();
        shopList.setObjId("1");
        shopList.setName("Name1");

        Principal principal = new Principal();
        principal.setUsername("principalUser1");
        principal.setName("principalName1");
        principal.setSurname("principalSurname1");
        principal.setEmail("principalemail1");
        principal.setGsm("principalGsm");
        principal.setPassword("principalPasword1");
        principal.setTitle("principalTitle1");
        principal.setObjId("principal1");
        shopList.setPrincipal(principal);

        ShopList s = shopListService.saveOrUpdate(shopList);
        Assert.assertTrue(s.getObjId().equals("1"));
    }
}
