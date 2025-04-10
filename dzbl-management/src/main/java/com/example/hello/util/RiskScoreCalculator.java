package com.example.hello.util;

import com.example.hello.config.RiskScoreConfig;
import com.example.hello.entity.HealthRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RiskScoreCalculator {
    
    /**
     * 计算BMI值
     * @param height 身高(cm)
     * @param weight 体重(kg)
     * @return BMI值
     */
    public BigDecimal calculateBMI(BigDecimal height, BigDecimal weight) {
        if (height == null || weight == null) {
            return null;
        }
        double heightInMeters = height.doubleValue() / 100;
        double bmi = weight.doubleValue() / (heightInMeters * heightInMeters);
        return new BigDecimal(String.format("%.1f", bmi));
    }
    
    /**
     * 计算总体健康风险分数
     * @param record 健康记录
     * @return 风险分数(0-100)
     */
    public Integer calculateRiskScore(HealthRecord record) {
        int score = 0;
        score += calculateBMIScore(record.getBmi());
        score += calculateBloodPressureScore(record.getBloodPressure());
        score += calculateBloodLipidScore(record.getBloodLipid());
        score += calculateBloodGlucoseScore(record.getBloodGlucose());
        return score;
    }
    
    /**
     * 计算BMI风险分数
     * @param bmi BMI值
     * @return 分数(0-25)
     */
    private int calculateBMIScore(BigDecimal bmi) {
        if (bmi == null) {
            return 0;
        }
        
        double value = bmi.doubleValue();
        if (value < RiskScoreConfig.BMI_UNDERWEIGHT) {
            return RiskScoreConfig.BMI_SCORE_UNDERWEIGHT;
        } else if (value < RiskScoreConfig.BMI_NORMAL) {
            return RiskScoreConfig.BMI_SCORE_NORMAL;
        } else if (value < RiskScoreConfig.BMI_OVERWEIGHT) {
            return RiskScoreConfig.BMI_SCORE_OVERWEIGHT;
        } else {
            return RiskScoreConfig.BMI_SCORE_OBESE;
        }
    }
    
    /**
     * 计算血压风险分数
     * @param bloodPressure 血压值(格式：收缩压/舒张压)
     * @return 分数(0-25)
     */
    private int calculateBloodPressureScore(String bloodPressure) {
        if (bloodPressure == null) {
            return 0;
        }
        
        try {
            String[] bp = bloodPressure.split("/");
            if (bp.length != 2) {
                return 0;
            }
            
            int systolic = Integer.parseInt(bp[0]);
            int diastolic = Integer.parseInt(bp[1]);
            
            if (systolic >= RiskScoreConfig.BP_HIGH_SYSTOLIC || 
                diastolic >= RiskScoreConfig.BP_HIGH_DIASTOLIC) {
                return RiskScoreConfig.BP_SCORE_HIGH;
            } else if (systolic >= RiskScoreConfig.BP_HIGH_NORMAL_SYSTOLIC || 
                     diastolic >= RiskScoreConfig.BP_HIGH_NORMAL_DIASTOLIC) {
                return RiskScoreConfig.BP_SCORE_BORDERLINE;
            } else if (systolic < RiskScoreConfig.BP_LOW_SYSTOLIC || 
                     diastolic < RiskScoreConfig.BP_LOW_DIASTOLIC) {
                return RiskScoreConfig.BP_SCORE_LOW;
            }
            return RiskScoreConfig.BP_SCORE_NORMAL;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * 计算血脂风险分数
     * @param bloodLipid 血脂值
     * @return 分数(0-25)
     */
    private int calculateBloodLipidScore(BigDecimal bloodLipid) {
        if (bloodLipid == null) {
            return 0;
        }
        
        double value = bloodLipid.doubleValue();
        if (value > RiskScoreConfig.LIPID_HIGH) {
            return RiskScoreConfig.LIPID_SCORE_HIGH;
        } else if (value > RiskScoreConfig.LIPID_NORMAL) {
            return RiskScoreConfig.LIPID_SCORE_BORDERLINE;
        } else if (value < RiskScoreConfig.LIPID_LOW) {
            return RiskScoreConfig.LIPID_SCORE_LOW;
        }
        return RiskScoreConfig.LIPID_SCORE_NORMAL;
    }
    
    /**
     * 计算血糖风险分数
     * @param bloodGlucose 血糖值
     * @return 分数(0-25)
     */
    private int calculateBloodGlucoseScore(BigDecimal bloodGlucose) {
        if (bloodGlucose == null) {
            return 0;
        }
        
        double value = bloodGlucose.doubleValue();
        if (value > RiskScoreConfig.GLUCOSE_HIGH) {
            return RiskScoreConfig.GLUCOSE_SCORE_HIGH;
        } else if (value > RiskScoreConfig.GLUCOSE_NORMAL) {
            return RiskScoreConfig.GLUCOSE_SCORE_BORDERLINE;
        } else if (value < RiskScoreConfig.GLUCOSE_LOW) {
            return RiskScoreConfig.GLUCOSE_SCORE_LOW;
        }
        return RiskScoreConfig.GLUCOSE_SCORE_NORMAL;
    }
} 