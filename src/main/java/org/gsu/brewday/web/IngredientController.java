package org.gsu.brewday.web;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.gsu.brewday.domain.Ingredient;
import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.dto.response.IngredientInfo;
import org.gsu.brewday.dto.response.ResponseInfo;
import org.gsu.brewday.dto.response.ResponseStatus;
import org.gsu.brewday.exception.BrewDayException;
import org.gsu.brewday.service.IngredientService;
import org.gsu.brewday.service.PrincipalService;
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

/**
 * Created by cyeniceri on 01/12/2017.
 */
@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin
public class IngredientController {
    private static final Logger LOG = LoggerFactory.getLogger(IngredientController.class);

    private final IngredientService ingredientService;
    private final PrincipalService principalService;
    private final ModelMapper modelMapper;


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<IngredientInfo>> ingredientList(final HttpServletRequest request) throws BrewDayException {
        final Claims claims = (Claims) request.getAttribute("claims");
        String principalOid = (String) claims.get("oid");
        Optional<Principal> principalOpt = principalService.findByObjId(principalOid);
        Principal principal = principalOpt.orElseThrow(()-> new BrewDayException("Principal is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Listing ingredients by principal.");
        Type listType = new TypeToken<List<IngredientInfo>>() {}.getType();
        List<IngredientInfo> ingredientInfoList = modelMapper.map(principal.getIngredients(), listType);
        return new ResponseEntity<>(ingredientInfoList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{objId}")
    public ResponseEntity<IngredientInfo> ingredientByObjId(@PathVariable("objId") String objId) throws BrewDayException {
        LOG.info("Getting Ingredient by ObjId");
        return new ResponseEntity(ingredientService.findByObjId(objId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ResponseInfo> create(@RequestBody Ingredient ingredient) throws BrewDayException {
        LOG.info("Create Ingredient : {}", ingredient);
        String ingredientName = ingredient.getName();
        if(StringUtils.hasText(ingredientName)){
            ingredient.setName(ingredientName.trim());
            ingredientService.saveOrUpdate(ingredient);
            return new ResponseEntity(new ResponseInfo("Ingredient is Created", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity(new ResponseInfo("Validation Error", ResponseStatus.FAILURE), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ResponseInfo> update(@RequestBody Ingredient ingredient) throws BrewDayException {
        Optional<Ingredient> ingredientDbOpt = ingredientService.findByObjId(ingredient.getObjId());
        Ingredient ingredientDb = ingredientDbOpt.orElseThrow(()-> new BrewDayException("Ingredient is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Updating Ingredient: {}", ingredientDb);
        String ingredientName = ingredient.getName();
        if(StringUtils.hasText(ingredientName)){
            ingredientDb.setName(ingredientName.trim());
            ingredientDb.setAmount(ingredient.getAmount());
            ingredientDb.setType(ingredient.getType());
            ingredientDb.setUnit(ingredient.getUnit());
            ingredientService.saveOrUpdate(ingredientDb);
            return new ResponseEntity(new ResponseInfo("Ingredient is Updated", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity(new ResponseInfo("Validation Error", ResponseStatus.FAILURE), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{objId}")
    public ResponseEntity<ResponseInfo> delete(@PathVariable("objId") String objId) {
        Optional<Ingredient> ingredientDbOpt = ingredientService.findByObjId(objId);
        Ingredient ingredientDb = ingredientDbOpt.orElseThrow(()-> new BrewDayException("Ingredient is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Deleting Ingredient");
        ingredientService.delete(ingredientDb);
        return new ResponseEntity(new ResponseInfo("Ingredient is Deleted Successfully", ResponseStatus.SUCCESS), HttpStatus.OK);
    }
}
