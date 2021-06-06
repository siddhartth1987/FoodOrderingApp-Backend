package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDAO;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDAO;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *  Order endpoint service.
 */
@Service
public class OrderService {

    @Autowired
    OrderDAO orderDao;
    @Autowired
    CouponDao couponDao;
    @Autowired
    OrderItemDAO orderItemDao;
    @Autowired
    CustomerDao customerDao;

    /**
     * endpoint to get coupon by coupon name.
     */
    public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {
        if(couponName == null||couponName == ""){
            throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
        }
        CouponEntity couponEntity = couponDao.getCouponByCouponName(couponName);
        if(couponEntity == null){
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        }
        return couponEntity;
    }

    /**
     *  Coupon endpoint to get coupon by uuid.
     */
    public CouponEntity getCouponByCouponId(String couponUuid) throws CouponNotFoundException {
        CouponEntity couponEntity = couponDao.getCouponByCouponId(couponUuid);
        if(couponEntity == null){
            throw new CouponNotFoundException("CPF-002","No coupon by this id");
        }
        return couponEntity;
    }

    /**
     * Order endpoint to save order.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrder(OrderEntity orderEntity) {
        OrderEntity savedOrderEntity = orderDao.saveOrder(orderEntity);
        return savedOrderEntity;
    }

    /**
     *Order item endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        OrderItemEntity savedOrderItemEntity = orderItemDao.saveOrderItem(orderItemEntity);
        return savedOrderItemEntity;
    }

    /**
     *Order by customer endpoint.
     */
    public List<OrderEntity> getOrdersByCustomers(String uuid) {
        CustomerEntity customerEntity = customerDao.getCustomerByUuid(uuid);
        List<OrderEntity> ordersEntities = orderDao.getOrdersByCustomers(customerEntity);
        return ordersEntities;
    }

    /**
     * Get Order item endpoint.
     */
    public List<OrderItemEntity> getOrderItemsByOrder(OrderEntity orderEntity) {
        List<OrderItemEntity> orderItemEntities = orderItemDao.getOrderItemsByOrder(orderEntity);
        return orderItemEntities;
    }
}
