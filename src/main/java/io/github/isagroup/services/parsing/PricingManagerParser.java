package io.github.isagroup.services.parsing;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.exceptions.VersionException;
import io.github.isagroup.models.AddOn;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.services.updaters.Version;

public class PricingManagerParser {

    private PricingManagerParser() {
    }

    public static PricingManager parseMapToPricingManager(Map<String, Object> yamlConfigMap) {

        PricingManager pricingManager = new PricingManager();

        setBasicAttributes(yamlConfigMap, pricingManager);
        setFeatures(yamlConfigMap, pricingManager);
        setUsageLimits(yamlConfigMap, pricingManager);
        setPlans(yamlConfigMap, pricingManager);
        setAddOns(yamlConfigMap, pricingManager);

        if (pricingManager.getPlans() == null && pricingManager.getAddOns() == null) {
            throw new PricingParsingException("The pricing manager does not have any plans or add ons");
        }

        return pricingManager;
    }

    private static void setBasicAttributes(Map<String, Object> yamlConfigMap, PricingManager pricingManager) {

        Version version = null;

        try {
            if (yamlConfigMap.get("version") == null) {
                version = Version.V1_0;
            } else if (yamlConfigMap.get("version") instanceof Double
                || yamlConfigMap.get("version") instanceof String) {

                version = Version.version(yamlConfigMap.get("version"));

            } else {
                throw new PricingParsingException(
                    String.format("'version' detected type is %s but 'version' type must be Double or String",
                        yamlConfigMap.get("version").getClass().getSimpleName()));
            }
        } catch (VersionException e) {
            throw new PricingParsingException(e.getMessage());
        }

        pricingManager.setVersion(version);

        if (yamlConfigMap.get("saasName") == null) {
            throw new PricingParsingException("SaasName was not defined");
        }

        if (!(yamlConfigMap.get("saasName") instanceof String)) {
            throw new PricingParsingException("'saasName' has to be a string");
        }

        pricingManager.setSaasName((String) yamlConfigMap.get("saasName"));

        if (yamlConfigMap.get("currency") == null) {
            throw new PricingParsingException("Currency was not defined");
        }

        if (!(yamlConfigMap.get("currency") instanceof String)) {
            throw new PricingParsingException("'currency' has to be a string");
        }

        pricingManager.setCurrency((String) yamlConfigMap.get("currency"));

        if (yamlConfigMap.get("hasAnnualPayment") != null) {
            pricingManager.setHasAnnualPayment((Boolean) yamlConfigMap.get("hasAnnualPayment"));
        }

        if (yamlConfigMap.get("createdAt") == null) {
            throw new PricingParsingException("'createdAt' is mandatory. Check your config file.");
        } else if (yamlConfigMap.get("createdAt") instanceof Date) {
            Date createdAt = (Date) yamlConfigMap.get("createdAt");
            pricingManager.setCreatedAt(createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        } else if (yamlConfigMap.get("createdAt") instanceof String) {
            try {
                pricingManager.setCreatedAt(LocalDate.parse((String) yamlConfigMap.get("createdAt")));

            } catch (DateTimeParseException err) {
                throw new PricingParsingException(
                    String.format(
                        "\"createdAt\" \"%s\" is invalid, use the following yyyy-MM-dd",
                        yamlConfigMap.get("createdAt")));
            }

        } else {
            throw new PricingParsingException(
                String.format(
                    "\"createdAt\" detected type is %s and must be a String or Date formatted like yyyy-MM-dd",
                    yamlConfigMap.get("createdAt").getClass().getSimpleName()));
        }

        if (yamlConfigMap.get("starts") != null && !(yamlConfigMap.get("starts") instanceof Date)) {
            throw new PricingParsingException(String.format("\"starts\" type is %s and must be a Date",
                yamlConfigMap.get("starts").getClass().getSimpleName()));
        }

        if (yamlConfigMap.get("ends") != null && !(yamlConfigMap.get("ends") instanceof Date)) {
            throw new PricingParsingException(String.format("\"ends\" type is %s and must be a Date",
                    yamlConfigMap.get("ends").getClass().getSimpleName()));
        }
        
        if (yamlConfigMap.get("tags") != null && !(yamlConfigMap.get("tags") instanceof List)) {
            throw new PricingParsingException(String.format("\"tags\" type is %s and must be a List",
                    yamlConfigMap.get("tags").getClass().getSimpleName()));
        }

        if (yamlConfigMap.get("tags") != null) {
            pricingManager.setTags((List<String>) yamlConfigMap.get("tags"));
        }

        pricingManager.setStarts((Date) yamlConfigMap.get("starts"));
        pricingManager.setEnds((Date) yamlConfigMap.get("ends"));

        checkVariables(yamlConfigMap);
        pricingManager.setVariables((Map<String, Object>) yamlConfigMap.get("variables"));


    }

    private static void checkVariables(Map<String, Object> yamlConfigMap) {
        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");
        if (yamlConfigMap.get("variables") != null && !(yamlConfigMap.get("variables") instanceof Map)) {
            throw new PricingParsingException("variables must be a map but found " + yamlConfigMap.get("variables").getClass().getSimpleName() + " instead");
        }

        if (yamlConfigMap.get("variables") != null && yamlConfigMap.get("variables") instanceof Map) {
            for (Object key : ((Map<?, ?>) yamlConfigMap.get("variables")).keySet()) {
                if (key == null) {
                    throw new PricingParsingException("null is not a valid key for a variable");
                }
                if (!pattern.matcher(key.toString()).matches()) {
                    throw new PricingParsingException(key + " is not a valid name for a variable. ");
                }
            }
        }


    }

    private static void setFeatures(Map<String, Object> map, PricingManager pricingManager) {


        if (map.get("features") == null) {
            throw new PricingParsingException(
                "'features' is mandatory. It should be a map of features with their correspoding attributes.");
        }

        if (!(map.get("features") instanceof Map)) {
            throw new PricingParsingException(
                "'features' must be a Map but found " + map.get("features").getClass().getSimpleName()
                    + " instead");
        }

        Map<String, Feature> pricingFeatures = new LinkedHashMap<>();
        Map<String, Object> featuresMap = (Map<String, Object>) map.get("features");


        for (String featureName : featuresMap.keySet()) {

            try {
                Map<String, Object> featureMap = (Map<String, Object>) featuresMap.get(featureName);
                Feature feature = FeatureParser.parseMapToFeature(featureName, featureMap);
                pricingFeatures.put(featureName, feature);
            } catch (ClassCastException e) {
                throw new PricingParsingException("The feature " + featureName
                    + " is not defined correctly. All its options must be specified, and it cannot be defined as a key-value pair");
            }
        }

        pricingManager.setFeatures(pricingFeatures);
    }

    private static void setUsageLimits(Map<String, Object> map, PricingManager pricingManager) {
        Map<String, Object> usageLimitsMap = (Map<String, Object>) map.get("usageLimits");
        Map<String, UsageLimit> usageLimits = new LinkedHashMap<>();

        if (usageLimitsMap == null) {
            return;
        }

        for (String limitName : usageLimitsMap.keySet()) {
            Map<String, Object> limitMap = (Map<String, Object>) usageLimitsMap.get(limitName);
            UsageLimit limit = UsageLimitParser.parseMapToFeature(limitName, limitMap, pricingManager);

            usageLimits.put(limitName, limit);
        }

        pricingManager.setUsageLimits(usageLimits);
    }

    private static void setPlans(Map<String, Object> map, PricingManager pricingManager) {

        Map<String, Object> plansMap = new HashMap<>();

        try {
            plansMap = (Map<String, Object>) map.get("plans");
        } catch (ClassCastException e) {
            throw new PricingParsingException(
                "The plans are not defined correctly. It should be a map of plans and their options");
        }

        Map<String, Plan> plans = new LinkedHashMap<>();

        if (plansMap == null) {
            return;
        }

        for (String planName : plansMap.keySet()) {

            Map<String, Object> planMap = (Map<String, Object>) plansMap.get(planName);
            Plan plan = PlanParser.parseMapToPlan(planName, planMap, pricingManager);

            plans.put(planName, plan);
        }

        pricingManager.setPlans(plans);
    }

    private static void setAddOns(Map<String, Object> map, PricingManager pricingManager) {
        Map<String, Object> addOnsMap = (Map<String, Object>) map.get("addOns");

        if (addOnsMap == null) {
            return;
        }

        pricingManager.setAddOns(new LinkedHashMap<>());

        for (String addOnName : addOnsMap.keySet()) {
            Map<String, Object> addOnMap = (Map<String, Object>) addOnsMap.get(addOnName);
            AddOn addOn = AddOnParser.parseMapToAddOn(addOnName, addOnMap, pricingManager);

            pricingManager.getAddOns().put(addOnName, addOn);
        }
    }
}
