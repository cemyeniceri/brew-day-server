package org.gsu.brewday.web;

import lombok.RequiredArgsConstructor;
import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.Recipe;
import org.gsu.brewday.domain.RecipeIngredient;
import org.gsu.brewday.domain.RecipePost;
import org.gsu.brewday.dto.response.RecipeInfo;
import org.gsu.brewday.dto.response.ResponseInfo;
import org.gsu.brewday.dto.response.ResponseStatus;
import org.gsu.brewday.exception.BrewDayException;
import org.gsu.brewday.service.PrincipalService;
import org.gsu.brewday.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by cyeniceri on 02/12/2017.
 */
@RestController
@RequestMapping("/public-recipes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin
public class PublicRecipeController {
    private static final Logger LOG = LoggerFactory.getLogger(PublicRecipeController.class);

    private final RecipeService recipeService;
    private final PrincipalService principalService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<RecipeInfo>> recipeList() throws BrewDayException {

        List<RecipeInfo> recipeInfoList = recipeService.findAll();
        LOG.info("Listing public recipes.");
        return new ResponseEntity<>(recipeInfoList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{objId}")
    public ResponseEntity<ResponseInfo> clone(@PathVariable("objId") String objId, final HttpServletRequest request) throws BrewDayException {

        Principal principal = principalService.userLoggedOn(request);
        Optional<Recipe> recipeOpt = recipeService.findByObjId(objId);
        Recipe recipe = recipeOpt.orElseThrow(() -> new BrewDayException("Recipe is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Clone Recipe : {} to principal : {} ", recipe, principal.getUsername());

        Recipe newRecipe = new Recipe();
        BeanUtils.copyProperties(recipe, newRecipe, "objId", "principal", "recipePosts", "recipeIngredients");

        Set<RecipeIngredient> recipeIngredientSet = cloneRecipeIngredients(recipe, newRecipe);
        Set<RecipePost> recipePostSet = cloneRecipePosts(recipe, newRecipe);

        newRecipe.setRecipeIngredients(recipeIngredientSet);
        newRecipe.setRecipePosts(recipePostSet);
        newRecipe.setPrincipal(principal);
        principal.getRecipes().add(newRecipe);
        principalService.saveOrUpdate(principal);

        return new ResponseEntity(new ResponseInfo("Recipe is Cloned", ResponseStatus.SUCCESS), HttpStatus.OK);
    }

    private Set<RecipeIngredient> cloneRecipeIngredients(Recipe recipe, Recipe newRecipe) {
        Set<RecipeIngredient> recipeIngredientSet = new HashSet<>();
        for (RecipeIngredient recipeIngredient: recipe.getRecipeIngredients()){
            RecipeIngredient newRecipeIngredient = new RecipeIngredient();
            BeanUtils.copyProperties(recipeIngredient, newRecipeIngredient, "objId", "recipe");
            newRecipeIngredient.setRecipe(newRecipe);
            recipeIngredientSet.add(newRecipeIngredient);
        }
        return recipeIngredientSet;
    }

    private Set<RecipePost> cloneRecipePosts(Recipe recipe, Recipe newRecipe) {
        Set<RecipePost> recipePostSet = new HashSet<>();
        for (RecipePost recipePost: recipe.getRecipePosts()){
            RecipePost newRecipePost = new RecipePost();
            BeanUtils.copyProperties(recipePost, newRecipePost, "objId", "recipe");
            newRecipePost.setRecipe(newRecipe);
            recipePostSet.add(newRecipePost);
        }
        return recipePostSet;
    }
}
