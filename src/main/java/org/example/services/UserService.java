package org.example.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.dtos.LoginDTO;
import org.example.dtos.UserRegistrationDTO;
import org.example.entities.Actor;
import org.example.enums.RoleEnum;
import org.example.exceptions.MarketPlaceException;
import org.example.repositories.UserRepository;
import org.example.utils.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     Registers a new user.
     @param userRegistrationDTO the user registration data
     **/
    @Transactional
    public void registerNewUser(UserRegistrationDTO userRegistrationDTO) {
        log.info("Registering new user with username: {}", userRegistrationDTO.getUsername());

        if (Boolean.TRUE.equals(userRepository.existsByUsername(userRegistrationDTO.getUsername()))) {
            throw new MarketPlaceException(HttpStatus.BAD_REQUEST, "Username is already taken!",
                    "Username is already taken!");
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(userRegistrationDTO.getEmail()))) {
            throw new MarketPlaceException(HttpStatus.BAD_REQUEST, "User already exists with given email!",
                    "User already exists with given email!");
        }

        Actor actor = Actor.builder()
                .username(userRegistrationDTO.getUsername())
                .email(userRegistrationDTO.getEmail())
                .password(encoder.encode(userRegistrationDTO.getPassword()))
                .build();

        if ("poster".equalsIgnoreCase(userRegistrationDTO.getRole().toString())) {
            actor.setRole(RoleEnum.POSTER);
        } else if ("bidder".equalsIgnoreCase(userRegistrationDTO.getRole().toString())) {
            actor.setRole(RoleEnum.BIDDER);
        } else {
            throw new MarketPlaceException(HttpStatus.BAD_REQUEST, "Invalid role specified!",
                    "Invalid role specified!");
        }

        userRepository.save(actor);
        log.info("User registered successfully with username: {}", userRegistrationDTO.getUsername());
    }

    /**
     Finds an actor by username.
     @param username the username of the actor
     @return the actor, or null if not found
     **/
    public Actor findByUsername(String username) {
        log.info("Searching for user by username: {}", username);
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     Retrieves the roles of a given actor.
     @param actor the actor whose roles are to be retrieved
     @return a list of role names
     **/
    public List<String> getUserRoles(Actor actor) {
        log.info("Retrieving roles for user ID: {}", actor.getId());
        return List.of(actor.getRole().toString());
    }

    /**
     Authenticates a user and generates an access token.
     @param loginDTO the login data
     @return a map containing the login message and access token
     **/
    public Map<String, Object> loginUser(LoginDTO loginDTO) {
        log.info("User login attempt with username: {}", loginDTO.getUsername());
        Actor actor = userRepository.findByUsername(loginDTO.getUsername()).orElse(null);

        if (actor != null) {
            if (encoder.matches(loginDTO.getPassword(), actor.getPassword())) {
                String token = jwtTokenUtil.generateAccessToken(actor, getUserRoles(actor));
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User logged in successfully!");
                response.put("access_token", token);
                log.info("User logged in successfully with username: {}", loginDTO.getUsername());
                return response;
            } else {
                throw new MarketPlaceException(HttpStatus.BAD_REQUEST, "Invalid password!", "Invalid password!");
            }
        } else {
            throw new MarketPlaceException(HttpStatus.BAD_REQUEST, "Username not found!", "Username not found!");
        }
    }
}
