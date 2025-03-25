package io.github.isagroup.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AddOn {

    private String name;
    private String description;
    private List<String> availableFor;
    private List<String> dependsOn;
    private List<String> excludes;
    private Object price;
    private String unit;
    private Boolean isPrivate;
    private Map<String, Feature> features;
    private Map<String, UsageLimit> usageLimits;
    private Map<String, UsageLimit> usageLimitsExtensions;

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

    public List<String> getAvailableFor() {
        return availableFor;
    }

    public void setAvailableFor(List<String> availableFor) {
        this.availableFor = availableFor;
    }

    public List<String> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<String> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
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

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
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

    public Map<String, UsageLimit> getUsageLimitsExtensions() {
        return usageLimitsExtensions;
    }

    public void setUsageLimitsExtensions(Map<String, UsageLimit> usageLimitsExtensions) {
        this.usageLimitsExtensions = usageLimitsExtensions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((availableFor == null) ? 0 : availableFor.hashCode());
        result = prime * result + ((dependsOn == null) ? 0 : dependsOn.hashCode());
        result = prime * result + ((excludes == null) ? 0 : excludes.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + ((isPrivate == null) ? 0 : isPrivate.hashCode());
        result = prime * result + ((features == null) ? 0 : features.hashCode());
        result = prime * result + ((usageLimits == null) ? 0 : usageLimits.hashCode());
        result = prime * result + ((usageLimitsExtensions == null) ? 0 : usageLimitsExtensions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AddOn other = (AddOn) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (availableFor == null) {
            if (other.availableFor != null)
                return false;
        } else if (!availableFor.equals(other.availableFor))
            return false;
        if (dependsOn == null) {
            if (other.dependsOn != null)
                return false;
        } else if (!dependsOn.equals(other.dependsOn))
            return false;
        if (excludes == null) {
            if (other.excludes != null)
                return false;
        } else if (!excludes.equals(other.excludes))
            return false;
        if (price == null) {
            if (other.price != null)
                return false;
        } else if (!price.equals(other.price))
            return false;
        if (unit == null) {
            if (other.unit != null)
                return false;
        } else if (!unit.equals(other.unit))
            return false;
        if (isPrivate == null) {
            if (other.isPrivate != null)
                return false;
        } else if (!isPrivate.equals(other.isPrivate))
            return false;
        if (features == null) {
            if (other.features != null)
                return false;
        } else if (!features.equals(other.features))
            return false;
        if (usageLimits == null) {
            if (other.usageLimits != null)
                return false;
        } else if (!usageLimits.equals(other.usageLimits))
            return false;
        if (usageLimitsExtensions == null) {
            if (other.usageLimitsExtensions != null)
                return false;
        } else if (!usageLimitsExtensions.equals(other.usageLimitsExtensions))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AddOn [name=" + name + ", description=" + description + ", availableFor=" + availableFor
                + ", dependsOn=" + dependsOn + ", excludes=" + excludes + ", price=" + price + ", unit=" + unit
                + ", isPrivate=" + isPrivate + ", features=" + features + ", usageLimits=" + usageLimits
                + ", usageLimitsExtensions=" + usageLimitsExtensions + "]";
    }

    public Map<String, Object> serializeAddOn() {
        Map<String, Object> serializedAddOn = new LinkedHashMap<>();

        if (description != null) {
            serializedAddOn.put("description", description);
        }

        if (availableFor != null && !availableFor.isEmpty()) {
            serializedAddOn.put("availableFor", availableFor);
        }

        if (dependsOn != null && !dependsOn.isEmpty()) {
            serializedAddOn.put("dependsOn", dependsOn);
        }
        if (excludes != null && !excludes.isEmpty()) {
            serializedAddOn.put("excludes", excludes);
        }

        if (isPrivate != null && isPrivate) {
            serializedAddOn.put("private", isPrivate);
        }

        if (price != null) {
            serializedAddOn.put("price", price);
        }

        if (unit != null) {
            serializedAddOn.put("unit", unit);
        }

        Map<String, Object> features = serializeFeatures().orElse(null);
        Map<String, Object> usageLimits = serializeUsageLimits().orElse(null);
        Map<String, Object> usageLimitExtensions = serializeUsageLimitExtensions().orElse(null);

        serializedAddOn.put("features", features);
        serializedAddOn.put("usageLimits", usageLimits);
        serializedAddOn.put("usageLimitsExtensions", usageLimitExtensions);

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

        if (usageLimitsExtensions == null) {
            return Optional.empty();
        }

        Map<String, Object> serializedUsageLimitExtensions = new LinkedHashMap<>();

        for (UsageLimit usageLimitExtension : usageLimitsExtensions.values()) {
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
