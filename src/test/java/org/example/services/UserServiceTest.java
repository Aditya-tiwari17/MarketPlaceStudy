package org.example.services;

import org.example.dtos.LoginDTO;
import org.example.dtos.UserRegistrationDTO;
import org.example.entities.Actor;
import org.example.enums.RoleEnum;
import org.example.exceptions.MarketPlaceException;
import org.example.repositories.UserRepository;
import org.example.utils.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Test
    @DisplayName("Successfully registers a new user")
    void registerNewUserSuccess() {
        UserRegistrationDTO dto = new UserRegistrationDTO("user1", "email@example.com", "password", RoleEnum.POSTER);
        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        assertDoesNotThrow(() -> userService.registerNewUser(dto));
    }

    @Test
    @DisplayName("Throws exception when username is taken")
    void registerNewUserUsernameTaken() {
        UserRegistrationDTO dto = new UserRegistrationDTO("existingUser", "email@example.com", "password", RoleEnum.BIDDER);
        when(userRepository.existsByUsername(any())).thenReturn(true);

        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> userService.registerNewUser(dto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Username is already taken!", exception.getEndUserMessage());
    }

    @Test
    @DisplayName("Throws exception when email is taken")
    void registerNewUserEmailTaken() {
        UserRegistrationDTO dto = new UserRegistrationDTO("newUser", "existingEmail@example.com", "password", RoleEnum.BIDDER);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> userService.registerNewUser(dto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User already exists with given email!", exception.getEndUserMessage());
    }

    @Test
    @DisplayName("Successfully logs in a user")
    void loginUserSuccess() {
        LoginDTO loginDTO = new LoginDTO("user", "password");
        Actor actor = Actor.builder().username("user").password("encodedPassword").role(RoleEnum.BIDDER).build();
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(actor));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenUtil.generateAccessToken(any(), any())).thenReturn("mockToken");

        Map<String, Object> response = userService.loginUser(loginDTO);

        assertEquals("User logged in successfully!", response.get("message"));
        assertEquals("mockToken", response.get("access_token"));
    }

    @Test
    @DisplayName("Throws exception when login username is not found")
    void loginUserUsernameNotFound() {
        LoginDTO loginDTO = new LoginDTO("user", "password");
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> userService.loginUser(loginDTO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Username not found!", exception.getEndUserMessage());
    }

    @Test
    @DisplayName("Throws exception when login password is invalid")
    void loginUserInvalidPassword() {
        LoginDTO loginDTO = new LoginDTO("user", "password");
        Actor actor = Actor.builder().username("user").password("encodedPassword").build();
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(actor));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> userService.loginUser(loginDTO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid password!", exception.getEndUserMessage());
    }
}
