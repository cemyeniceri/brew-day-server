package org.gsu.brewday.authentication;

import lombok.Data;

@Data
public class PrincipalLogin {
    private String username;
    private String password;
}