package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* This Controller handles endpoints pertaining to Item.
    getTopFiveItemsByPopularity
 */

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    ItemService itemService;

    @Autowired
    RestaurantService restaurantService;

    // The method fetches top 5 items by their popularity
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "/restaurant/{restaurant_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getTopFiveItemsByPopularity (@PathVariable(value = "restaurant_id")final String restaurantUuid) throws RestaurantNotFoundException {

        // get restaurant entity by UUID
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        // get popular items

        List<ItemEntity> itemEntities = itemService.getItemsByPopularity(restaurantEntity);

        // generating response
        ItemListResponse itemListResponse = new ItemListResponse();
        itemEntities.forEach(itemEntity -> {
            ItemList itemList = new ItemList()
                    .id(UUID.fromString(itemEntity.getUuid()))
                    .itemName(itemEntity.getitemName())
                    .price(itemEntity.getPrice())
                    .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().getValue()));
            itemListResponse.add(itemList);
        });

        return new ResponseEntity<ItemListResponse>(itemListResponse,HttpStatus.OK);
    }

}
