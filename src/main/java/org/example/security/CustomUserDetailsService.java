package org.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.entities.Actor;
import org.example.services.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    /**
     Loads user-specific data for authentication.
     @param username the username of the user
     @return a UserDetails object containing user information and authorities
     @throws UsernameNotFoundException if the user is not found
     **/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve user details from the user service
        Actor actor = userService.findByUsername(username);
        if (actor == null) {
            // Throw exception if user is not found
            throw new UsernameNotFoundException(String.format("Username: %s not found.", username));
        }

        // Retrieve roles for the user
        List<String> roles = userService.getUserRoles(actor);
        if (roles.isEmpty()) {
            // Throw exception if user has no roles
            throw new UsernameNotFoundException(String.format("Username: %s has no roles assigned.", username));
        }

        // Convert roles to Spring Security authorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role ->
                authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", role.toUpperCase())))
        );

        // Log successful user retrieval and return user details
        log.info("User with username {} successfully loaded with roles: {}", username, roles);
        return new User(actor.getUsername(), actor.getPassword(), authorities);
    }
}