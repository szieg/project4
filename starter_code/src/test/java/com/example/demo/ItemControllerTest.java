package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemControllerTest {

    private final String ITEMNAME = "item";
    private final String PRICE = "12.34";

    private ItemController itemController;
    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @BeforeEach
    public void setUp() {
        itemController = new ItemController(itemRepo);
    }


    @Test
    public void validateGetItemById() {
        Item item = new Item();
        item.setId(1L);
        item.setName(ITEMNAME);
        item.setDescription(ITEMNAME);
        item.setPrice(new BigDecimal(PRICE));

        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertEquals(200, response.getStatusCodeValue());
        Item item1 = response.getBody();
        assertEquals(ITEMNAME, item1.getName());
    }
    @Test
    public void checkGetItems() {
        Item item = new Item();
        item.setId(1L);
        item.setName(ITEMNAME);
        item.setDescription(ITEMNAME);
        item.setPrice(new BigDecimal(PRICE));

        List<Item> items = new ArrayList<>(Arrays.asList(item, item, item));
        when(itemRepo.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertEquals(200, response.getStatusCodeValue());
        List<Item> itemList = response.getBody();
        assertEquals(item.getPrice(), itemList.get(1).getPrice());
    }
    @Test
    public void checkGetItemsByName() {
        Item item = new Item();
        item.setId(1L);
        item.setName(ITEMNAME);
        item.setDescription(ITEMNAME);
        item.setPrice(new BigDecimal(PRICE));

        List<Item> items = new ArrayList<>(Arrays.asList(item, item, item));
        when(itemRepo.findByName(ITEMNAME)).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(ITEMNAME);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> itemList = response.getBody();
        assertEquals(ITEMNAME, itemList.get(0).getName());
    }


}