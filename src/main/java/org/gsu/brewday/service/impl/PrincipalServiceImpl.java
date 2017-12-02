package org.gsu.brewday.service.impl;

import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.domain.repository.PrincipalRepository;
import org.gsu.brewday.dto.response.PrincipalInfo;
import org.gsu.brewday.exception.BrewDayException;
import org.gsu.brewday.service.PrincipalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by cyeniceri on 04/02/2017.
 */
@Component("principalService")
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrincipalServiceImpl implements PrincipalService {

    private final PrincipalRepository principalRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<PrincipalInfo> findAll() {
        List<Principal> principalList = principalRepository.findAll();
        return principalList.stream().map(t-> modelMapper.map(t, PrincipalInfo.class)).collect(Collectors.toList());
    }

    @Override
    public Principal saveOrUpdate(Principal principal) {
        Optional<Principal> mbrPrincipalDb = principalRepository.findByUsername(principal.getUsername());
        if(mbrPrincipalDb.isPresent() && !mbrPrincipalDb.get().getObjId().equals(principal.getObjId())) {
            throw new BrewDayException("Username must be unique. " + principal.getUsername() + " is already defined.");
        }
        return principalRepository.save(principal);
    }

    @Override
    public Optional<Principal> findByUsernameAndPassword(String username, String password) {
        return principalRepository.findByUsernameAndPassword(username, password);
    }

    @Override
    public Optional<Principal> findByObjId(String objId) {
        return Optional.ofNullable(principalRepository.findOne(objId));
    }

    @Override
    public void delete(Principal principal) {
        principalRepository.delete(principal);
    }

}
