package io.github.isagroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.error.YAMLException;

import io.github.isagroup.exceptions.PricingPlanEvaluationException;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.services.yaml.YamlUtils;
import io.github.isagroup.models.AddOn;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;

/**
 * An abstract class from which create a component that adapt the pricing
 * configuration to the application domain
 */
@Component
public abstract class PricingContext {

    private static final Logger logger = LoggerFactory.getLogger(PricingContext.class);

    /**
     * Returns path of the pricing configuration YAML file.
     * This file should be located in the resources folder, and the path should be
     * relative to it.
     * 
     * @return Configuration file path
     */
    public abstract String getConfigFilePath();

    /**
     * Returns the secret used to encode the pricing JWT.
     * * @return JWT secret String
     */
    public abstract String getJwtSecret();

    /**
     * Returns the secret used to encode the authorization JWT.
     * * @return JWT secret String
     */
    public String getAuthJwtSecret() {
        return this.getJwtSecret();
    }

    /**
     * Returns the expiration time of the JWT in milliseconds
     * 
     * @return JWT expiration time in milliseconds
     */
    public int getJwtExpiration() {
        return 86400000;
    }

    /**
     * This method can be used to determine which users are affected
     * by the pricing, so a pricing-driven JWT will be only generated
     * for them.
     * 
     * @return A {@link Boolean} indicating the condition to include, or not,
     *         the pricing evaluation context in the JWT.
     * 
     * @see PricingEvaluatorUtil#generateUserToken
     * 
     */
    public Boolean userAffectedByPricing() {
        return true;
    }

    /**
     * This method should return the user context that will be used to evaluate the
     * pricing plan.
     * It should be considered which users has accessed the service and what
     * information is available.
     * 
     * @return Map with the user context
     */
    public abstract Map<String, Object> getUserContext();

    /**
     * This method should return the plan name of the current user.
     * With this information, the library will be able to build the {@link Plan}
     * object of the user from the configuration.
     * 
     * @return String with the current user's plan name
     */
    public abstract String getUserPlan();

    /**
     * This method should return a list with the name of the add-ons contracted by
     * the current user.
     * With this information, the library will be able to build the subscription of
     * the user
     * from the configuration.
     * 
     * @return List<String> with the current user's contracted add-ons. Add-on names
     *         should be the same as in the pricing configuration file.
     * 
     */
    public abstract List<String> getUserAddOns();

    /**
     * Returns a list with the full subscription contracted by the current user
     * (including plans and add-ons).
     * 
     * Key "plan" contains the plan name of the user.
     * Key "addOns" contains a list with the add-ons contracted by the user.
     * 
     * @return Map<String, Object> with the current user's contracted subscription.
     */
    public final Map<String, Object> getUserSubscription() {
        Map<String, Object> userSubscription = new HashMap<>();

        userSubscription.put("plan", this.getUserPlan());
        userSubscription.put("addOns", this.getUserAddOns());

        return userSubscription;
    }

    /**
     * This method returns the plan context of the current user, represented by a
     * {@link Map}. It's used to evaluate the pricing plan.
     * 
     * @return current user's plan context
     */
    public final Map<String, Object> getPlanContext() {

        Plan plan = this.getPricingManager().getPlans().get(this.getUserPlan());
        Map<String, AddOn> addOnsMap = this.getPricingManager().getAddOns();

        List<AddOn> addOns = new ArrayList<>();

        for (String addOnName : this.getUserAddOns()) {

            AddOn addOn = addOnsMap.get(addOnName);
            if (addOn != null) {
                addOns.add(addOn);
            } else {
                logger.warn(
                        "[WARNING] User add-on {} not found in the pricing configuration. It hasn't been considered in feature evaluation.",
                        addOnName);
            }
        }

        Map<String, Object> planContext = new HashMap<>();

        Map<String, Object> planFeaturesContext = computeFeatureValueMap(plan, addOns);
        planContext.put("features", planFeaturesContext);

        Map<String, Object> planUsageLimitMap = computeUsageLimitValueMap(plan, addOns);
        planContext.put("usageLimits", planUsageLimitMap);

        return planContext;
    }

    /**
     * This method returns the {@link PricingManager} object that is being used to
     * evaluate the pricing plan.
     * 
     * @return PricingManager object
     */
    public final PricingManager getPricingManager() {
        try {
            return YamlUtils.retrieveManagerFromYaml(this.getConfigFilePath());
        } catch (YAMLException e) {
            throw new PricingPlanEvaluationException("Error while parsing YAML file");
        }
    }

    private final Map<String, Object> computeFeatureValueMap(Plan plan, List<AddOn> addOns) {
        Map<String, Object> featureValueMap = new HashMap<>();

        // Add plan features
        featureValueMap.putAll(plan.getFeatures().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getValue() != null ? e.getValue().getValue()
                                : e.getValue().getDefaultValue())));

        // Replace by add-ons features
        for (AddOn addOn : addOns) {
            try {
                Map<String, Feature> addOnFeatures = addOn.getFeatures();

                if (addOnFeatures == null) {
                    continue;
                }

                featureValueMap.putAll(addOnFeatures.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                e -> e.getValue().getValue())));
            } catch (NullPointerException e) {
                throw new PricingPlanEvaluationException("Error while creating evaluation context. Add-on "
                        + addOn.getName() + " do not have a value for all its features.");
            }
        }

        return featureValueMap;
    }

    private final Map<String, Object> computeUsageLimitValueMap(Plan plan, List<AddOn> addOns) {
        Map<String, Object> usageLimitMap = new HashMap<>();

        // Add plan usage limits
        usageLimitMap.putAll(plan.getUsageLimits().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getValue() != null ? e.getValue().getValue()
                                : e.getValue().getDefaultValue())));

        // Replace by add-ons usage limits
        for (AddOn addOn : addOns) {
            try {

                Map<String, UsageLimit> addOnUsageLimits = addOn.getUsageLimits();

                if (addOnUsageLimits == null) {
                    continue;
                }

                usageLimitMap.putAll(addOnUsageLimits.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getValue().getName(),
                                e -> e.getValue().getValue())));
            } catch (NullPointerException e) {
                throw new PricingPlanEvaluationException("Error while creating evaluation context. Add-on "
                        + addOn.getName() + " do not have a value for all its features.");
            }
        }

        // Extend with Add add-ons usage limits extensions
        for (AddOn addOn : addOns) {
            try {

                Map<String, UsageLimit> addOnUsageLimitsExtensions = addOn.getUsageLimitsExtensions();

                if (addOnUsageLimitsExtensions == null) {
                    continue;
                }

                usageLimitMap.putAll(addOnUsageLimitsExtensions.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getValue().getName(),
                                e -> {
                                    Number existingValue = (Number) usageLimitMap.get(e.getValue().getName());
                                    Number extensionValue = (Number) e.getValue().getValue();
                                    if (existingValue == null || extensionValue == null) {
                                        throw new PricingPlanEvaluationException(
                                                "Error while creating evaluation context. Usage limit extension values must be numeric and not null.");
                                    }
                                    return existingValue.doubleValue() + extensionValue.doubleValue();
                                })));
            } catch (NullPointerException e) {
                throw new PricingPlanEvaluationException(
                        "Error while creating evaluation context. It wasn't possible to extend the add-on "
                                + addOn.getName()
                                + ". Please check that the usage limit that youre trying to extend actually exists in the configuration and that it's NUMERIC.");
            }
        }

        return usageLimitMap;
    }
}
