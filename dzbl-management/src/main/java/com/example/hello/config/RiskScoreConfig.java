package com.example.hello.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RiskScoreConfig {
    // BMI阈值
    public static final double BMI_UNDERWEIGHT = 18.5;
    public static final double BMI_NORMAL = 24.0;
    public static final double BMI_OVERWEIGHT = 28.0;
    
    // BMI分数
    public static final int BMI_SCORE_UNDERWEIGHT = 10;
    public static final int BMI_SCORE_NORMAL = 0;
    public static final int BMI_SCORE_OVERWEIGHT = 15;
    public static final int BMI_SCORE_OBESE = 25;
    
    // 血压阈值（收缩压/舒张压）
    public static final int BP_LOW_SYSTOLIC = 90;
    public static final int BP_LOW_DIASTOLIC = 60;
    public static final int BP_HIGH_NORMAL_SYSTOLIC = 120;
    public static final int BP_HIGH_NORMAL_DIASTOLIC = 80;
    public static final int BP_HIGH_SYSTOLIC = 140;
    public static final int BP_HIGH_DIASTOLIC = 90;
    
    // 血压分数
    public static final int BP_SCORE_LOW = 10;
    public static final int BP_SCORE_NORMAL = 0;
    public static final int BP_SCORE_BORDERLINE = 15;
    public static final int BP_SCORE_HIGH = 25;
    
    // 血脂阈值
    public static final double LIPID_LOW = 3.1;
    public static final double LIPID_NORMAL = 5.2;
    public static final double LIPID_HIGH = 6.2;
    
    // 血脂分数
    public static final int LIPID_SCORE_LOW = 10;
    public static final int LIPID_SCORE_NORMAL = 0;
    public static final int LIPID_SCORE_BORDERLINE = 15;
    public static final int LIPID_SCORE_HIGH = 25;
    
    // 血糖阈值
    public static final double GLUCOSE_LOW = 3.9;
    public static final double GLUCOSE_NORMAL = 6.1;
    public static final double GLUCOSE_HIGH = 7.0;
    
    // 血糖分数
    public static final int GLUCOSE_SCORE_LOW = 10;
    public static final int GLUCOSE_SCORE_NORMAL = 0;
    public static final int GLUCOSE_SCORE_BORDERLINE = 15;
    public static final int GLUCOSE_SCORE_HIGH = 25;
    
    // 风险等级阈值
    public static final int RISK_LOW = 20;
    public static final int RISK_MEDIUM_LOW = 40;
    public static final int RISK_MEDIUM = 60;
    public static final int RISK_MEDIUM_HIGH = 80;
    public static final int RISK_HIGH = 100;
} 