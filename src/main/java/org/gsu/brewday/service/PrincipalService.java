package org.gsu.brewday.service;

import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.dto.response.PrincipalInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Created by cyeniceri on 04/02/2017.
 */
public interface PrincipalService {

    List<PrincipalInfo> findAll();

    Principal saveOrUpdate(Principal principal);

    Optional<Principal> findByUsernameAndPassword(String username, String password);

    Optional<Principal> findByObjId(String objId);

    void delete(Principal principal);

    Principal userLoggedOn(HttpServletRequest request);
}



