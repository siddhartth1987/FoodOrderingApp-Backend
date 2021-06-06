package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.UtilityProvider;
import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.CategoryItemDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantItemDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.dao.OrderDAO;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

// This Class handles all services for Item.

@Service
public class ItemService {

    @Autowired
    ItemDao itemDao;

    @Autowired
    RestaurantDao restaurantDao;

    @Autowired
    RestaurantItemDao restaurantItemDao;

    @Autowired
    CategoryItemDao categoryItemDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    UtilityProvider utilityProvider;

    @Autowired
    OrderItemDAO orderItemDao;

    @Autowired
    OrderDAO orderDao;


    // This method gets Items By UUID

    public ItemEntity getItemByUUID(String itemUuid) throws ItemNotFoundException {
        ItemEntity itemEntity = itemDao.getItemByUUID(itemUuid);
        if(itemEntity == null){
            throw new ItemNotFoundException("INF-003","No item by this id exist");
        }
        return itemEntity;
    }

    // This method gets Items By Category

    public List<ItemEntity> getItemsByCategory(CategoryEntity categoryEntity) {

        //Calls getItemsByCategory method of categoryItemDao to get the  CategoryItemEntity
        List<CategoryItemEntity> categoryItemEntities = categoryItemDao.getItemsByCategory(categoryEntity);
        List<ItemEntity> itemEntities = new LinkedList<>();
        categoryItemEntities.forEach(categoryItemEntity -> {
            ItemEntity itemEntity = categoryItemEntity.getItem();
            itemEntities.add(itemEntity);
        });
        return itemEntities;
    }

    // This method gets Items By Restaurant & Category

    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {

        //get  RestaurantEntity & CategoryEntity
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryUuid);

        // get items by restaurant
        List<RestaurantItemEntity> restaurantItemEntities = restaurantItemDao.getItemsByRestaurant(restaurantEntity);

        // get items by category
        List<CategoryItemEntity> categoryItemEntities = categoryItemDao.getItemsByCategory(categoryEntity);

        // create list for all items
        List<ItemEntity> itemEntities = new LinkedList<>();

        restaurantItemEntities.forEach(restaurantItemEntity -> {
            categoryItemEntities.forEach(categoryItemEntity -> {
                if(restaurantItemEntity.getItem().equals(categoryItemEntity.getItem())){
                    itemEntities.add(restaurantItemEntity.getItem());
                }
            });
        });

        return itemEntities;

    }

    // This method fetches Items By Popularity
    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {

        // get OrderEntity
        List <OrderEntity> ordersEntities = orderDao.getOrdersByRestaurant(restaurantEntity);

        // list of Items ordered from the restaurant.
        List <ItemEntity> itemEntities = new LinkedList<>();

        ordersEntities.forEach(ordersEntity -> {
            List <OrderItemEntity> orderItemEntities = orderItemDao.getItemsByOrders(ordersEntity);
            orderItemEntities.forEach(orderItemEntity -> {
                itemEntities.add(orderItemEntity.getItem());
            });
        });

        // get frequency of the order.
        Map<String,Integer> itemCountMap = new HashMap<String,Integer>();
        itemEntities.forEach(itemEntity -> { //Looping in to count the frequency of Item ordered correspondingly updating the count.
            Integer count = itemCountMap.get(itemEntity.getUuid());
            itemCountMap.put(itemEntity.getUuid(),(count == null) ? 1 : count+1);
        });

        // sort itemCountMap
        Map<String,Integer> sortedItemCountMap = utilityProvider.sortMapByValues(itemCountMap);

        // create and return a new list of top 5 items

        List<ItemEntity> sortedItemEntites = new LinkedList<>();
        Integer count = 0;
        for(Map.Entry<String,Integer> item:sortedItemCountMap.entrySet()){
            if(count < 5) {
                //Calls getItemByUUID to get the Itemtentity
                sortedItemEntites.add(itemDao.getItemByUUID(item.getKey()));
                count = count+1;
            }else{
                break;
            }
        }

        return sortedItemEntites;
    }

}
