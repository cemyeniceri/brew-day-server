package org.gsu.brewday.web;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.gsu.brewday.domain.*;
import org.gsu.brewday.dto.response.*;
import org.gsu.brewday.dto.response.ResponseStatus;
import org.gsu.brewday.exception.BrewDayException;
import org.gsu.brewday.service.PrincipalService;
import org.gsu.brewday.service.RecipeService;
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
import java.util.List;
import java.util.Optional;
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
        final Claims claims = (Claims) request.getAttribute("claims");
        String principalOid = (String) claims.get("oid");
        Optional<Principal> principalOpt = principalService.findByObjId(principalOid);
        Principal principal = principalOpt.orElseThrow(() -> new BrewDayException("Principal is Not Found", HttpStatus.NOT_FOUND));
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
        final Claims claims = (Claims) request.getAttribute("claims");
        String principalOid = (String) claims.get("oid");
        Optional<Principal> principalOpt = principalService.findByObjId(principalOid);
        Principal principal = principalOpt.orElseThrow(() -> new BrewDayException("Principal is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Create Shop List : {}", shopList);
        String shopListName = shopList.getName();
        if (StringUtils.hasText(shopListName)) {
            shopList.setPrincipal(principal);
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
        LOG.info("Creating Recipe Ingredient by ObjId");
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
            shopListIngredientList.remove(shopListIngredientList.get(0));
            shopListService.saveOrUpdate(shopListDb);
            return new ResponseEntity(new ResponseInfo("Shop List Ingredient is Deleted", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        throw new BrewDayException("Ingredient is Not Found", HttpStatus.NOT_FOUND);
    }

}
