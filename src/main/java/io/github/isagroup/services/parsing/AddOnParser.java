package io.github.isagroup.services.parsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.isagroup.exceptions.FeatureNotFoundException;
import io.github.isagroup.exceptions.InvalidDefaultValueException;
import io.github.isagroup.exceptions.InvalidPlanException;
import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.models.AddOn;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.featuretypes.Payment;
import io.github.isagroup.utils.PricingValidators;

public class AddOnParser {

    private AddOnParser() {
    }

    public static AddOn parseMapToAddOn(String addOnName, Map<String, Object> addOnMap, PricingManager pricingManager) {
        AddOn addOn = new AddOn();

        if (addOnName == null) {
            throw new PricingParsingException("An add on name cannot be null");
        }

        // ---------- name ----------
        addOn.setName(addOnName);
        
        // ---------- description ----------
        addOn.setDescription((String) addOnMap.get("description"));
        
        // ---------- availableFor ----------
        setAvailableFor(addOnMap, pricingManager, addOn);

        // ---------- dependsOn ----------
        setDependsOn(addOnMap, pricingManager, addOn);

        // ---------- excludes ----------
        setExcludes(addOnMap, pricingManager, addOn);

        // ---------- private ----------

        if (addOnMap.get("private") != null) {
            if (!(addOnMap.get("private") instanceof Boolean)) {
                throw new PricingParsingException("The field \"private\" should be a boolean");
            }
            
            addOn.setIsPrivate((Boolean) addOnMap.get("private"));
        }else{
            addOn.setIsPrivate(false);
        }

        // ---------- price ----------

        PricingValidators.checkPriceType(addOnMap.get("price"), addOnName);


        if (addOnMap.get("price") instanceof String && addOnMap.get("price").toString().contains("#")) {
            addOn.setPrice(PricingManagerParser.evaluateFormula(addOnMap.get("price").toString(), pricingManager));
        } else {
            addOn.setPrice(addOnMap.get("price"));
        }

        // ---------- unit ----------
        addOn.setUnit((String) addOnMap.get("unit"));

        // ---------- features ----------
        if (addOnMap.get("features") == null) {
            addOn.setFeatures(null);
        } else {
            setAddOnFeatures(addOnName, addOnMap, pricingManager, addOn);
        }

        // ---------- usageLimits ----------
        if (addOnMap.get("usageLimits") == null) {
            addOn.setUsageLimits(null);
        } else {
            setAddOnUsageLimits(addOnName, addOnMap, pricingManager, addOn, false);
        }

        // ---------- usageLimitsExtensions ----------
        if (addOnMap.get("usageLimitsExtensions") == null) {
            addOn.setUsageLimitsExtensions(null);
        } else {
            setAddOnUsageLimits(addOnName, addOnMap, pricingManager, addOn, true);
        }

        return addOn;
    }

    private static void setAvailableFor(Map<String, Object> addOnMap, PricingManager pricingManager, AddOn addOn) {

        List<String> plansAvailable = (List<String>) addOnMap.get("availableFor");

        for (String planName : plansAvailable) {
            if (!pricingManager.getPlans().containsKey(planName)
                && !pricingManager.getAddOns().containsKey(planName)) {
                throw new InvalidPlanException(
                    "The plan or addOn " + planName + " is not defined in the pricing manager");
            }
        }

        addOn.setAvailableFor(plansAvailable);

    }

    public static void setDependsOn(Map<String, Object> addOnMap, PricingManager pricingManager, AddOn addOn) {

        List<String> dependsOn = (List<String>) addOnMap.get("dependsOn");

        if (dependsOn == null) {
            return;
        }

        for (String addOnName : dependsOn) {
            if (!pricingManager.getAddOns().containsKey(addOnName)) {
                throw new InvalidPlanException(
                    "The addOn " + addOnName + " is not defined in the pricing manager");
            }
        }

        addOn.setDependsOn(dependsOn);

    }

    public static void setExcludes(Map<String, Object> addOnMap, PricingManager pricingManager, AddOn addOn) {

        List<String> excludes = (List<String>) addOnMap.get("excludes");

        if (excludes == null) {
            return;
        }

        for (String addOnName : excludes) {
            if (!pricingManager.getAddOns().containsKey(addOnName)) {
                throw new InvalidPlanException(
                    "The addOn " + addOnName + " is not defined in the pricing manager");
            }
        }

        addOn.setExcludes(excludes);

    }

