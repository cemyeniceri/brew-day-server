package org.gsu.brewday.web;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.gsu.brewday.authentication.LoginResponse;
import org.gsu.brewday.authentication.PrincipalLogin;
import org.gsu.brewday.domain.Principal;
import org.gsu.brewday.dto.response.PrincipalInfo;
import org.gsu.brewday.dto.response.ResponseInfo;
import org.gsu.brewday.dto.response.ResponseStatus;
import org.gsu.brewday.exception.BrewDayException;
import org.gsu.brewday.service.PrincipalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by cyeniceri on 16/10/2017.
 */
@RestController
@RequestMapping("/principals")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin
public class PrincipalController {
    private static final Logger LOG = LoggerFactory.getLogger(PrincipalController.class);

    private final PrincipalService principalService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public LoginResponse login(@RequestBody final PrincipalLogin principalLogin) {
        LOG.info("Login request: {}", principalLogin);
        Principal principal = principalService.findByUsernameAndPassword(principalLogin.getUsername(), principalLogin.getPassword())
                .orElseThrow(() -> new BrewDayException("Principal is not Found."));
        LOG.info("Login principal: {}", principal);
        return new LoginResponse(Jwts.builder().setSubject(principalLogin.getUsername())
                .claim("oid", principal.getObjId())
                .signWith(SignatureAlgorithm.HS256, "secretkey").compact());
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<PrincipalInfo>> principals() {
        LOG.info("Listing principals.");
        return new ResponseEntity<>(principalService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ResponseInfo> create(@RequestBody Principal principal) throws BrewDayException {
        LOG.info("Create Principal : {}", principal);
        String username = principal.getUsername();
        if(StringUtils.hasText(username)){
            principal.setUsername(username.trim());
            principalService.saveOrUpdate(principal);
            return new ResponseEntity(new ResponseInfo("Principal is Created", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity(new ResponseInfo("Validation Error", ResponseStatus.FAILURE), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ResponseInfo> update(@RequestBody Principal principal) throws BrewDayException {
        Optional<Principal> mbrPrincipalDbOpt = principalService.findByObjId(principal.getObjId());
        Principal principalDb = mbrPrincipalDbOpt.orElseThrow(()-> new BrewDayException("Principal is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Updating Principal: {}", principalDb);
        String userName = principal.getUsername();
        if(StringUtils.hasText(userName)){
            principalDb.setUsername(userName.trim());
            principalDb.setPassword(principal.getPassword());
            principalDb.setEmail(principal.getEmail());
            principalDb.setTitle(principal.getTitle());
            principalDb.setName(principal.getName());
            principalDb.setSurname(principal.getSurname());
            principalDb.setGsm(principal.getGsm());
            principalService.saveOrUpdate(principalDb);
            return new ResponseEntity(new ResponseInfo("Principal is Updated", ResponseStatus.SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity(new ResponseInfo("Validation Error", ResponseStatus.FAILURE), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{objId}")
    public ResponseEntity<ResponseInfo> delete(@PathVariable("objId") String objId) {
        Optional<Principal> mbrPrincipalDbOpt = principalService.findByObjId(objId);
        Principal principalDb = mbrPrincipalDbOpt.orElseThrow(()-> new BrewDayException("Principal is Not Found", HttpStatus.NOT_FOUND));
        LOG.info("Deleting Principal");
        principalService.delete(principalDb);
        return new ResponseEntity(new ResponseInfo("Principal is Deleted Successfully", ResponseStatus.SUCCESS), HttpStatus.OK);
    }
}
