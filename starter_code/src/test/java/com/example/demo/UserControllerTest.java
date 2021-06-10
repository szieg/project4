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
import org.springframework.security.core.context.SecurityContext;
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
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);


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
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASS);
        request.setConfirmPassword(PASS);

        ResponseEntity<User> response1 = userController.createUser(request);
        User user2 = response1.getBody();
        System.out.println("user created: " + user2.getId());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(USERNAME);

        User user = new User();
        user.setId(0L);
        user.setUsername(USERNAME);
        user.setPassword(PASS);

        Optional<User> optUser = Optional.of(user);
        when(userRepo.findById(0L)).thenReturn(optUser);
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        ResponseEntity<User> response = userController.findById(0l, authentication);

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
