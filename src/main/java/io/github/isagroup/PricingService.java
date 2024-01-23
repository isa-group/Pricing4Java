package io.github.isagroup;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.ValueType;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;

/**
 * Service that provides methods to manage the pricing configuration.
 */
@Service
public class PricingService {

    @Autowired
    private PricingContext pricingContext;

    public PricingService(PricingContext pricingContext) {
        this.pricingContext = pricingContext;
    }

    // ------------------------- PLAN MANAGEMENT ------------------------- //

    /**
     * Returns the plan of the configuration that matchs the given name.
     * 
     * @param planName name of the plan that must be returned
     * @return The plan of the configuration that matchs the given name
     * @throws IllegalArgumentException if the plan does not exist in the current
     *                                  pricing configuration
     */
    public Plan getPlanFromName(String planName) {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());
        Plan plan = pricingManager.getPlans().get(planName);

        if (plan == null) {
            throw new IllegalArgumentException(
                    "The plan " + planName + " does not exist in the current pricing configuration");
        } else {
            return plan;
        }
    }

    /**
     * Adds a new plan to the current pricing configuration.
     * The plan must not exist and must contain all the
     * features declared on the configuration. It is recommended to use the
     * {@link PricingContext#getFeatures()} method to get the list of features that
     * appear in the configuration.
     * 
     * @param name name of the plan that is going to be added
     * @param plan {@link Plan} object that includes the details of the plan that is
     *             going to be added
     * @throws IllegalArgumentException if the plan does already exist in the
     *                                  current pricing configuration
     */
    @Transactional
    public void addPlanToConfiguration(String name, Plan plan) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Plan> plans = pricingManager.getPlans();

        if (plans.containsKey(name)) {
            throw new IllegalArgumentException(
                    "The plan " + name + " already exists in the current pricing configuration");
        } else {
            plans.put(name, plan);
            pricingManager.setPlans(plans);
            YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
        }
    }

    /**
     * Creates a new global feature in the pricing configuration and adds it to all
     * the plans using its default value.
     * 
     * @param name    name of the feature that is going to be added
     * @param feature {@link Feature} object that includes the details of the
     *                feature that is going to be added
     * @throws IllegalArgumentException if the feature does already exist in the
     *                                  current pricing configuration
     */
    @Transactional
    public void addFeatureToConfiguration(String name, Feature feature) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Feature> features = pricingManager.getFeatures();

        if (features.containsKey(name)) {
            throw new IllegalArgumentException("The feature " + name
                    + " does already exist in the current pricing configuration. Check the features");
        } else {
            feature.setValue(null);
            features.put(name, feature);
            pricingManager.setFeatures(features);
        }

        Map<String, Plan> plans = pricingManager.getPlans();

        for (String planName : plans.keySet()) {

            Plan plan = plans.get(planName);
            Map<String, Feature> planFeatures = plan.getFeatures();

            if (planFeatures.containsKey(name)) {
                throw new IllegalArgumentException("The feature " + name
                        + " does already exist in the current pricing configuration. Check the " + planName + " plan");
            } else {
                Feature newFeature;
                try {
                    newFeature = feature.getClass().newInstance();
                    newFeature.setValue(features.get(name).getDefaultValue());

                    planFeatures.put(name, newFeature);
                    plan.setFeatures(planFeatures);
                    plans.put(planName, plan);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }

        pricingManager.setPlans(plans);

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    /**
     * Modifies a plan's feature value. In order to do that, the plan must exist in
     * the {@link PricingContext}
     * that is being used. A feature with the given feature name must also exist.
     * 
     * @param planName    name of the plan whose feature will suffer the change
     * @param featureName name of the feature that will suffer the change
     * @param value       the new value of the feature. It must be a supported type
     *                    depending on the feature's {@link ValueType} attribute
     * @throws IllegalArgumentException if the plan does not exist in the current
     *                                  pricing configuration
     * @throws IllegalArgumentException if the plan does not contain the feature
     * @throws IllegalArgumentException if the value does not match a supported type
     *                                  depending on the feature's {@link ValueType}
     *                                  attribute
     */
    @Transactional
    public void setPlanFeatureValue(String planName, String featureName, Object value) {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        try {

            Feature selectedPlanFeature = pricingManager.getPlans().get(planName).getFeatures().get(featureName);

            if (selectedPlanFeature == null) {
                throw new IllegalArgumentException(
                        "The plan " + planName + " does not have the feature " + featureName);
            } else if (isNumeric(value) && selectedPlanFeature.getValueType() == ValueType.NUMERIC) {
                selectedPlanFeature.setValue((Integer) value);
            } else if (isText(value) && selectedPlanFeature.getValueType() == ValueType.TEXT) {
                selectedPlanFeature.setValue((String) value);
            } else if (isCondition(value) && selectedPlanFeature.getValueType() == ValueType.BOOLEAN) {
                selectedPlanFeature.setValue((Boolean) value);
            } else {
                throw new IllegalArgumentException(
                        "The value " + value + " is not of the type " + selectedPlanFeature.getValueType());
            }

            pricingManager.getPlans().get(planName).getFeatures().put(featureName, selectedPlanFeature);

            YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());

        } catch (NullPointerException e) {
            throw new IllegalArgumentException(
                    "The plan " + planName + " does not exist in the current pricing configuration");
        }

    }

    /**
     * Modifies a plan's price. In order to do that, the plan must exist in the
     * {@link PricingContext} that is being used.
     * 
     * @param planName name of the plan whose price will suffer the change
     * @param newPrice the new price value of the plan
     * @throws IllegalArgumentException if the plan does not exist in the current
     *                                  pricing configuration
     */
    @Transactional
    public void setPlanPrice(String planName, Double newPrice) {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Plan> plans = pricingManager.getPlans();

        if (!plans.containsKey(planName)) {
            throw new IllegalArgumentException(
                    "There is no plan with the name " + planName + " in the current pricing configuration");
        } else {
            Plan plan = plans.get(planName);
            plan.setMonthlyPrice(newPrice);
            plans.put(planName, plan);
            pricingManager.setPlans(plans);
            YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
        }

    }

    /**
     * Modifies a feature's expression. In order to do that, the feature must exist
     * in the {@link PricingContext} that is being used.
     * 
     * @param featureName name of the feature whose expression will suffer the
     *                    change
     * @param expression  the new expression to evaluate the feature
     * @throws IllegalArgumentException if the feature does not exist in the current
     *                                  pricing configuration
     */
    @Transactional
    public void setFeatureExpression(String featureName, String expression) {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Feature> features = pricingManager.getFeatures();

        if (!features.containsKey(featureName)) {
            throw new IllegalArgumentException(
                    "There is no feature with the name " + featureName + " in the current pricing configuration");
        } else {
            Feature feature = features.get(featureName);
            feature.setExpression(expression);
            features.put(featureName, feature);
            pricingManager.setFeatures(features);
            YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
        }

    }

    /**
     * Modifies a feature's type. In order to do that, the feature must exist in the
     * {@link PricingContext} that is being used.
     * 
     * @param featureName name of the feature whose type will suffer the change
     * @param newType     the new type of the feature
     * @throws IllegalArgumentException if the feature does not exist in the current
     *                                  pricing configuration
     */
    @Transactional
    public void setValueType(String featureName, ValueType newType) {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Feature> features = pricingManager.getFeatures();

        if (!features.containsKey(featureName)) {
            throw new IllegalArgumentException(
                    "There is no feature with the name " + featureName + " in the current pricing configuration");
        } else {
            Feature feature = features.get(featureName);
            feature.setValueType(newType);
            features.put(featureName, feature);
            pricingManager.setFeatures(features);
            YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
        }

    }

    /**
     * Receives a {@link PricingManager} object and writes it to the pricing
     * configuration file.
     * 
     * @param pricingManager the {@link PricingManager} object that models the
     *                       pricing configuration
     */
    @Transactional
    public void setPricingConfiguration(PricingManager pricingManager) {
        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    /**
     * Removes a plan from the pricing configuration. In order to do that, it must
     * exist in the {@link PricingContext} that is being used.
     * 
     * @param name the name of the plan that is going to be removed
     * @throws IllegalArgumentException if the plan does not exist in the current
     *                                  pricing configuration
     */
    @Transactional
    public void removePlanFromConfiguration(String name) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Plan> plans = pricingManager.getPlans();

        if (!plans.containsKey(name)) {
            throw new IllegalArgumentException(
                    "There is no plan with the name " + name + " in the current pricing configuration");
        } else {
            plans.remove(name);
            pricingManager.setPlans(plans);
            YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
        }
    }

    /**
     * Removes a feature from the pricing configuration.
     * In order to do that, it must exist in the {@link PricingContext} that is
     * being used.
     * The method also removes the feature from all the plans that include it.
     * 
     * @param name the name of the feature that is going to be removed
     * @throws IllegalArgumentException if the feature does not exist in the current
     *                                  pricing configuration
     */
    @Transactional
    public void removeFeatureFromConfiguration(String name) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Feature> features = pricingManager.getFeatures();

        if (!features.containsKey(name)) {
            throw new IllegalArgumentException(
                    "There is no feature with the name " + name + " in the current pricing configuration");
        } else {
            features.remove(name);
            pricingManager.setFeatures(features);
        }

        Map<String, Plan> plans = pricingManager.getPlans();

        for (String planName : plans.keySet()) {

            Plan plan = plans.get(planName);
            Map<String, Feature> planFeatures = plan.getFeatures();

            if (planFeatures.containsKey(name)) {
                planFeatures.remove(name);
                plan.setFeatures(planFeatures);
                plans.put(planName, plan);
            }

        }

        pricingManager.setPlans(plans);

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    // ------------------------- EVALUATION MANAGEMENT ------------------------- //

    private boolean isNumeric(Object value) {
        return value instanceof Integer || value instanceof Double || value instanceof Float || value instanceof Long
                || value instanceof Short || value instanceof Byte;
    }

    private boolean isText(Object value) {
        return value instanceof String;
    }

    private boolean isCondition(Object value) {
        return value instanceof Boolean;
    }

}