    private static void setAddOnFeatures(String addOnName, Map<String, Object> addOnMap, PricingManager pricingManager,
                                         AddOn addOn) {
        Map<String, Object> addOnFeaturesMap = (Map<String, Object>) addOnMap.get("features");
        Map<String, Feature> globalFeaturesMap = pricingManager.getFeatures();
        Map<String, Feature> addOnFeatures = new HashMap<>();

        if (addOnFeaturesMap == null) {
            return;
        }

        for (String addOnFeatureName : addOnFeaturesMap.keySet()) {

            Map<String, Object> addOnFeatureMap = (Map<String, Object>) addOnFeaturesMap.get(addOnFeatureName);

            if (!globalFeaturesMap.containsKey(addOnFeatureName)) {
                throw new FeatureNotFoundException(
                    "The feature " + addOnFeatureName + " is not defined in the global features");
            }

            Feature addOnFeature = Feature.cloneFeature(globalFeaturesMap.get(addOnFeatureName));

            switch (addOnFeature.getValueType()) {
                case NUMERIC:
                    addOnFeature.setValue(addOnFeatureMap.get("value"));
                    if (!(addOnFeature.getValue() instanceof Integer || addOnFeature.getValue() instanceof Double
                        || addOnFeature.getValue() instanceof Long)) {
                        throw new InvalidDefaultValueException(
                            "The feature " + addOnFeatureName + " does not have a valid value. Current valueType:"
                                + addOnFeature.getValueType().toString() + "; Current defaultValue: "
                                + addOnFeatureMap.get("value").toString());
                    }
                    break;
                case BOOLEAN:
                    addOnFeature.setValue((boolean) addOnFeatureMap.get("value"));
                    break;
                case TEXT:

                    if (addOnFeature instanceof Payment) {
                        PlanParser.parsePaymentValue(addOnFeature, addOnFeatureName, addOnFeatureMap);
                    } else {
                        addOnFeature.setValue((String) addOnFeatureMap.get("value"));
                    }
                    break;
            }

            addOnFeatures.put(addOnFeatureName, addOnFeature);
        }

        addOn.setFeatures(addOnFeatures);
    }

    private static void setAddOnUsageLimits(String addOnName, Map<String, Object> addOnMap,
                                            PricingManager pricingManager, AddOn addOn, boolean areExtensions) {

        Map<String, Object> addOnUsageLimitsMap = null;

        if (areExtensions) {
            addOnUsageLimitsMap = (Map<String, Object>) addOnMap.get("usageLimitsExtensions");
        } else {
            addOnUsageLimitsMap = (Map<String, Object>) addOnMap.get("usageLimits");
        }
        Map<String, UsageLimit> globalUsageLimitsMap = pricingManager.getUsageLimits();
        Map<String, UsageLimit> addOnUsageLimits = new HashMap<>();

        if (addOnUsageLimitsMap == null) {
            return;
        }

        for (String addOnUsageLimitName : addOnUsageLimitsMap.keySet()) {

            Map<String, Object> addOnUsageLimitMap = new HashMap<>();

            try {
                addOnUsageLimitMap = (Map<String, Object>) addOnUsageLimitsMap.get(addOnUsageLimitName);
            } catch (ClassCastException e) {
                throw new PricingParsingException("The usage limit " + addOnUsageLimitName + " of the add-on "
                    + addOnName + " is not a valid map. Maybe 'value' attribute is missing to set the value of the limit");
            }

            if (!globalUsageLimitsMap.containsKey(addOnUsageLimitName)) {
                throw new FeatureNotFoundException(
                    "The feature " + addOnUsageLimitName + " is not defined in the global features");
            }

            UsageLimit addOnUsageLimit = UsageLimit.cloneUsageLimit(globalUsageLimitsMap.get(addOnUsageLimitName));

            switch (addOnUsageLimit.getValueType()) {
                case NUMERIC:
                    addOnUsageLimit.setValue(addOnUsageLimitMap.get("value"));
                    if (!(addOnUsageLimit.getValue() instanceof Integer || addOnUsageLimit.getValue() instanceof Double
                        || addOnUsageLimit.getValue() instanceof Long)) {
                        throw new InvalidDefaultValueException("The feature " + addOnUsageLimitName
                            + " does not have a valid value. Current valueType:"
                            + addOnUsageLimit.getValueType().toString() + "; Current defaultValue: "
                            + addOnUsageLimitMap.get("value").toString());
                    }
                    break;
                case BOOLEAN:
                    addOnUsageLimit.setValue((boolean) addOnUsageLimitMap.get("value"));
                    break;
                case TEXT:
                    addOnUsageLimit.setValue((String) addOnUsageLimitMap.get("value"));
                    break;
            }

            addOnUsageLimits.put(addOnUsageLimitName, addOnUsageLimit);
        }

        if (areExtensions) {
            addOn.setUsageLimitsExtensions(addOnUsageLimits);
        } else {
            addOn.setUsageLimits(addOnUsageLimits);
        }
    }

    private static boolean isValidPrice(Object price) {
        return price instanceof Double || price instanceof Long || price instanceof Integer || price instanceof String;
    }

}
