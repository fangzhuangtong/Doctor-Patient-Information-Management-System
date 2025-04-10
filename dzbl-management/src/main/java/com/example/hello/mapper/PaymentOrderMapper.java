package com.example.hello.mapper;

import com.example.hello.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PaymentOrderMapper {
    List<PaymentOrder> selectAll();
    List<PaymentOrder> selectByCondition(PaymentOrder order);
    PaymentOrder selectById(Integer orderId);
    int insert(PaymentOrder order);
    int update(PaymentOrder order);
    int deleteById(Integer orderId);
} 