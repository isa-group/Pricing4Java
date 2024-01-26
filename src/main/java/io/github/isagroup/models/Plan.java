package io.github.isagroup.models;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Object to model pricing plans
 */
@Getter
@Setter
public class Plan {
    private String name;
    private String description;
    private Object monthlyPrice;
    private Object annualPrice;
    private String unit;
    private Map<String, Feature> features;
    private Map<String, UsageLimit> usageLimits;

    public static Map<String, Object> serializeFeature(Feature feature) {
        Map<String, Object> attributes = new LinkedHashMap<>();

        if (feature.getValue() != null) {
            attributes.put("value", feature.getValue());
        }
        return attributes;
    }

    public static Map<String, Object> serializeFeatures(Map<String, Feature> features) {
        Map<String, Object> serializedFeatures = new LinkedHashMap<>();
        for (Feature feature : features.values()) {
            serializedFeatures.put(feature.getName(), serializeFeature(feature));
        }
        return serializedFeatures;
    }

    public static Map<String, Object> serializeUsageLimit(UsageLimit usageLimit) {
        Map<String, Object> attributes = new LinkedHashMap<>();

        if (usageLimit.getValue() != null) {
            attributes.put("value", usageLimit.getValue());
        }
        return attributes;
    }

    public static Map<String, Object> serializeUsageLimits(Map<String, UsageLimit> usageLimits) {
        Map<String, Object> serializedUsageLimits = new LinkedHashMap<>();
        for (UsageLimit usageLimit : usageLimits.values()) {
            serializedUsageLimits.put(usageLimit.getName(), serializeUsageLimit(usageLimit));
        }
        return serializedUsageLimits;
    }

    public static Map<String, Object> serializePlan(Plan plan) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("description", plan.getDescription());
        attributes.put("monthlyPrice", plan.getMonthlyPrice());
        attributes.put("annualPrice", plan.getAnnualPrice());
        attributes.put("unit", plan.getUnit());
        attributes.put("features", serializeFeatures(plan.getFeatures()));
        attributes.put("usageLimits", serializeUsageLimits(plan.getUsageLimits()));
        return attributes;
    }

    public static Map<String, Object> serializePlans(Map<String, Plan> plans) {
        Map<String, Object> serializedPlans = new LinkedHashMap<>();
        for (Plan plan : plans.values()) {
            serializedPlans.put(plan.getName(), serializePlan(plan));
        }
        return serializedPlans;
    }

    @Override
    public String toString() {
        return "Plan[name=" + name + ", monthlyPrice=" + monthlyPrice + ", annualPrice=" + annualPrice + ", unit="
                + unit + ", features: " + features.get("superAdminRole") + "]";
    }
}
