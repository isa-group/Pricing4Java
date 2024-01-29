package io.github.isagroup.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Object to model pricing plans
 */
@Getter
@Setter
@EqualsAndHashCode
public class Plan {
    private String name;
    private String description;
    private Object monthlyPrice;
    private Object annualPrice;
    private String unit;
    private Map<String, Feature> features;
    private Map<String, UsageLimit> usageLimits;

    private Optional<Map<String, Object>> serializeFeature(Feature feature) {

        if (feature.getValue() == null) {
            return Optional.empty();
        }

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("value", feature.getValue());
        return Optional.of(attributes);
    }

    private Optional<Map<String, Object>> serializeFeatures() {

        Map<String, Object> serializedFeatures = new LinkedHashMap<>();
        for (Feature feature : features.values()) {
            Optional<Map<String, Object>> serializedFeature = serializeFeature(feature);
            if (serializedFeature.isPresent()) {
                serializedFeatures.put(feature.getName(), serializedFeature.get());
            }
        }

        boolean featureMapIsEmpty = serializedFeatures.size() == 0;

        if (featureMapIsEmpty) {
            return Optional.empty();
        }

        return Optional.of(serializedFeatures);
    }

    private Optional<Map<String, Object>> serializeUsageLimit(UsageLimit usageLimit) {

        if (usageLimit.getValue() == null) {
            return Optional.empty();
        }

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("value", usageLimit.getValue());
        return Optional.of(attributes);
    }

    private Optional<Map<String, Object>> serializeUsageLimits() {

        Map<String, Object> serializedUsageLimits = new LinkedHashMap<>();

        for (UsageLimit usageLimit : usageLimits.values()) {
            Optional<Map<String, Object>> serializedUsageLimit = serializeUsageLimit(usageLimit);
            if (serializedUsageLimit.isPresent()) {

                serializedUsageLimits.put(usageLimit.getName(), serializedUsageLimit.get());
            }
        }

        boolean usageLimitMapIsEmpty = serializedUsageLimits.size() == 0;

        if (usageLimitMapIsEmpty) {
            return Optional.empty();
        }

        return Optional.of(serializedUsageLimits);
    }

    public Map<String, Object> serializePlan() {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("description", description);
        attributes.put("monthlyPrice", monthlyPrice);
        attributes.put("annualPrice", monthlyPrice);
        attributes.put("unit", unit);

        Map<String, Object> features = serializeFeatures().orElse(null);
        Map<String, Object> usageLimits = serializeUsageLimits().orElse(null);

        attributes.put("features", features);
        attributes.put("usageLimits", usageLimits);

        return attributes;
    }

    @Override
    public String toString() {
        return "Plan[name=" + name + ", monthlyPrice=" + monthlyPrice + ", annualPrice=" + annualPrice + ", unit="
                + unit + ", features: " + features.get("superAdminRole") + "]";
    }
}
