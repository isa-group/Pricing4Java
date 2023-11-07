package io.github.isagroup.services.parsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.isagroup.exceptions.FeatureNotFoundException;
import io.github.isagroup.exceptions.InvalidDefaultValueException;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.featuretypes.Payment;
import io.github.isagroup.models.featuretypes.PaymentType;

public class PlanParser {

    private PlanParser(){}

    public static Plan parseMapToPlan(String planName, Map<String, Object> map, PricingManager pricingManager){
        
        Plan plan = new Plan();

        plan.setDescription((String) map.get("description"));
        plan.setMonthlyPrice((Double) map.get("monthlyPrice"));
        plan.setAnnualPrice((Double) map.get("annualPrice"));
        plan.setUnit((String) map.get("unit"));

        setFeaturesToPlan(planName, map, pricingManager, plan);
        setUsageLimitsToPlan(planName, map, pricingManager, plan);
        
        return plan;
    }

    private static void setFeaturesToPlan(String planName, Map<String, Object> map, PricingManager pricingManager, Plan plan){
        Map<String, Object> planFeaturesMap = (Map<String, Object>) map.get("features");
        Map<String, Feature> globalFeaturesMap = pricingManager.getFeatures();
        Map<String, Feature> planFeatures = new HashMap<>();
        
        for (String globalFeatureName: globalFeaturesMap.keySet()){
            Feature globalFeature = globalFeaturesMap.get(globalFeatureName);
            try {
                planFeatures.put(globalFeatureName, Feature.cloneFeature(globalFeature));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        plan.setFeatures(planFeatures);

        if (planFeaturesMap == null){
            return;
        }

        for (String planFeatureName: planFeaturesMap.keySet()){
            
            Map<String, Object> planFeatureMap = (Map<String, Object>) planFeaturesMap.get(planFeatureName);
            
            if (!plan.getFeatures().containsKey(planFeatureName)){
                throw new FeatureNotFoundException("The feature " + planFeatureName + " is not defined in the global features");
            }else{
                Feature feature = plan.getFeatures().get(planFeatureName);

                switch (feature.getValueType()) {
                    case NUMERIC:
                        feature.setValue(planFeatureMap.get("value"));
                        if (!(feature.getValue() instanceof Integer || feature.getValue() instanceof Double || feature.getValue() instanceof Long)){
                            throw new InvalidDefaultValueException("The feature " + feature + " does not have a valid value. Current valueType:" + feature.getValueType().toString() + "; Current defaultValue: " + planFeatureMap.get("value").toString());
                        }
                        break;
                    case BOOLEAN:
                        feature.setValue((boolean) planFeatureMap.get("value"));
                        break;
                    case TEXT:  
                    
                        if (feature instanceof Payment){
                            parsePaymentValue(feature, planFeatureName, planFeatureMap);
                        }else{
                            feature.setValue((String) planFeatureMap.get("value"));
                        }
                        break;
                }

                plan.getFeatures().put(planFeatureName, feature);
            }
        }
    }

    private static void setUsageLimitsToPlan(String planName, Map<String, Object> map, PricingManager pricingManager, Plan plan){
        Map<String, Object> planUsageLimitsMap = (Map<String, Object>) map.get("usageLimits");
        Map<String, UsageLimit> globalUsageLimitsMap = pricingManager.getUsageLimits();
        Map<String, UsageLimit> planUsageLimits = new HashMap<>();

        for (String globalUsageLimitName: globalUsageLimitsMap.keySet()){
            UsageLimit globalUsageLimit = globalUsageLimitsMap.get(globalUsageLimitName);
            try {
                planUsageLimits.put(globalUsageLimitName, UsageLimit.cloneUsageLimit(globalUsageLimit));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        plan.setUsageLimits(planUsageLimits);

        if (planUsageLimitsMap == null){
            return;
        }

        for (String planUsageLimitName: planUsageLimitsMap.keySet()){
            
            Map<String, Object> planUsageLimitMap = (Map<String, Object>) planUsageLimitsMap.get(planUsageLimitName);
            
            if (!plan.getUsageLimits().containsKey(planUsageLimitName)){
                throw new FeatureNotFoundException("The usageLimit " + planUsageLimitName + " is not defined in the global usageLimits");
            }else{
                UsageLimit usageLimit = plan.getUsageLimits().get(planUsageLimitName);

                switch (usageLimit.getValueType()) {
                    case NUMERIC:
                        usageLimit.setValue(planUsageLimitMap.get("value"));
                        if (!(usageLimit.getValue() instanceof Integer || usageLimit.getValue() instanceof Double || usageLimit.getValue() instanceof Long || usageLimit.getValue() == null)){
                            throw new InvalidDefaultValueException("The usageLimit " + usageLimit + " does not have a valid value. Current valueType:" + usageLimit.getValueType().toString() + "; Current defaultValue: " + planUsageLimitMap.get("value").toString());
                        }
                        break;
                    case BOOLEAN:
                        usageLimit.setValue((boolean) planUsageLimitMap.get("value"));
                        break;
                    case TEXT:  
                        usageLimit.setValue((String) planUsageLimitMap.get("value"));
                        break;
                }

                plan.getUsageLimits().put(planUsageLimitName, usageLimit);
            }
        }
    }

    public static void parsePaymentValue(Feature feature, String featureName, Map<String, Object> map){
        
        List<String> allowedPaymentTypes = (List<String>) map.get("value");
        for (String type : allowedPaymentTypes){
            try{
                PaymentType.valueOf(type);
            }catch(IllegalArgumentException e){
                throw new InvalidDefaultValueException("The feature " + featureName + " does not have a supported paymentType. PaymentType that generates the issue: " + type);
            }
        }
        
        feature.setValue(allowedPaymentTypes.toString().replace("[", "").replace("]", ""));
        
    }
    
}
