package io.github.isagroup.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

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
    private Map<String, Feature> features;
    private Map<String, UsageLimit> usageLimits;
    private Map<String, Plan> plans;
    private Map<String, AddOn> addOns;

    public Map<String, Object> getPlanUsageLimits(String planName) {
        Map<String, Object> usageLimitsContext = new LinkedHashMap<>();
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

    public static Map<String, Object> serializePricingMananger(PricingManager pricingManager) {

        Map<String, Object> serializedPricingManager = new LinkedHashMap<>();
        serializedPricingManager.put("saasName", pricingManager.getSaasName());
        serializedPricingManager.put("day", pricingManager.getDay());
        serializedPricingManager.put("month", pricingManager.getMonth());
        serializedPricingManager.put("year", pricingManager.getYear());
        serializedPricingManager.put("currency", pricingManager.getCurrency());
        serializedPricingManager.put("hasAnnualPayment", pricingManager.getHasAnnualPayment());
        serializedPricingManager.put("features", serializeFeatures(pricingManager.getFeatures()));
        serializedPricingManager.put("usageLimits", UsageLimit.serializeUsageLimits(pricingManager.getUsageLimits()));
        serializedPricingManager.put("plans", Plan.serializePlans(pricingManager.getPlans()));
        serializedPricingManager.put("addOns", null);
        return serializedPricingManager;
    }

    public static Map<String, Object> serializeFeatures(Map<String, Feature> features) {
        Map<String, Object> serializedFeatures = new LinkedHashMap<>();
        for (Entry<String, Feature> entry : features.entrySet()) {
            serializedFeatures.put(entry.getKey(), entry.getValue().serializeFeature());

        }
        return serializedFeatures;
    }

}
