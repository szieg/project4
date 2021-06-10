package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderControllerTest {

    private final String USERNAME = "test";
    private final String PASS = "test1234";
    private final String ITEMNAME = "item";
    private final String PRICE = "12.34";
    private final String TOTAL_PRICE = "1234.05";


    private OrderController orderController;
    private final OrderRepository orderRepo = mock(OrderRepository.class);
    private final UserRepository userRepo = mock(UserRepository.class);

    @BeforeEach
    public void setUp() {
        orderController = new OrderController(userRepo, orderRepo);
    }

    private User createNewUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword(PASS);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotal(new BigDecimal(TOTAL_PRICE));

        List<Item> items = new ArrayList<>();
        for (long i = 0L; i < 3L; i++) {
            Item item = new Item();
            item.setId(i);
            item.setDescription(ITEMNAME);
            item.setName(ITEMNAME);
            item.setPrice(new BigDecimal(PRICE));
            cart.addItem(item);
        }
        cart.setItems(items);

        user.setCart(cart);

        return user;
    }

    @Test
    public void submitOrderValidation() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(createNewUser());

        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);

        assertEquals(200, response.getStatusCodeValue());

        UserOrder orders = response.getBody();
        assertEquals(USERNAME, orders.getUser().getUsername());
    }

    @Test
    public void validateOrdersForUser() {
        User user = createNewUser();
        when(userRepo.findByUsername(USERNAME)).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(createOrdersByUser());
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);

        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();
        assertEquals(new BigDecimal(TOTAL_PRICE), orders.get(1).getTotal());
    }

    private List<UserOrder> createOrdersByUser() {
        List<UserOrder> userOrders = new ArrayList<>();
        for (long i = 0L; i < 3L; i++) {
            UserOrder order = new UserOrder();
            order.setId(i);
            order.setUser(createNewUser());
            List<Item> items = new ArrayList<>();
            for (long j = 0L; j < 3L; j++) {
                Item item = new Item();
                item.setId(j);
                item.setDescription(ITEMNAME);
                item.setName(ITEMNAME);
                item.setPrice(new BigDecimal(PRICE));
                items.add(item);
            }
            order.setItems(items);
            order.setTotal(new BigDecimal(TOTAL_PRICE));
            userOrders.add(order);
        }
        return userOrders;
    }

}