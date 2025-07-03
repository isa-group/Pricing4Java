package io.github.isagroup.services.parsing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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

        // NOTE: dependsOn and excludes will be added later. This is because we need to
        // have all the addOns parsed before we can check if the dependsOn and excludes
        // are valid.

        // ---------- private ----------

        if (addOnMap.get("private") != null) {
            if (!(addOnMap.get("private") instanceof Boolean)) {
                throw new PricingParsingException("The field \"private\" should be a boolean");
            }

            addOn.setIsPrivate((Boolean) addOnMap.get("private"));
        } else {
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

        Object plansAvailable = addOnMap.get("availableFor");
        List<String> plansAvailableList;

        if (plansAvailable == null) {
            // If no plans are defined, the addOn is available for all plans
            plansAvailable = pricingManager.getPlans().keySet().stream().toList();
        }

        if (!(plansAvailable instanceof List<?>)) {
            throw new PricingParsingException("The field \"availableFor\" should be a list");
        }

        if (plansAvailable instanceof List<?>) {
            plansAvailableList = ((List<?>) plansAvailable).stream()
                    .filter(item -> item instanceof String)
                    .map(item -> (String) item)
                    .toList();
        } else {
            throw new PricingParsingException("The field \"availableFor\" should be a list of strings");
        }

        if (plansAvailableList.isEmpty()) {
            plansAvailableList = pricingManager.getPlans().keySet().stream().toList();
        }

        for (String planName : plansAvailableList) {
            if (!pricingManager.getPlans().containsKey(planName)
                    && !pricingManager.getAddOns().containsKey(planName)) {
                throw new InvalidPlanException(
                        "The plan or addOn " + planName + " is not defined in the pricing manager");
            }
        }

        addOn.setAvailableFor(new ArrayList<>(plansAvailableList)); // From java 16, stream().toList() generates an immutable list

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
        Map<String, Feature> addOnFeatures = new LinkedHashMap<>();

        if (addOnFeaturesMap == null) {
            return;
        }

        for (String addOnFeatureName : addOnFeaturesMap.keySet()) {

            Object featureObj = addOnFeaturesMap.get(addOnFeatureName);
            if (!(featureObj instanceof Map)) {
                throw new PricingParsingException("The feature " + addOnFeatureName
                        + " of the add-on " + addOnName + " is not a valid map. Maybe 'value' attribute is missing to set the value of the feature");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> addOnFeatureMap = (Map<String, Object>) featureObj;

            if (!globalFeaturesMap.containsKey(addOnFeatureName)) {
                throw new FeatureNotFoundException(
                        "The feature " + addOnFeatureName + " is not defined in the global features");
            }

            Feature addOnFeature = Feature.cloneFeature(globalFeaturesMap.get(addOnFeatureName));

            Object value = addOnFeatureMap.get("value");
            boolean isValueNull = (value == null);
            
            if (isValueNull){
                throw new InvalidDefaultValueException("The feature " + addOnFeature.getName()
                    + " does not have a valid value. Current valueType: "
                    + addOnFeature.getValueType().toString() + "; Current value in addOn " + addOn.getName() + " is null");
            }

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
            Object usageLimitsExtensionsObj = addOnMap.get("usageLimitsExtensions");
            if (usageLimitsExtensionsObj instanceof Map<?, ?>) {
                addOnUsageLimitsMap = (Map<String, Object>) usageLimitsExtensionsObj;
            } else if (usageLimitsExtensionsObj != null) {
                throw new PricingParsingException("The field \"usageLimitsExtensions\" should be a map. It is currently: "
                        + usageLimitsExtensionsObj.getClass().getSimpleName() + ". "
                        + "Maybe you forgot to add the 'value' attribute to the usage limit in the add-on definition.");
            }
        } else {
            Object usageLimitsObj = addOnMap.get("usageLimits");
            if (usageLimitsObj instanceof Map<?, ?>) {
                addOnUsageLimitsMap = (Map<String, Object>) usageLimitsObj;
            } else if (usageLimitsObj != null) {
                throw new PricingParsingException("The field \"usageLimits\" should be a map. It is currently: "
                        + usageLimitsObj.getClass().getSimpleName() + ". "
                        + "Maybe you forgot to add the 'value' attribute to the usage limit in the add-on definition.");
            }
        }
        Map<String, UsageLimit> globalUsageLimitsMap = pricingManager.getUsageLimits();
        Map<String, UsageLimit> addOnUsageLimits = new LinkedHashMap<>();

        if (addOnUsageLimitsMap == null) {
            return;
        }

        for (String addOnUsageLimitName : addOnUsageLimitsMap.keySet()) {

            Map<String, Object> addOnUsageLimitMap = new LinkedHashMap<>();

            try {
                addOnUsageLimitMap = (Map<String, Object>) addOnUsageLimitsMap.get(addOnUsageLimitName);
            } catch (ClassCastException e) {
                throw new PricingParsingException("The usage limit " + addOnUsageLimitName + " of the add-on "
                        + addOnName
                        + " is not a valid map. Maybe 'value' attribute is missing to set the value of the limit");
            }

            if (!globalUsageLimitsMap.containsKey(addOnUsageLimitName)) {
                throw new FeatureNotFoundException(
                        "The usageLimit " + addOnUsageLimitName + " is not defined in the global features");
            }

            UsageLimit addOnUsageLimit = UsageLimit.cloneUsageLimit(globalUsageLimitsMap.get(addOnUsageLimitName));

            Object value = addOnUsageLimitMap.get("value");
            boolean isValueNull = (value == null);
            
            if (isValueNull){
                throw new InvalidDefaultValueException("The usageLimit " + addOnUsageLimit.getName()
                    + " does not have a valid value. Current valueType: "
                    + addOnUsageLimit.getValueType().toString() + "; Current value in addOn " + addOn.getName() + " is null");
            }

            switch (addOnUsageLimit.getValueType()) {
                case NUMERIC:
                    addOnUsageLimit.setValue(addOnUsageLimitMap.get("value"));
                    if (!(addOnUsageLimit.getValue() instanceof Integer || addOnUsageLimit.getValue() instanceof Double
                            || addOnUsageLimit.getValue() instanceof Long)) {
                        throw new InvalidDefaultValueException("The usageLimit " + addOnUsageLimitName
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

}
