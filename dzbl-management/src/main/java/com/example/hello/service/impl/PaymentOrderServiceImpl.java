package com.example.hello.service.impl;

import com.example.hello.entity.PaymentOrder;
import com.example.hello.mapper.PaymentOrderMapper;
import com.example.hello.service.PaymentOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class PaymentOrderServiceImpl implements PaymentOrderService {
    
    @Autowired
    private PaymentOrderMapper paymentOrderMapper;
    
    @Override
    public List<PaymentOrder> getAllOrders() {
        return paymentOrderMapper.selectAll();
    }
    
    @Override
    public PageInfo<PaymentOrder> getOrdersByPage(Integer userId, String payStatus, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        PaymentOrder condition = new PaymentOrder();
        condition.setUserId(userId);
        condition.setPayStatus(payStatus);
        List<PaymentOrder> orders = paymentOrderMapper.selectByCondition(condition);
        return new PageInfo<>(orders);
    }
    
    @Override
    public PaymentOrder getOrderById(Integer orderId) {
        return paymentOrderMapper.selectById(orderId);
    }
    
    @Override
    public void addOrder(PaymentOrder order) {
        // 设置默认值
        if (order.getOrderType() == null) {
            order.setOrderType("门诊");
        }
        if (order.getPayStatus() == null) {
            order.setPayStatus("未支付");
        }
        // 验证订单类型
        if (!Arrays.asList("门诊", "住院", "体检", "检查").contains(order.getOrderType())) {
            throw new IllegalArgumentException("无效的订单类型");
        }
        paymentOrderMapper.insert(order);
    }
    
    @Override
    public void updateOrder(PaymentOrder order) {
        // 如果更新支付状态为已支付，设置支付时间
        if ("已支付".equals(order.getPayStatus())) {
            order.setPayTime(LocalDateTime.now());
        }
        // 验证支付方式
        if (order.getPayMethod() != null && 
            !Arrays.asList("微信", "支付宝").contains(order.getPayMethod())) {
            throw new IllegalArgumentException("无效的支付方式");
        }
        paymentOrderMapper.update(order);
    }
    
    @Override
    public void deleteOrder(Integer orderId) {
        paymentOrderMapper.deleteById(orderId);
    }
} 