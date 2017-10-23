package org.gsu.brewday.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrincipalInfo implements Serializable{
    private String password;
    private String email;
    private String title;
    private String name;
    private String surname;
    private String userStatus;
    private String gsm;
    private String objId;
    private String username;
}
