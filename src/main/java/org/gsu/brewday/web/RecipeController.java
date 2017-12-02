package org.gsu.brewday.web;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.Recipe;
import org.gsu.brewday.domain.RecipePost;
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
import java.util.List;
import java.util.Optional;
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
        final Claims claims = (Claims) request.getAttribute("claims");
        String principalOid = (String) claims.get("oid");
        Optional<Principal> principalOpt = principalService.findByObjId(principalOid);
        Principal principal = principalOpt.orElseThrow(() -> new BrewDayException("Principal is Not Found", HttpStatus.NOT_FOUND));
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
        final Claims claims = (Claims) request.getAttribute("claims");
        String principalOid = (String) claims.get("oid");
        Optional<Principal> principalOpt = principalService.findByObjId(principalOid);
        Principal principal = principalOpt.orElseThrow(() -> new BrewDayException("Principal is Not Found", HttpStatus.NOT_FOUND));
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
    public ResponseEntity<IngredientInfo> recipePostByObjId(@PathVariable("objId") String objId, @PathVariable("postObjId") String postObjId) throws BrewDayException {
        LOG.info("Getting RecipePost by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(objId);
        Recipe recipe = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        List<RecipePost> recipePostList = recipe.getRecipePosts().stream().filter(t-> t.getObjId().equals(postObjId)).collect(Collectors.toList());
        if (!recipePostList.isEmpty())
            return new ResponseEntity(modelMapper.map(recipePostList.get(0), RecipeInfo.class), HttpStatus.OK);
        throw new BrewDayException("Post is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{recipeObjId}/posts")
    public ResponseEntity<IngredientInfo> createRecipePostByObjId(@PathVariable("objId") String objId, @RequestBody RecipePost recipePost) throws BrewDayException {
        LOG.info("Creating RecipePost by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(objId);
        Recipe recipeDb = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        recipePost.setRecipe(recipeDb);
        recipeDb.getRecipePosts().add(recipePost);
        recipeService.saveOrUpdate(recipeDb);
        return new ResponseEntity(new ResponseInfo("Recipe Post is Created", ResponseStatus.SUCCESS), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{recipeObjId}/posts")
    public ResponseEntity<IngredientInfo> updateRecipePostByObjId(@PathVariable("objId") String objId, @RequestBody RecipePost recipePost) throws BrewDayException {
        LOG.info("Updating RecipePost by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(objId);
        Recipe recipeDb = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        List<RecipePost> recipePostList = recipeDb.getRecipePosts().stream().filter(t-> t.getObjId().equals(recipePost.getObjId())).collect(Collectors.toList());
        if (!recipePostList.isEmpty()){
            recipePostList.get(0).setPost(recipePost.getPost());
            recipeService.saveOrUpdate(recipeDb);
            return new ResponseEntity(new ResponseInfo("Recipe Post is Created", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        throw new BrewDayException("Post is Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{recipeObjId}/posts/{postObjId}")
    public ResponseEntity<IngredientInfo> updateRecipePostByObjId(@PathVariable("objId") String objId, @PathVariable("postObjId") String postObjId) throws BrewDayException {
        LOG.info("Deleting RecipePost by ObjId");
        Optional<Recipe> recipeOpt = recipeService.findByObjId(objId);
        Recipe recipeDb = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        List<RecipePost> recipePostList = recipeDb.getRecipePosts().stream().filter(t-> t.getObjId().equals(postObjId)).collect(Collectors.toList());
        if (!recipePostList.isEmpty()){
            recipePostList.remove(recipePostList.get(0));
            recipeService.saveOrUpdate(recipeDb);
            return new ResponseEntity(new ResponseInfo("Recipe Post is Deleted", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        throw new BrewDayException("Post is Not Found", HttpStatus.NOT_FOUND);
    }

}
