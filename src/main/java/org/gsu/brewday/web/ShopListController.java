package org.gsu.brewday.web;

import lombok.RequiredArgsConstructor;
import org.gsu.brewday.domain.Ingredient;
import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.ShopList;
import org.gsu.brewday.domain.ShopListIngredient;
import org.gsu.brewday.dto.response.IngredientInfo;
import org.gsu.brewday.dto.response.ResponseInfo;
import org.gsu.brewday.dto.response.ResponseStatus;
import org.gsu.brewday.dto.response.ShopListInfo;
import org.gsu.brewday.exception.BrewDayException;
import org.gsu.brewday.service.PrincipalService;
import org.gsu.brewday.service.ShopListService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by cyeniceri on 03/12/2017.
 */
@RestController
@RequestMapping("/shop-lists")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin
public class ShopListController {
    private static final Logger LOG = LoggerFactory.getLogger(ShopListController.class);

    private final ShopListService shopListService;
    private final PrincipalService principalService;
    private final ModelMapper modelMapper;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ShopListInfo>> shopLists(final HttpServletRequest request) throws BrewDayException {
        Principal principal = principalService.userLoggedOn(request);
        LOG.info("Listing Shop Lists by principal.");
        Type listType = new TypeToken<List<ShopListInfo>>() {
        }.getType();
        List<ShopListInfo> shopListInfoList = modelMapper.map(principal.getShopLists(), listType);
        return new ResponseEntity<>(shopListInfoList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{objId}")
    public ResponseEntity<ShopListInfo> shopListByObjId(@PathVariable("objId") String objId) throws BrewDayException {
        LOG.info("Getting Shop List by ObjId");
        Optional<ShopList> shopListOpt = shopListService.findByObjId(objId);
        ShopList shopList = shopListOpt.orElseThrow(() -> new BrewDayException("Shop List is Not Found", HttpStatus.NOT_FOUND));
        return new ResponseEntity(modelMapper.map(shopList, ShopListInfo.class), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ResponseInfo> create(@RequestBody ShopList shopList, final HttpServletRequest request) throws BrewDayException {
        Principal principal = principalService.userLoggedOn(request);
        LOG.info("Create Shop List : {}", shopList);
        String shopListName = shopList.getName();
        if (StringUtils.hasText(shopListName)) {
            shopList.setPrincipal(principal);
            for (ShopListIngredient shopListIngredient: shopList.getShopListIngredients()){
                shopListIngredient.setShopList(shopList);
            }
            shopList.setName(shopListName.trim());
            shopListService.saveOrUpdate(shopList);
            return new ResponseEntity(new ResponseInfo("Shop List is Created", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity(new ResponseInfo("Validation Error", ResponseStatus.FAILURE), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ResponseInfo> update(@RequestBody ShopList shopList) throws BrewDayException {
        Optional<ShopList> shopListDbOpt = shopListService.findByObjId(shopList.getObjId());
        ShopList shopListDb = shopListDbOpt.orElseThrow(() -> new BrewDayException("Shop List is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Updating Shop list: {}", shopListDb);
        String shopListName = shopList.getName();
        if (StringUtils.hasText(shopListName)) {
            shopListDb.setName(shopListName.trim());
            shopListService.saveOrUpdate(shopListDb);
            return new ResponseEntity(new ResponseInfo("Shop List is Updated", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity(new ResponseInfo("Validation Error", ResponseStatus.FAILURE), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{objId}")
    public ResponseEntity<ResponseInfo> delete(@PathVariable("objId") String objId) {
        Optional<ShopList> shopListDbOpt = shopListService.findByObjId(objId);
        ShopList shopListDb = shopListDbOpt.orElseThrow(() -> new BrewDayException("Shop List is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Deleting Shop List");
        shopListService.delete(shopListDb);
        return new ResponseEntity(new ResponseInfo("Shop List is Deleted Successfully", ResponseStatus.SUCCESS), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{shopListObjId}/ingredients")
    public ResponseEntity<List<IngredientInfo>> shopListIngredientList(final @PathVariable("shopListObjId") String shopListObjId) throws BrewDayException {
        Optional<ShopList> shopListOpt = shopListService.findByObjId(shopListObjId);
        ShopList shopList = shopListOpt.orElseThrow(() -> new BrewDayException("Shop List is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Listing ingredients by Shop List.");
        Type listType = new TypeToken<List<IngredientInfo>>() {
        }.getType();
        List<IngredientInfo> shopListIngredientInfoList = modelMapper.map(shopList.getShopListIngredients(), listType);
        return new ResponseEntity<>(shopListIngredientInfoList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{shopListObjId}/ingredients/{ingredientObjId}")
    public ResponseEntity<IngredientInfo> shopListIngredientByObjId(@PathVariable("shopListObjId") String shopListObjId, @PathVariable("ingredientObjId") String ingredientObjId) throws BrewDayException {
        LOG.info("Getting Shop List Ingredient by ObjId");
        Optional<ShopList> shopListOpt = shopListService.findByObjId(shopListObjId);
        ShopList shopList = shopListOpt.orElseThrow(() -> new BrewDayException("Shop List is Not Found", HttpStatus.NOT_FOUND));
        List<ShopListIngredient> shopListIngredientList = shopList.getShopListIngredients().stream().filter(t-> t.getObjId().equals(ingredientObjId)).collect(Collectors.toList());
        if (!shopListIngredientList.isEmpty())
            return new ResponseEntity(modelMapper.map(shopListIngredientList.get(0), IngredientInfo.class), HttpStatus.OK);
        throw new BrewDayException("Ingredient is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{shopListObjId}/ingredients")
    public ResponseEntity<ResponseInfo> createShopListIngredientByObjId(@PathVariable("shopListObjId") String shopListObjId, @RequestBody ShopListIngredient shopListIngredient) throws BrewDayException {
        LOG.info("Creating Shop Ingredient by ObjId");
        Optional<ShopList> shopListOpt = shopListService.findByObjId(shopListObjId);
        ShopList shopListDb = shopListOpt.orElseThrow(() -> new BrewDayException("Shop List is Not Found", HttpStatus.NOT_FOUND));
        shopListIngredient.setShopList(shopListDb);
        shopListDb.getShopListIngredients().add(shopListIngredient);
        shopListService.saveOrUpdate(shopListDb);
        return new ResponseEntity(new ResponseInfo("Shop List Ingredient is Created", ResponseStatus.SUCCESS), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{shopListObjId}/ingredients")
    public ResponseEntity<ResponseInfo> updateShopListIngredientByObjId(@PathVariable("shopListObjId") String shopListObjId, @RequestBody ShopListIngredient shopListIngredient) throws BrewDayException {
        LOG.info("Updating Shop List Ingredient by ObjId");
        Optional<ShopList> shopListOpt = shopListService.findByObjId(shopListObjId);
        ShopList shopListDb = shopListOpt.orElseThrow(() -> new BrewDayException("Shop List is Not Found", HttpStatus.NOT_FOUND));
        List<ShopListIngredient> shopListIngredientList = shopListDb.getShopListIngredients().stream().filter(t-> t.getObjId().equals(shopListIngredient.getObjId())).collect(Collectors.toList());
        if (!shopListIngredientList.isEmpty()){
            ShopListIngredient shopListIngredientDb = shopListIngredientList.get(0);
            shopListIngredientDb.setAmount(shopListIngredient.getAmount());
            shopListIngredientDb.setName(shopListIngredient.getName());
            shopListIngredientDb.setType(shopListIngredient.getType());
            shopListIngredientDb.setUnit(shopListIngredient.getUnit());
            shopListService.saveOrUpdate(shopListDb);
            return new ResponseEntity(new ResponseInfo("Shop List Ingredient is Created", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        throw new BrewDayException("Ingredient is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{shopListObjId}/ingredients/{ingredientObjId}")
    public ResponseEntity<ResponseInfo> deleteShopListIngredientByObjId(@PathVariable("shopListObjId") String shopListObjId, @PathVariable("ingredientObjId") String ingredientObjId) throws BrewDayException {
        LOG.info("Deleting Shop List Ingredient by ObjId");
        Optional<ShopList> shopListOpt = shopListService.findByObjId(shopListObjId);
        ShopList shopListDb = shopListOpt.orElseThrow(() -> new BrewDayException("Shop List is Not Found", HttpStatus.NOT_FOUND));
        List<ShopListIngredient> shopListIngredientList = shopListDb.getShopListIngredients().stream().filter(t-> t.getObjId().equals(ingredientObjId)).collect(Collectors.toList());
        if (!shopListIngredientList.isEmpty()){
            shopListDb.getShopListIngredients().remove(shopListIngredientList.get(0));
            shopListService.saveOrUpdate(shopListDb);
            return new ResponseEntity(new ResponseInfo("Shop List Ingredient is Deleted", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        throw new BrewDayException("Ingredient is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{shopListObjId}/done")
    public ResponseEntity<ResponseInfo> doneShopListByObjId(@PathVariable("shopListObjId") String shopListObjId, final HttpServletRequest request) throws BrewDayException {
        LOG.info("Deleting Shop List and Updating Ingredient");
        Optional<ShopList> shopListOpt = shopListService.findByObjId(shopListObjId);
        ShopList shopListDb = shopListOpt.orElseThrow(() -> new BrewDayException("Shop List is Not Found", HttpStatus.NOT_FOUND));

        if(!shopListDb.getShopListIngredients().isEmpty()){

            Principal principal = principalService.userLoggedOn(request);
            Set<Ingredient> userIngredients = principal.getIngredients();

            for (ShopListIngredient shopListIngredient :shopListDb.getShopListIngredients()){

                List<Ingredient> userIngListFiltered = userIngredients.stream().filter(usrIng ->
                        usrIng.getName().equalsIgnoreCase(shopListIngredient.getName())
                                && usrIng.getType().equalsIgnoreCase(shopListIngredient.getType())
                ).collect(Collectors.toList());

                if(!userIngListFiltered.isEmpty()){
                    userIngListFiltered.get(0).setAmount(new BigDecimal(userIngListFiltered.get(0).getAmount()).add(new BigDecimal(shopListIngredient.getAmount())).toString());
                }else{
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setPrincipal(principal);
                    newIngredient.setAmount(shopListIngredient.getAmount());
                    newIngredient.setUnit(shopListIngredient.getUnit());
                    newIngredient.setType(shopListIngredient.getType());
                    newIngredient.setName(shopListIngredient.getName());
                    principal.getIngredients().add(newIngredient);
                }
            }
            principalService.saveOrUpdate(principal);
        }

        shopListService.delete(shopListDb);
        return new ResponseEntity(new ResponseInfo("Shop List Done Operation is finished successfully", ResponseStatus.SUCCESS), HttpStatus.OK);
    }

}
