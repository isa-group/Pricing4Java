package io.github.isagroup.models;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Object to model pricing plans
 */
@Getter
@Setter
public class Plan {
    private String description;
    private Double monthlyPrice;
    private Double annualPrice;
    private String unit;
    private Map<String, Feature> features;
    private Map<String, UsageLimit> usageLimits;

    public String toString(){
        return "Plan[monthlyPrice="+ monthlyPrice + ", annualPrice=" + annualPrice + ", unit=" + unit + ", features: " + features.get("superAdminRole") + "]";
    }
}
