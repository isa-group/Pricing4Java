package io.github.isagroup.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class AddOn {

    private String name;
    private List<String> availableFor;
    private Object price;
    private Object monthlyPrice;
    private Object annualPrice;
    private String unit;
    private Map<String, Feature> features;
    private Map<String, UsageLimit> usageLimits;
    private Map<String, UsageLimit> usageLimitsExtensions;

    public Map<String, Object> serializeAddOn() {
        Map<String, Object> serializedAddOn = new LinkedHashMap<>();

        if (availableFor != null && !availableFor.isEmpty()) {
            serializedAddOn.put("availableFor", availableFor);
        }

        if (price != null) {
            serializedAddOn.put("price", price);
        }

        if (monthlyPrice != null) {
            serializedAddOn.put("monthlyPrice", monthlyPrice);
        }

        if (annualPrice != null) {
            serializedAddOn.put("annualPrice", annualPrice);
        }

        if (unit != null) {
            serializedAddOn.put("unit", unit);
        }

        Map<String, Object> features = serializeFeatures().orElse(null);
        Map<String, Object> usageLimits = serializeUsageLimits().orElse(null);
        Map<String, Object> usageLimitExtensions = serializeUsageLimitExtensions().orElse(null);

        serializedAddOn.put("features", features);
        serializedAddOn.put("usageLimits", usageLimits);
        serializedAddOn.put("usageLimitExtensions", usageLimitExtensions);

        return serializedAddOn;
    }

    private <V> Optional<Map<String, V>> serializeValue(V value) {
        if (value == null) {
            return Optional.empty();
        }

        Map<String, V> attributes = new LinkedHashMap<>();
        attributes.put("value", value);
        return Optional.of(attributes);
    }

    private Optional<Map<String, Object>> serializeFeatures() {

        if (features == null) {
            return Optional.empty();
        }

        Map<String, Object> serializedFeatures = new LinkedHashMap<>();
        for (Feature feature : features.values()) {
            Optional<Map<String, Object>> serializedFeature = serializeValue(feature.getValue());
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

    private Optional<Map<String, Object>> serializeUsageLimits() {

        if (usageLimits == null) {
            return Optional.empty();
        }

        Map<String, Object> serializedUsageLimits = new LinkedHashMap<>();

        for (UsageLimit usageLimit : usageLimits.values()) {
            Optional<Map<String, Object>> serializedUsageLimit = serializeValue(usageLimit.getValue());
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

    private Optional<Map<String, Object>> serializeUsageLimitExtensions() {

        if (usageLimits == null) {
            return Optional.empty();
        }

        Map<String, Object> serializedUsageLimitExtensions = new LinkedHashMap<>();

        for (UsageLimit usageLimitExtension : usageLimits.values()) {
            Optional<Map<String, Object>> serializedUsageLimit = serializeValue(usageLimitExtension.getValue());
            if (serializedUsageLimit.isPresent()) {

                serializedUsageLimitExtensions.put(usageLimitExtension.getName(), serializedUsageLimit.get());
            }
        }

        boolean usageLimitMapIsEmpty = serializedUsageLimitExtensions.size() == 0;

        if (usageLimitMapIsEmpty) {
            return Optional.empty();
        }

        return Optional.of(serializedUsageLimitExtensions);
    }

}
