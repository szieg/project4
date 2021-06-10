package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CartControllerTest {

    private final String USERNAME = "test";
    private final String PASS = "test1234";
    private final String ITEMNAME = "item";
    private final String PRICE = "12.34";

    private CartController cartController;
    private final ItemRepository itemRepo = mock(ItemRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final UserRepository userRepo = mock(UserRepository.class);

    @BeforeEach
    public void setUpCart() {
        cartController = new CartController(userRepo, cartRepo, itemRepo);
    }

    private User createNewUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword(PASS);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        List<Item> items = new ArrayList<>();
        for (long i = 0L; i < 3L; i++) {
            Item item = new Item();
            item.setId(i);
            item.setDescription(ITEMNAME);
            item.setName(ITEMNAME);
            item.setPrice(new BigDecimal("12.34"));
            cart.addItem(item);
        }
        cart.setItems(items);
        user.setCart(cart);

        return user;
    }

    private Item createNewItem() {
        Item item = new Item();
        item.setId(1L);
        item.setDescription(ITEMNAME);
        item.setName(ITEMNAME);
        item.setPrice(new BigDecimal(PRICE));

        return item;
    }

    private ModifyCartRequest createModifyCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(3);
        modifyCartRequest.setUsername(USERNAME);

        return modifyCartRequest;
    }
    @Test
    public void checkRemoveFromCart() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(createNewUser());
        when(itemRepo.findById(1L)).thenReturn(Optional.of(createNewItem()));
        ResponseEntity<Cart> response = cartController.removeFromcart(createModifyCartRequest());
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertEquals(USERNAME, cart.getUser().getUsername());
    }
    @Test
    public void checkAddToCart() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(createNewUser());
        when(itemRepo.findById(1L)).thenReturn(Optional.of(createNewItem()));
        ResponseEntity<Cart> response = cartController.addTocart(createModifyCartRequest());
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertEquals(USERNAME, cart.getUser().getUsername());
    }


}
