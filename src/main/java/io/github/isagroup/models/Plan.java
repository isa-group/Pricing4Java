package io.github.isagroup.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Object to model pricing plans
 */
public class Plan {
    private String name;
    private String description;
    private Object price;
    private String unit;
    private Boolean isPrivate;
    private Map<String, Feature> features;
    private Map<String, UsageLimit> usageLimits;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Feature> features) {
        this.features = features;
    }

    public Map<String, UsageLimit> getUsageLimits() {
        return usageLimits;
    }

    public void setUsageLimits(Map<String, UsageLimit> usageLimits) {
        this.usageLimits = usageLimits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plan plan = (Plan) o;
        return Objects.equals(name, plan.name) && Objects.equals(description, plan.description) && Objects.equals(price, plan.price) && Objects.equals(unit, plan.unit) && Objects.equals(isPrivate, plan.isPrivate) && Objects.equals(features, plan.features) && Objects.equals(usageLimits, plan.usageLimits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price, unit, isPrivate, features, usageLimits);
    }

    @Override
    public String toString() {
        return "Plan [name=" + name + ", description=" + description + ", price=" + price + ", unit=" + unit
            + ", isPrivate=" + isPrivate + ", features=" + features + ", usageLimits=" + usageLimits + "]";
    }

    public Map<String, Object> parseToMap() {
        Map<String, Object> planMap = new LinkedHashMap<>();
        planMap.put("name", name);
        planMap.put("description", description);
        planMap.put("price", price);
        planMap.put("unit", unit);
        planMap.put("isPrivate", isPrivate);
        planMap.put("features", features);
        planMap.put("usageLimits", usageLimits);
        return planMap;
    }

    public Map<String, Object> serializePlan() {
        Map<String, Object> attributes = new LinkedHashMap<>();
        if (this.description != null) {
            attributes.put("description", description);
        }

        attributes.put("price", price);
        if (this.unit != null) {
            attributes.put("unit", this.unit);
        }

        if (this.isPrivate != null && this.isPrivate) {
            attributes.put("private", isPrivate);
        }

        attributes.put("features", serializeFeatures().orElse(null));
        attributes.put("usageLimits", serializeUsageLimits().orElse(null));

        return attributes;
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

}
