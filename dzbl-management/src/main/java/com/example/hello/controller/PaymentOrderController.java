package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.PaymentOrder;
import com.example.hello.service.PaymentOrderService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/payment-orders")
public class PaymentOrderController {
    
    @Autowired
    private PaymentOrderService paymentOrderService;
    
    @GetMapping
    public Result<List<PaymentOrder>> getAllOrders() {
        return Result.success(paymentOrderService.getAllOrders());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<PaymentOrder>> getOrdersByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String payStatus,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(paymentOrderService.getOrdersByPage(userId, payStatus, page, pageSize));
    }
    
    @GetMapping("/{orderId}")
    public Result<PaymentOrder> getOrderById(@PathVariable Integer orderId) {
        return Result.success(paymentOrderService.getOrderById(orderId));
    }
    
    @PostMapping
    public Result<?> addOrder(@RequestBody PaymentOrder order) {
        if (order.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (order.getAmount() == null || order.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("订单金额必须大于0");
        }
        if (order.getOrderType() != null && 
            !Arrays.asList("门诊", "住院", "体检", "检查").contains(order.getOrderType())) {
            return Result.error("无效的订单类型");
        }
        
        paymentOrderService.addOrder(order);
        return Result.success(null);
    }
    
    @PutMapping("/{orderId}")
    public Result<?> updateOrder(@PathVariable Integer orderId, @RequestBody PaymentOrder order) {
        if (order.getPayMethod() != null && 
            !Arrays.asList("微信", "支付宝").contains(order.getPayMethod())) {
            return Result.error("无效的支付方式");
        }
        
        if ("已支付".equals(order.getPayStatus()) && order.getPayTime() == null) {
            order.setPayTime(LocalDateTime.now());
        }

        order.setOrderId(orderId);
        paymentOrderService.updateOrder(order);
        return Result.success(null);
    }
    
    @DeleteMapping("/{orderId}")
    public Result<?> deleteOrder(@PathVariable Integer orderId) {
        paymentOrderService.deleteOrder(orderId);
        return Result.success(null);
    }
} 