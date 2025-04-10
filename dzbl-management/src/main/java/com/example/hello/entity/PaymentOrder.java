package com.example.hello.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentOrder {
    private Integer orderId;
    private Integer userId;
    private String orderType;  // 枚举: '门诊','住院','体检','检查'
    private Integer relatedId;
    private BigDecimal amount;
    private String payStatus;  // 枚举: '未支付','已支付'
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    
    private String payMethod;  // 枚举: '微信','支付宝'
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
} 