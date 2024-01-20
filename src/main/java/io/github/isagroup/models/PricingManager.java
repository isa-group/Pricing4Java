package io.github.isagroup.models;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Object to model pricing configuration
 */
@Getter
@Setter
public class PricingManager {
    private String saasName;
    private int day;
    private int month;
    private int year;
    private String currency;
    private Boolean hasAnnualPayment;
    private Map<String, Plan> plans;
    private Map<String, Feature> features;
    private Map<String, UsageLimit> usageLimits;
    private Map<String, AddOn> addOns;

    public Map<String, Object> getPlanUsageLimits(String planName) {
        Map<String, Object> usageLimitsContext = new HashMap<>();
        Map<String, UsageLimit> planUsageLimits = this.plans.get(planName).getUsageLimits();

        Map<String, UsageLimit> defaultUsageLimits = this.usageLimits;

        for (String usageLimitName : defaultUsageLimits.keySet()) {
            Object defaultUsageLimitValue = this.usageLimits.get(usageLimitName);
            Object planUsageLimitValue = planUsageLimits.get(usageLimitName);
            boolean planUsageLimitOverwritesDefaultUsageLimit = planUsageLimitValue != null;

            Object currentValue = defaultUsageLimitValue;
            if (planUsageLimitOverwritesDefaultUsageLimit) {
                currentValue = planUsageLimitValue;
            }
            usageLimitsContext.put(usageLimitName, currentValue);
        }

        return usageLimitsContext;
    }

}
