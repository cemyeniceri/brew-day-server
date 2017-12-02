package org.gsu.brewday.service;

import org.gsu.brewday.domain.ShopList;

import java.util.Optional;

/**
 * Created by cyeniceri on 03/12/2017.
 */
public interface ShopListService {

    ShopList saveOrUpdate(ShopList shopList);

    Optional<ShopList> findByObjId(String objId);

    void delete(ShopList shopList);
}



