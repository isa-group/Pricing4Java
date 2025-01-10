package io.github.isagroup;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.error.YAMLException;

import io.github.isagroup.exceptions.PricingPlanEvaluationException;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;
import io.github.isagroup.models.Plan;

/**
 * An abstract class from which create a component that adapt the pricing
 * configuration to the application domain
 */
@Component
public abstract class PricingContext {

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
     * This method returns the plan context of the current user, represented by a
     * {@link Map}. It's used to evaluate the pricing plan.
     * 
     * @return current user's plan context
     */
    public final Map<String, Object> getPlanContext() {

        Plan plan = this.getPricingManager().getPlans().get(this.getUserPlan());
        Map<String, Object> planContext = plan.parseToMap();

        Map<String, Object> planFeaturesContext = plan.getFeatures().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getValue() != null ? e.getValue().getValue()
                                : e.getValue().getDefaultValue()));
        planContext.put("features", planFeaturesContext);

        Map<String, Object> planUsageLimitMap = plan.getUsageLimits().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getValue() != null ? e.getValue().getValue()
                                : e.getValue().getDefaultValue()));
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
}
