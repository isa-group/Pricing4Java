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

    public static Map<String, Object> serializeAddOn(AddOn addOn) {
        Map<String, Object> serializedAddOn = new LinkedHashMap<>();
        if (addOn.getAvailableFor() != null && !addOn.getAvailableFor().isEmpty()) {
            serializedAddOn.put("availableFor", addOn.getAvailableFor());
        }

        if (addOn.getPrice() != null) {
            serializedAddOn.put("price", addOn.getPrice());
        }

        if (addOn.getMonthlyPrice() != null) {
            serializedAddOn.put("monthlyPrice", addOn.getMonthlyPrice());
        }

        if (addOn.getAnnualPrice() != null) {
            serializedAddOn.put("annualPrice", addOn.getAnnualPrice());
        }

        if (addOn.getUnit() != null) {
            serializedAddOn.put("unit", addOn.getUnit());
        }

        if (addOn.getFeatures() != null) {
            serializedAddOn.put("features", Plan.serializeFeatures(addOn.getFeatures()));
        }

        if (addOn.getUsageLimits() != null) {
            serializedAddOn.put("usageLimits", Plan.serializeUsageLimits(addOn.getUsageLimits()));
        }

        if (addOn.getUsageLimitsExtensions() != null) {
            serializedAddOn.put("usageLimitExtensions", Plan.serializeUsageLimits(addOn.getUsageLimits()));
        }

        return serializedAddOn;
    }

    public static Map<String, Object> serializeAddOns(Map<String, AddOn> addOns) {
        Map<String, Object> serializedAddOns = new LinkedHashMap<>();

        if (addOns == null) {
            return null;
        }

        for (AddOn addOn : addOns.values()) {
            serializedAddOns.put(addOn.getName(), serializeAddOn(addOn));
        }
        return serializedAddOns;
    }
}
