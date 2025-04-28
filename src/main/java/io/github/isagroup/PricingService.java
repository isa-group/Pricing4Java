package io.github.isagroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.isagroup.exceptions.CloneUsageLimitException;
import io.github.isagroup.models.AddOn;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.ValueType;
import io.github.isagroup.models.usagelimittypes.NonRenewable;
import io.github.isagroup.models.usagelimittypes.Renewable;
import io.github.isagroup.models.usagelimittypes.ResponseDriven;
import io.github.isagroup.models.usagelimittypes.TimeDriven;
import io.github.isagroup.services.yaml.YamlUtils;
import io.github.isagroup.utils.PricingValidators;

/**
 * Service that provides methods to manage the pricing configuration.
 */
/**
 * The PricingService class is responsible for managing the pricing configuration and performing operations related to plans and features.
 * It provides methods to retrieve, add, update, and remove plans and features from the pricing configuration.
 */
/**
 * The PricingService class is responsible for managing the pricing configuration and performing operations related to plans and features.
 * It provides methods to retrieve, add, update, and remove plans and features from the pricing configuration.
 */
@Service
public class PricingService {

    @Autowired
    private PricingContext pricingContext;

    private final Map<ValueType, Object> DEFAULT_VALUES = new HashMap<>();

    public PricingService(PricingContext pricingContext) {
        this.pricingContext = pricingContext;

        DEFAULT_VALUES.put(ValueType.BOOLEAN, false);
        DEFAULT_VALUES.put(ValueType.NUMERIC, 0);
        DEFAULT_VALUES.put(ValueType.TEXT, "");
    }

    /**
     * Returns the features defined in the current pricing configuration.
     * @return the features defined 
     * in the current pricing configuration
     */
    public Map<String, Feature> getPricingFeatures() {
        PricingManager pricingManager = pricingContext.getPricingManager();
        return pricingManager.getFeatures();
    }

    /**
     * Returns the usage limits defined in the current pricing configuration.
     * @return the usage limits defined 
     * in the current pricing configuration
     */
    public Map<String, UsageLimit> getPricingUsageLimits() {
        PricingManager pricingManager = pricingContext.getPricingManager();
        return pricingManager.getUsageLimits();
    }
    /**
     * Returns the plans defined in the current pricing configuration.
     * @return the plans defined 
     * in the current pricing configuration
     */
    public Map<String, Plan> getPricingPlans() {
        PricingManager pricingManager = pricingContext.getPricingManager();
        return pricingManager.getPlans();
    }
    /**
     * Returns the add-ons defined in the current pricing configuration.
     * @return the add-ons defined 
     * in the current pricing configuration
     */
    public Map<String, AddOn> getPricingAddOns() {
        PricingManager pricingManager = pricingContext.getPricingManager();
        return pricingManager.getAddOns();
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
     * {@link PricingManager} to get the list of features that
     * appear in the configuration. The same to get
     * the usageLimits.
     * 
     * @param plan {@link Plan} object that includes the details of the plan that is
     *             going to be added
     * @throws IllegalArgumentException if the plan does already exist in the
     *                                  current pricing configuration
     */
    @Transactional
    public void addPlanToConfiguration(Plan plan) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Plan> plans = pricingManager.getPlans();

        if (plans.containsKey(plan.getName())) {
            throw new IllegalArgumentException(
                    "The plan " + plan.getName() + " already exists in the current pricing configuration");
        } else {
            PricingValidators.validateAndFormatPlan(pricingManager, plan);
            plans.put(plan.getName(), plan);
            pricingManager.setPlans(plans);
            YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
        }
    }

    /**
     * Creates a new global feature in the pricing configuration and adds it to all
     * the plans using its default value.
     * 
     * @param feature {@link Feature} object that includes the details of the
     *                feature that is going to be added
     * @throws IllegalArgumentException if the feature does already exist in the
     *                                  current pricing configuration
     */
    @Transactional
    public void addFeatureToConfiguration(Feature feature) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Feature> features = pricingManager.getFeatures();

