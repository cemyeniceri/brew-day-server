package org.gsu.brewday.domain.repository;

import org.gsu.brewday.domain.Principal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by cyeniceri on 01/12/2017.
 */
public interface PrincipalRepository extends JpaRepository<Principal, String> {

    Optional<Principal> findByUsernameAndPassword(String username, String password);
    Optional<Principal> findByUsername(String username);

}
