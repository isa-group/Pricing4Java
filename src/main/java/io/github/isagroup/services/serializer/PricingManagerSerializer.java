package io.github.isagroup.services.serializer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import io.github.isagroup.exceptions.SerializerException;
import io.github.isagroup.models.AddOn;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.services.updaters.Version;

public class PricingManagerSerializer {

    private Map<String, Object> serializedPricingManager;

    public PricingManagerSerializer() {
        this.serializedPricingManager = new LinkedHashMap<>();
    }

    public Map<String, Object> serialize(PricingManager pricingManager) throws SerializerException {

        serializedPricingManager.put("version", Version.LATEST.toString());
        serializedPricingManager.put("saasName", pricingManager.getSaasName());
        serializedPricingManager.put("createdAt", pricingManager.getCreatedAt().toString());
        serializedPricingManager.put("tags", pricingManager.getTags());

        if (pricingManager.getStarts() != null) {
            serializedPricingManager.put("starts", pricingManager.getStarts());
        }

        if (pricingManager.getEnds() != null) {
            serializedPricingManager.put("ends", pricingManager.getEnds());
        }

        serializedPricingManager.put("currency", pricingManager.getCurrency());
        serializedPricingManager.put("hasAnnualPayment", pricingManager.getHasAnnualPayment());

        if (pricingManager.getFeatures() == null) {
            throw new SerializerException("Features are null. Filling the pricing with features is mandatory.");
        }

        if (pricingManager.getPlans() == null && pricingManager.getAddOns() == null) {
            throw new SerializerException(
                    "Plans and AddOns are null. You have to set at least one of them.");
        }

        serializedPricingManager.put("features", serializeFeatures(pricingManager));
        serializedPricingManager.put("usageLimits", serializeUsageLimits(pricingManager));
        serializedPricingManager.put("plans", serializePlans(pricingManager));
        serializedPricingManager.put("addOns", serializeAddOns(pricingManager).orElse(null));

        return serializedPricingManager;
    }

    private Map<String, Object> serializeFeatures(PricingManager pricingManager) {

        Map<String, Object> serializedFeatures = new LinkedHashMap<>();
        for (Feature feature : pricingManager.getFeatures().values()) {
            serializedFeatures.put(feature.getName(), feature.serializeFeature());

        }
        return serializedFeatures;
    }

    private Map<String, Object> serializeUsageLimits(PricingManager pricingManager) {
        Map<String, Object> serializedUsageLimits = new LinkedHashMap<>();

        if (pricingManager.getUsageLimits() == null) {
            return null;
        }

        for (UsageLimit usageLimit : pricingManager.getUsageLimits().values()) {
            serializedUsageLimits.put(usageLimit.getName(), usageLimit.serialize());

        }

        return serializedUsageLimits;

    }

    private Map<String, Object> serializePlans(PricingManager pricingManager) {
        Map<String, Object> serializedPlans = new LinkedHashMap<>();
        for (Plan plan : pricingManager.getPlans().values()) {
            serializedPlans.put(plan.getName(), plan.serializePlan());
        }
        return serializedPlans;
    }

    public Optional<Map<String, Object>> serializeAddOns(PricingManager pricingManager) {

        if (pricingManager.getAddOns() == null) {
            return Optional.empty();
        }

        Map<String, Object> serializedAddOns = new LinkedHashMap<>();
        for (AddOn addOn : pricingManager.getAddOns().values()) {
            serializedAddOns.put(addOn.getName(), addOn.serializeAddOn());
        }
        return Optional.of(serializedAddOns);
    }
}