        if (features.containsKey(feature.getName())) {
            throw new IllegalArgumentException("The feature " + feature.getName()
                    + " does already exist in the current pricing configuration. Check the features");
        } else {
            PricingValidators.validateAndFormatFeature(feature);
            feature.setValue(null);
            features.put(feature.getName(), feature);
            pricingManager.setFeatures(features);
        }

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    /**
     * Updates a feature in the pricing configuration.
     * 
     * @param previousName name of the feature previous to its update
     * @param feature      {@link Feature} object that includes the details of the
     *                     feature that is going to be updated
     */
    @Transactional
    public void updateFeatureFromConfiguration(String previousName, Feature feature) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Feature> features = pricingManager.getFeatures();
        Map<String, UsageLimit> usageLimits = pricingManager.getUsageLimits();
        Map<String, Plan> plans = pricingManager.getPlans();
        Map<String, AddOn> addOns = pricingManager.getAddOns();

        PricingValidators.validateAndFormatFeature(feature);

        if (!features.containsKey(previousName)) {
            throw new IllegalArgumentException(
                    "There is no feature with the name " + previousName + " in the current pricing configuration");
        }

        boolean nameHasChanged = !previousName.equals(feature.getName());
        // The configuration of the feature inside a plan/addOn will be removed if default value or valueType has changed
        boolean valueTypeConsistencyHasChanged = features.get(previousName).getValueType() != feature.getValueType() ||
                features.get(previousName).getDefaultValue() != feature.getDefaultValue();

        if (nameHasChanged) {
            features.remove(previousName);
        }

        features.put(feature.getName(), feature);

        Map<String, UsageLimit> newUsageLimits = nameHasChanged ? updateUsageLimitsWithUpdatedFeature(previousName, feature, usageLimits) : usageLimits;
        Map<String, Plan> newPlans = nameHasChanged || valueTypeConsistencyHasChanged ? removeFeatureFromPlans(previousName, pricingManager) : plans;
        Map<String, AddOn> newAddOns = nameHasChanged || valueTypeConsistencyHasChanged ? removeFeatureFromAddOns(previousName, pricingManager) : addOns;

        pricingManager.setFeatures(features);
        pricingManager.setUsageLimits(newUsageLimits);
        pricingManager.setPlans(newPlans);
        pricingManager.setAddOns(newAddOns);

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    /**
     * Updates a plan in the pricing configuration.
     * @param previousName name of the plan previous to its update
     * @param plan {@link Plan} object that includes the details of the plan that is going to be updated
     */
    @Transactional
    public void updatePlanFromConfiguration(String previousName, Plan plan) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, Plan> plans = pricingManager.getPlans();

        if (!plans.containsKey(previousName)) {
            throw new IllegalArgumentException(
                    "There is no plan with the name " + previousName + " in the current pricing configuration");
        }

        PricingValidators.validateAndFormatPlan(pricingManager, plan);

        if (!previousName.equals(plan.getName())) {
            plans.remove(previousName);
        }

        plans.put(plan.getName(), plan);

        pricingManager.setPlans(plans);

