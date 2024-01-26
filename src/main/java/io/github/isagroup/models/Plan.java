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

        if (usageLimits == null) {
            return null;
        }
        for (UsageLimit usageLimit : usageLimits.values()) {
            serializedUsageLimits.put(usageLimit.getName(), serializeUsageLimit(usageLimit));
        }
        return serializedUsageLimits;
    }

    public Map<String, Object> serializePlan() {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("description", description);
        attributes.put("monthlyPrice", monthlyPrice);
        attributes.put("annualPrice", monthlyPrice);
        attributes.put("unit", unit);

        if (features != null) {
            attributes.put("features", serializeFeatures(features));
        }

        if (usageLimits != null) {
            attributes.put("usageLimits", serializeUsageLimits(usageLimits));
        }
        return attributes;
    }

    @Override
    public String toString() {
        return "Plan[name=" + name + ", monthlyPrice=" + monthlyPrice + ", annualPrice=" + annualPrice + ", unit="
                + unit + ", features: " + features.get("superAdminRole") + "]";
    }
}
