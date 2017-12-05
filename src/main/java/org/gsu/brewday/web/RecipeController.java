package org.gsu.brewday.web;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.gsu.brewday.domain.*;
import org.gsu.brewday.dto.response.*;
import org.gsu.brewday.dto.response.ResponseStatus;
import org.gsu.brewday.exception.BrewDayException;
import org.gsu.brewday.service.PrincipalService;
import org.gsu.brewday.service.RecipeService;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cyeniceri on 02/12/2017.
 */
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin
public class RecipeController {
    private static final Logger LOG = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeService recipeService;
    private final PrincipalService principalService;
    private final ModelMapper modelMapper;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<RecipeInfo>> recipeList(final HttpServletRequest request) throws BrewDayException {

        Principal principal = principalService.userLoggedOn(request);
        LOG.info("Listing recipes by principal.");
        Type listType = new TypeToken<List<RecipeInfo>>() {
        }.getType();
        List<RecipeInfo> recipeInfoList = modelMapper.map(principal.getRecipes(), listType);
        return new ResponseEntity<>(recipeInfoList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{objId}")
    public ResponseEntity<IngredientInfo> recipeByObjId(@PathVariable("objId") String objId) throws BrewDayException {
        LOG.info("Getting Recipe by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(objId);
        Recipe recipe = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        return new ResponseEntity(modelMapper.map(recipe, RecipeInfo.class), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ResponseInfo> create(@RequestBody Recipe recipe, final HttpServletRequest request) throws BrewDayException {

        Principal principal = principalService.userLoggedOn(request);
        LOG.info("Create Recipe : {}", recipe);
        String recipeName = recipe.getName();
        if (StringUtils.hasText(recipeName)) {
            recipe.setPrincipal(principal);
            recipe.setName(recipeName.trim());
            recipeService.saveOrUpdate(recipe);
            return new ResponseEntity(new ResponseInfo("Recipe is Created", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity(new ResponseInfo("Validation Error", ResponseStatus.FAILURE), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ResponseInfo> update(@RequestBody Recipe recipe) throws BrewDayException {
        Optional<Recipe> recipeDbOpt = recipeService.findByObjId(recipe.getObjId());
        Recipe recipeDb = recipeDbOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Updating Recipe: {}", recipeDb);
        String recipeName = recipe.getName();
        if (StringUtils.hasText(recipeName)) {
            recipeDb.setName(recipeName.trim());
            recipeDb.setDetail(recipe.getDetail());
            recipeService.saveOrUpdate(recipeDb);
            return new ResponseEntity(new ResponseInfo("Recipe is Updated", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity(new ResponseInfo("Validation Error", ResponseStatus.FAILURE), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{objId}")
    public ResponseEntity<ResponseInfo> delete(@PathVariable("objId") String objId) {
        Optional<Recipe> recipeDbOpt = recipeService.findByObjId(objId);
        Recipe recipeDb = recipeDbOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Deleting Recipe");
        recipeService.delete(recipeDb);
        return new ResponseEntity(new ResponseInfo("Recipe is Deleted Successfully", ResponseStatus.SUCCESS), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{recipeObjId}/posts")
    public ResponseEntity<List<RecipePostInfo>> recipePostList(final @PathVariable("recipeObjId") String recipeObjId) throws BrewDayException {
        Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
        Recipe recipe = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Listing posts by recipe.");
        Type listType = new TypeToken<List<RecipePostInfo>>() {
        }.getType();
        List<RecipePostInfo> recipePostInfoList = modelMapper.map(recipe.getRecipePosts(), listType);
        return new ResponseEntity<>(recipePostInfoList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{recipeObjId}/posts/{postObjId}")
    public ResponseEntity<RecipePostInfo> recipePostByObjId(@PathVariable("recipeObjId") String recipeObjId, @PathVariable("postObjId") String postObjId) throws BrewDayException {
        LOG.info("Getting RecipePost by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
        Recipe recipe = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        List<RecipePost> recipePostList = recipe.getRecipePosts().stream().filter(t-> t.getObjId().equals(postObjId)).collect(Collectors.toList());
        if (!recipePostList.isEmpty())
            return new ResponseEntity(modelMapper.map(recipePostList.get(0), RecipePostInfo.class), HttpStatus.OK);
        throw new BrewDayException("Post is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{recipeObjId}/posts")
    public ResponseEntity<ResponseInfo> createRecipePostByObjId(@PathVariable("recipeObjId") String recipeObjId, @RequestBody RecipePost recipePost) throws BrewDayException {
        LOG.info("Creating RecipePost by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
        Recipe recipeDb = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        recipePost.setRecipe(recipeDb);
        recipeDb.getRecipePosts().add(recipePost);
        recipeService.saveOrUpdate(recipeDb);
        return new ResponseEntity(new ResponseInfo("Recipe Post is Created", ResponseStatus.SUCCESS), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{recipeObjId}/posts")
    public ResponseEntity<ResponseInfo> updateRecipePostByObjId(@PathVariable("recipeObjId") String recipeObjId, @RequestBody RecipePost recipePost) throws BrewDayException {
        LOG.info("Updating RecipePost by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
        Recipe recipeDb = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        List<RecipePost> recipePostList = recipeDb.getRecipePosts().stream().filter(t-> t.getObjId().equals(recipePost.getObjId())).collect(Collectors.toList());
        if (!recipePostList.isEmpty()){
            recipePostList.get(0).setPost(recipePost.getPost());
            recipeService.saveOrUpdate(recipeDb);
            return new ResponseEntity(new ResponseInfo("Recipe Post is Updated", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        throw new BrewDayException("Post is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{recipeObjId}/posts/{postObjId}")
    public ResponseEntity<ResponseInfo> deleteRecipePostByObjId(@PathVariable("recipeObjId") String recipeObjId, @PathVariable("postObjId") String postObjId) throws BrewDayException {
        LOG.info("Deleting RecipePost by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
        Recipe recipeDb = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        List<RecipePost> recipePostList = recipeDb.getRecipePosts().stream().filter(t-> t.getObjId().equals(postObjId)).collect(Collectors.toList());
        if (!recipePostList.isEmpty()){
            recipeDb.getRecipePosts().remove(recipePostList.get(0));
            recipeService.saveOrUpdate(recipeDb);
            return new ResponseEntity(new ResponseInfo("Recipe Post is Deleted", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        throw new BrewDayException("Post is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{recipeObjId}/ingredients")
    public ResponseEntity<List<IngredientInfo>> recipeIngredientList(final @PathVariable("recipeObjId") String recipeObjId) throws BrewDayException {
        Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
        Recipe recipe = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Listing ingredients by recipe.");
        Type listType = new TypeToken<List<IngredientInfo>>() {
        }.getType();
        List<IngredientInfo> recipeIngredientInfoList = modelMapper.map(recipe.getRecipeIngredients(), listType);
        return new ResponseEntity<>(recipeIngredientInfoList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{recipeObjId}/ingredients/{ingredientObjId}")
    public ResponseEntity<IngredientInfo> recipeIngredientByObjId(@PathVariable("recipeObjId") String recipeObjId, @PathVariable("ingredientObjId") String ingredientObjId) throws BrewDayException {
        LOG.info("Getting Recipe Ingredient by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
        Recipe recipe = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        List<RecipeIngredient> recipeIngredientList = recipe.getRecipeIngredients().stream().filter(t-> t.getObjId().equals(ingredientObjId)).collect(Collectors.toList());
        if (!recipeIngredientList.isEmpty())
            return new ResponseEntity(modelMapper.map(recipeIngredientList.get(0), IngredientInfo.class), HttpStatus.OK);
        throw new BrewDayException("Ingredient is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{recipeObjId}/ingredients")
    public ResponseEntity<ResponseInfo> createRecipeIngredientByObjId(@PathVariable("recipeObjId") String recipeObjId, @RequestBody RecipeIngredient recipeIngredient) throws BrewDayException {
        LOG.info("Creating Recipe Ingredient by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
        Recipe recipeDb = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        recipeIngredient.setRecipe(recipeDb);
        recipeDb.getRecipeIngredients().add(recipeIngredient);
        recipeService.saveOrUpdate(recipeDb);
        return new ResponseEntity(new ResponseInfo("Recipe Ingredient is Created", ResponseStatus.SUCCESS), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{recipeObjId}/ingredients")
    public ResponseEntity<ResponseInfo> updateRecipeIngredientByObjId(@PathVariable("recipeObjId") String recipeObjId, @RequestBody RecipeIngredient recipeIngredient) throws BrewDayException {
        LOG.info("Updating Recipe Ingredient by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
        Recipe recipeDb = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        List<RecipeIngredient> recipeIngredientList = recipeDb.getRecipeIngredients().stream().filter(t-> t.getObjId().equals(recipeIngredient.getObjId())).collect(Collectors.toList());
        if (!recipeIngredientList.isEmpty()){
            RecipeIngredient recipeIngredientDb = recipeIngredientList.get(0);
            recipeIngredientDb.setAmount(recipeIngredient.getAmount());
            recipeIngredientDb.setName(recipeIngredient.getName());
            recipeIngredientDb.setType(recipeIngredient.getType());
            recipeIngredientDb.setUnit(recipeIngredient.getUnit());
            recipeService.saveOrUpdate(recipeDb);
            return new ResponseEntity(new ResponseInfo("Recipe Ingredient is Updated", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        throw new BrewDayException("Ingredient is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{recipeObjId}/ingredients/{ingredientObjId}")
    public ResponseEntity<ResponseInfo> deleteRecipeIngredientByObjId(@PathVariable("recipeObjId") String recipeObjId, @PathVariable("ingredientObjId") String ingredientObjId) throws BrewDayException {
        LOG.info("Deleting Recipe Ingredient by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
        Recipe recipeDb = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        List<RecipeIngredient> recipeIngredientList = recipeDb.getRecipeIngredients().stream().filter(t-> t.getObjId().equals(ingredientObjId)).collect(Collectors.toList());
        if (!recipeIngredientList.isEmpty()){
            recipeDb.getRecipeIngredients().remove(recipeIngredientList.get(0));
            recipeService.saveOrUpdate(recipeDb);
            return new ResponseEntity(new ResponseInfo("Recipe Ingredient is Deleted", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        throw new BrewDayException("Ingredient is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{recipeObjId}/check")
    public ResponseEntity<Map<String, ShopListCheckIngredientInfo>> checkIsRecipeAvailableByObjId(@PathVariable("recipeObjId") String recipeObjId, final HttpServletRequest request) throws BrewDayException {

        Principal principal = principalService.userLoggedOn(request);
        LOG.info("Listing ingredients by principal.");
        Set<Ingredient> ingredientSet = principal.getIngredients();
        if(ingredientSet.isEmpty()){
            throw new BrewDayException("You haven't any ingredients in your stock!!", HttpStatus.NOT_FOUND);
        }else{

            Optional<Recipe> recipeOpt = recipeService.findByObjId(recipeObjId);
            Recipe recipeDb = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
            Set<RecipeIngredient> recipeIngredientSet = recipeDb.getRecipeIngredients();

            if (!recipeIngredientSet.isEmpty()){
                LOG.info("Checking Recipe by ObjId");

                Map<String, ShopListCheckIngredientInfo> availabilityMap = new HashMap<>();
                for (RecipeIngredient recipeIngredient : recipeIngredientSet) {
                    ShopListCheckIngredientInfo shopListCheckIngredientInfo = new ShopListCheckIngredientInfo();

                    List<Ingredient> filteredList = ingredientSet.stream().filter(ing->
                        ing.getName().equalsIgnoreCase(recipeIngredient.getName())
                                && ing.getType().equalsIgnoreCase(recipeIngredient.getType())
                    ).collect(Collectors.toList());

                    if(!filteredList.isEmpty()){
                        BigDecimal difference = new BigDecimal(filteredList.get(0).getAmount()).subtract(new BigDecimal(recipeIngredient.getAmount()));
                        shopListCheckIngredientInfo.setState(difference.compareTo(BigDecimal.ZERO) >= 0);
                        shopListCheckIngredientInfo.setAmount(shopListCheckIngredientInfo.getState() ? "0" : difference.abs().toString());
                    }else{
                        shopListCheckIngredientInfo.setState(Boolean.FALSE);
                        shopListCheckIngredientInfo.setAmount(recipeIngredient.getAmount());
                    }

                    availabilityMap.put(recipeIngredient.getObjId() ,shopListCheckIngredientInfo);
                }
                return new ResponseEntity(availabilityMap, HttpStatus.OK);
            }else{
                throw new BrewDayException("Recipe Ingredient List is Empty", HttpStatus.NOT_FOUND);
            }
        }
    }

}
