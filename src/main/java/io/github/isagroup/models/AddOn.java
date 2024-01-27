package io.github.isagroup.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

        if (features != null) {
            serializedAddOn.put("features", Plan.serializeFeatures(features));
        }

        if (usageLimits != null) {
            serializedAddOn.put("usageLimits", Plan.serializeUsageLimits(usageLimits));
        }

        if (usageLimitsExtensions != null) {
            serializedAddOn.put("usageLimitExtensions", Plan.serializeUsageLimits(usageLimitsExtensions));
        }

        return serializedAddOn;
    }

}
