package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserControllerTest {

    private final String USERNAME = "test";
    private final String PASS = "test1234";
    private UserController userController;
    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController(userRepo, cartRepo, bCryptPasswordEncoder);
    }

    @Test
    public void createUserData() {
        when(bCryptPasswordEncoder.encode(PASS)).thenReturn("hashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASS);
        request.setConfirmPassword(PASS);

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();

        assertEquals(0, user.getId());
        assertEquals(USERNAME, user.getUsername());
        assertEquals("hashed", user.getPassword());
    }


    @Test
    public void validateUserFindById() {
        User user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword(PASS);

        Optional<User> optUser = Optional.of(user);
        when(userRepo.findById(1L)).thenReturn(optUser);
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        ResponseEntity<User> response = userController.findById(1L, authentication);

        assertEquals(200, response.getStatusCodeValue());
        User user1 = response.getBody();
        assertEquals(user.getUsername(), user1.getUsername());
    }

    @Test
    public void validateUserFindByUsername() {
        User user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword(PASS);
        when(userRepo.findByUsername(USERNAME)).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName(USERNAME);
        assertEquals(200, response.getStatusCodeValue());
        User user1 = response.getBody();
        assertEquals(USERNAME, user1.getUsername());
    }

}
