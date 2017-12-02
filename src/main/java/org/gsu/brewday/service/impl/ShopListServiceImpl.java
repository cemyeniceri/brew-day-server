package org.gsu.brewday.service.impl;

import lombok.RequiredArgsConstructor;
import org.gsu.brewday.domain.Recipe;
import org.gsu.brewday.domain.ShopList;
import org.gsu.brewday.domain.repository.RecipeRepository;
import org.gsu.brewday.domain.repository.ShopListRepository;
import org.gsu.brewday.exception.BrewDayException;
import org.gsu.brewday.service.RecipeService;
import org.gsu.brewday.service.ShopListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by cyeniceri on 03/12/2017.
 */
@Component("shopListService")
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShopListServiceImpl implements ShopListService {

    private final ShopListRepository shopListRepository;

    @Override
    public ShopList saveOrUpdate(ShopList shopList) {
        Optional<ShopList> shopListDb = shopListRepository.findByNameAndPrincipal(shopList.getName(), shopList.getPrincipal());
        if(shopListDb.isPresent() && !shopListDb.get().getObjId().equals(shopList.getObjId())) {
            throw new BrewDayException("Name must be unique for " + shopList.getPrincipal().getUsername() + ". ");
        }
        return shopListRepository.save(shopList);
    }

    @Override
    public Optional<ShopList> findByObjId(String objId) {
        return Optional.ofNullable(shopListRepository.findOne(objId));
    }

    @Override
    public void delete(ShopList shopList) {
        shopListRepository.delete(shopList);
    }

}
