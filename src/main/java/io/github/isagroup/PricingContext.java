package io.github.isagroup;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.error.YAMLException;

import io.github.isagroup.exceptions.PricingPlanEvaluationException;
import io.github.isagroup.models.Feature;
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
     * Returns the secret used to encode the JWT.
     * * @return JWT secret String
     */
    public abstract String getJwtSecret();

    /**
     * Returns the expiration time of the JWT in milliseconds
     * 
     * @return JWT expiration time in milliseconds
     */
    public int getJwtExpiration() {
        return 86400000;
    };

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
     * This method should return the object used inside the application to determine
     * the authority of the user inside the JWT.
     * 
     * @return Current user's authorities object
     */
    public abstract Object getUserAuthorities();

    /**
     * This method returns the plan context of the current user, represented by a
     * {@link Map}. It's used to evaluate the pricing plan.
     * 
     * @return current user's plan context
     */
    public final Map<String, Object> getPlanContext() {

        Map<String, Feature> features = this.getPricingManager().getPlans().get(this.getUserPlan()).getFeatures();

        return features.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue()));
    };

    public final Map<String, Object> getUsageLimitsContext() {

        return this.getPricingManager().getPlanUsageLimits(this.getUserPlan());
    }

    /**
     * This method returns the features declared on the pricing configuration.
     * 
     * @return Map with the features
     */
    public final Map<String, Feature> getFeatures() {
        return this.getPricingManager().getFeatures();
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