        updateAddOnsWithNewPlan(previousName, plan.getName(), pricingManager);

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
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
            removePlanFromAddOns(name, pricingManager);
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
        }

        if (features.keySet().size() == 1) {
            throw new IllegalStateException("You cannot delete a feature from a one-feature pricing configuration");
        }

        features.remove(name);

        Map<String, UsageLimit> newUsageLimits = removeFeatureFromUsageLimits(name, pricingManager);
        Map<String, Plan> newPlans = removeFeatureFromPlans(name, pricingManager);
        Map<String, AddOn> newAddOns = removeFeatureFromAddOns(name, pricingManager);

        pricingManager.setFeatures(features);
        if (newUsageLimits == null || newUsageLimits.isEmpty()){
            pricingManager.setUsageLimits(null);
        }else{
            pricingManager.setUsageLimits(newUsageLimits);
        }
        if (newPlans == null || newPlans.isEmpty()){
            pricingManager.setPlans(null);
        }else{
            pricingManager.setPlans(newPlans);
        }
        if (newAddOns == null || newAddOns.isEmpty()){
            pricingManager.setAddOns(null);
        }else{
            pricingManager.setAddOns(newAddOns);
        }

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    // ------------------------- USAGE LIMIT MANAGEMENT ------------------------- //

    /**
     * Creates a new global usageLimit within the pricing configuration and adds 
     * it to all the plans using its default value.
     * 
     * @param usageLimit type of usage limit {@link UsageLimit} you want to add
     *                   Possible subclasses are {@link Renewable}
     *                   {@link NonRenewable} {@link TimeDriven} and
     *                   {@link ResponseDriven}
     */
    @Transactional
    public void addUsageLimitToConfiguration(UsageLimit usageLimit) {
        PricingManager pricingManager = pricingContext.getPricingManager();

        Map<String, UsageLimit> usageLimits = pricingManager.getUsageLimits();

        PricingValidators.validateAndFormatUsageLimit(pricingManager, usageLimit);

        if (pricingManager.getUsageLimits().containsKey(usageLimit.getName())) {
            throw new CloneUsageLimitException(
                    "An usage limit with the name " + usageLimit.getName()
                            + " already exists within the pricing configuration");
        }

        usageLimits.put(usageLimit.getName(), usageLimit);

        pricingManager.setUsageLimits(usageLimits);

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());

    }

    /**
     * Update an existing usage limit in the pricing configuration.
     * 
     * @param previousUsageLimitName Usage limit name previous to its update
     * @param usageLimit             type of usage limit {@link UsageLimit} you want
     *                               to add
     *                               Possible subclasses are {@link Renewable}
     *                               {@link NonRenewable} {@link TimeDriven} and
     *                               {@link ResponseDriven}
     */
    @Transactional
    public void updateUsageLimitFromConfiguration(String previousUsageLimitName, UsageLimit usageLimit) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, UsageLimit> usageLimits = pricingManager.getUsageLimits();
        List<String> usageLimitsNames = usageLimits.keySet().stream().collect(Collectors.toList());

        if (!usageLimitsNames.contains(previousUsageLimitName)) {
            throw new IllegalArgumentException(
                    "There is no usage limit with the name " + previousUsageLimitName
                            + " in the current pricing configuration");
        }

        PricingValidators.validateAndFormatUsageLimit(pricingManager, usageLimit);

        // Handle different valueTypes and default values

        if (usageLimit.getValueType() != usageLimits.get(usageLimit.getName()).getValueType() ||
                usageLimit.getDefaultValue() != usageLimits.get(usageLimit.getName()).getDefaultValue()) {
            Map<String, Plan> plans = pricingManager.getPlans();

            for (Entry<String, Plan> planEntry : plans.entrySet()) {
                planEntry.getValue().getUsageLimits().remove(previousUsageLimitName);
            }
        }

        if (!previousUsageLimitName.equals(usageLimit.getName())) {
            usageLimits.remove(previousUsageLimitName);
        }

        usageLimits.put(usageLimit.getName(), usageLimit);

        pricingManager.setUsageLimits(usageLimits);

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    /**
     * Deletes an usage limit from the configuration.
     * 
     * @param name Usage limit name to delete
     * 
     * @throws IllegalArgumentException if usage limit does not exists in the
     *                                  configuration
     */
    @Transactional
    public void removeUsageLimitFromConfiguration(String name) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, UsageLimit> usageLimits = pricingManager.getUsageLimits();

        if (!usageLimits.containsKey(name)) {
            throw new IllegalArgumentException(
                    "There is no usage limit with the name " + name + " in the current pricing configuration");
        }

        usageLimits.remove(name);
        pricingManager.setUsageLimits(usageLimits);

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    // ------------------------- ADD ONS MANAGEMENT ------------------------- //

    /**
     * Adds a new add on to the current pricing configuration. The add on must not 
     * exist and must contain all the features declared on the configuration. 
     * It is recommended to use the {@link PricingManager} to get 
     * the list of features that appear in the configuration. The same to get
     * the usageLimits.
     * 
     * @param addOn AddOn object to add
     * 
     * @throws IllegalArgumentException if the add on already exists whithin the pricing
     *                                  configuration
     */
    @Transactional
    public void addAddOnToConfiguration(AddOn addOn) {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());
        PricingValidators.validateAndFormatAddOn(pricingManager, addOn);

        Map<String, AddOn> addOns = pricingManager.getAddOns();

        if (addOns == null) {
            addOns = new HashMap<>();
        }

        if (addOns.containsKey(addOn.getName())) {
            throw new IllegalArgumentException(
                    "An add-on with the name " + addOn.getName() + " already exists within the pricing configuration");
        }

        addOns.put(addOn.getName(), addOn);

        pricingManager.setAddOns(addOns);

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    /**
     * Updates an add on of the pricing configuration.
     * 
     * @param previousName name of the add on previous to the update
     * @param addOn        AddOn object to add
     * 
     */
    @Transactional
    public void updateAddOnFromConfiguration(String previousName, AddOn addOn) {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());
        List<String> addOnsNames = pricingManager.getAddOns().keySet().stream().collect(Collectors.toList());

        if (!addOnsNames.contains(previousName)) {
            throw new IllegalArgumentException(
                    "There is no add-on with the name " + previousName + " in the current pricing configuration");
        }

        PricingValidators.validateAndFormatAddOn(pricingManager, addOn);

        Map<String, AddOn> addOns = pricingManager.getAddOns();

        if (!previousName.equals(addOn.getName())) {
            addOns.remove(previousName);
        }

        addOns.put(addOn.getName(), addOn);

        pricingManager.setAddOns(addOns);

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    /**
     * Deletes an add on from the configuration.
     * 
     * @param addOnName name of the add on to delete
     * 
     * @throws IllegalArgumentException if add on does not exist whithin the pricing
     *                                  configuration
     */
    @Transactional
    public void removeAddOnFromConfiguration(String addOnName) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContext.getConfigFilePath());

        Map<String, AddOn> addOns = pricingManager.getAddOns();

        if (!addOns.containsKey(addOnName)) {
            throw new IllegalArgumentException(
                    "There is no add-on with the name " + addOnName + " in the current pricing configuration");
        }

        addOns.remove(addOnName);
        pricingManager.setAddOns(addOns);

        YamlUtils.writeYaml(pricingManager, pricingContext.getConfigFilePath());
    }

    // ------------------------- PRIVATE FUNCTIONS ------------------------- //

    private Map<String, UsageLimit> updateUsageLimitsWithUpdatedFeature(String previousName, Feature feature, Map<String, UsageLimit> usageLimits){
        for (UsageLimit usageLimit : usageLimits.values()) {
            if (usageLimit.isLinkedToFeature(previousName)) {
                usageLimit.getLinkedFeatures().remove(previousName);
                usageLimit.getLinkedFeatures().add(feature.getName());
            }
        }
        return usageLimits;
    }

    /**
     * Removes a feature from the usage limits of a {@link PricingManager} object.
     * @param featureName
     * @param pricingManager
     * @return The new set of usage limits
     */
    private Map<String, UsageLimit> removeFeatureFromUsageLimits(String featureName, PricingManager pricingManager){
        
        Map<String, UsageLimit> usageLimits = pricingManager.getUsageLimits();
        List<String> usageLimitsToRemove = new ArrayList<>();

        if (usageLimits == null) {
            return usageLimits;
        }

        for (UsageLimit usageLimit : usageLimits.values()) {
            if (usageLimit.isLinkedToFeature(featureName)) {
                List<String> newLinkedFeatures = usageLimit.getLinkedFeatures().stream()
                        .filter(name -> !name.equals(featureName)).collect(Collectors.toList());
                usageLimit.setLinkedFeatures(newLinkedFeatures.isEmpty() ? null : newLinkedFeatures);
                if (usageLimit.getLinkedFeatures() == null){
                    usageLimitsToRemove.add(usageLimit.getName());
                }else{
                    usageLimits.put(usageLimit.getName(), usageLimit);
                }
            }
        }

        usageLimitsToRemove.forEach(usageLimits::remove);

        removeUsageLimitsFromPlans(usageLimitsToRemove, pricingManager);
        removeUsageLimitsFromAddOns(usageLimitsToRemove, pricingManager);
        
        return usageLimits;
    }

    private Map<String, Plan> removeFeatureFromPlans(String featureName, PricingManager pricingManager){
        
        Map<String, Plan> plans = pricingManager.getPlans();
        List<String> plansToRemove = new ArrayList<>();

        if (plans == null) {
            return plans;
        }

        for (String planName : plans.keySet()) {

            Plan plan = plans.get(planName);
            Map<String, Feature> planFeatures = plan.getFeatures();

            if (planFeatures.containsKey(featureName)) {
                planFeatures.remove(featureName);
                
                if (planFeatures.isEmpty()) {
                    plansToRemove.add(planName);
                } else {
                    plan.setFeatures(planFeatures);
                    plans.put(planName, plan);
                }
            }

        }

        plansToRemove.forEach(plans::remove);
        
        return plans;
        

    }

    private Map<String, AddOn> removeFeatureFromAddOns(String featureName, PricingManager pricingManager){
        
        Map<String, AddOn> addOns = pricingManager.getAddOns();
        List<String> addOnsToRemove = new ArrayList<>();

        if (addOns == null) {
            return addOns;
        }

        for (String addOnName : addOns.keySet()) {

            AddOn addOn = addOns.get(addOnName);
            Map<String, Feature> addOnFeatures = addOn.getFeatures();

            if (addOnFeatures != null && addOnFeatures.containsKey(featureName)) {
                addOnFeatures.remove(featureName);
                if (addOnFeatures.isEmpty()) {
                    addOnsToRemove.add(addOnName);
                } else {
                    addOn.setFeatures(addOnFeatures);
                    addOns.put(addOnName, addOn);
                }
            }

        }

        addOnsToRemove.forEach(addOns::remove);

        return addOns;
    }

    private void removeUsageLimitsFromPlans(List<String> usageLimitsToRemove, PricingManager pricingManager){
        Map<String, Plan> plans = pricingManager.getPlans();

        if (plans == null) {
            return;
        }

        for (Plan plan : plans.values()) {
            for (String usageLimitName : usageLimitsToRemove) {
                plan.getUsageLimits().remove(usageLimitName);
            }
        }

        pricingManager.setPlans(plans);
    }

    private void removeUsageLimitsFromAddOns(List<String> usageLimitsToRemove, PricingManager pricingManager){
        Map<String, AddOn> addOns = pricingManager.getAddOns();

        if (addOns == null) {
            return;
        }

        for (AddOn addOn : addOns.values()) {
            for (String usageLimitName : usageLimitsToRemove) {
                if (addOn.getUsageLimits() != null && addOn.getUsageLimits().get(usageLimitName) != null) {
                    addOn.getUsageLimits().remove(usageLimitName);
                }

                if (addOn.getUsageLimitsExtensions() != null && addOn.getUsageLimitsExtensions().get(usageLimitName) != null){
                    addOn.getUsageLimitsExtensions().remove(usageLimitName);
                }
            }
        }

        pricingManager.setAddOns(addOns);
    }

    private void removePlanFromAddOns(String planName, PricingManager pricingManager){
        Map<String, AddOn> addOns = pricingManager.getAddOns();

        if (addOns == null) {
            return;
        }

        for (AddOn addOn : addOns.values()) {
            if (addOn.getAvailableFor() != null && addOn.getAvailableFor().contains(planName)) {
                addOn.getAvailableFor().remove(planName);
            }

            if (addOn.getAvailableFor().isEmpty()) {
                addOns.remove(addOn.getName());
            }
        }

        pricingManager.setAddOns(addOns);
    }

    private void updateAddOnsWithNewPlan(String previousName, String newName, PricingManager pricingManager){
        Map<String, AddOn> addOns = pricingManager.getAddOns();

        if (addOns == null) {
            return;
        }

        for (AddOn addOn : addOns.values()) {
            if (addOn.getAvailableFor() != null && addOn.getAvailableFor().contains(previousName)) {
                addOn.getAvailableFor().remove(previousName);
                addOn.getAvailableFor().add(newName);
            }
        }

        pricingManager.setAddOns(addOns);
    }
}
