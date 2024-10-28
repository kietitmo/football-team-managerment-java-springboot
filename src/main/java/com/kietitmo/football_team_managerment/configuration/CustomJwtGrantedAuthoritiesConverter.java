package com.kietitmo.football_team_managerment.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> preAuthoritiesList = new ArrayList<GrantedAuthority>();

        // Extract roles from the "roles" claim
        List<GrantedAuthority> roles = jwt.getClaimAsStringList("roles").stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Extract permissions from the "permissions" claim
        List<GrantedAuthority> permissions = jwt.getClaimAsStringList("permissions").stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Combine roles and permissions
        preAuthoritiesList.addAll(roles);
        preAuthoritiesList.addAll(permissions);

        return preAuthoritiesList;
    }
}
