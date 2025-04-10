package com.example.hello.service;

import com.example.hello.entity.PaymentOrder;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface PaymentOrderService {
    List<PaymentOrder> getAllOrders();
    PageInfo<PaymentOrder> getOrdersByPage(Integer userId, String payStatus, Integer page, Integer pageSize);
    PaymentOrder getOrderById(Integer orderId);
    void addOrder(PaymentOrder order);
    void updateOrder(PaymentOrder order);
    void deleteOrder(Integer orderId);
} 