package io.github.isagroup.services.parsing;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.isagroup.exceptions.InvalidDefaultValueException;
import io.github.isagroup.exceptions.InvalidLinkedFeatureException;
import io.github.isagroup.exceptions.InvalidValueTypeException;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;
import io.github.isagroup.models.ValueType;
import io.github.isagroup.models.usagelimittypes.NonRenewable;
import io.github.isagroup.models.usagelimittypes.Renewable;
import io.github.isagroup.models.usagelimittypes.ResponseDriven;
import io.github.isagroup.models.usagelimittypes.TimeDriven;

public class UsageLimitParser {

    private UsageLimitParser() {
    }

    public static UsageLimit parseMapToFeature(String limitName, Map<String, Object> limitMap,
            PricingManager pricingManager) {

        Set<String> featureKeys = pricingManager.getFeatures().keySet();

        try {

            switch (UsageLimitType.valueOf((String) limitMap.get("type"))) {

                case NON_RENEWABLE:
                    return parseMapToNonRenewable(limitName, limitMap, featureKeys);

                case RENEWABLE:
                    return parseMapToRenewable(limitName, limitMap, featureKeys);

                case RESPONSE_DRIVEN:
                    return parseMapToResponseDriven(limitName, limitMap, featureKeys);

                case TIME_DRIVEN:
                    return parseMapToTimeDriven(limitName, limitMap, featureKeys);

                default:
                    return null;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The usage limit " + limitName
                    + " does not have a supported type. Current type value: " + (String) limitMap.get("type"));
        }
    }

    private static NonRenewable parseMapToNonRenewable(String limitName, Map<String, Object> map,
            Set<String> featureKeys) {
        NonRenewable nonRenewable = new NonRenewable();

        loadBasicAttributes(nonRenewable, limitName, map, featureKeys);

        return nonRenewable;
    }

    private static Renewable parseMapToRenewable(String limitName, Map<String, Object> map, Set<String> featureKeys) {
        Renewable renewable = new Renewable();

        loadBasicAttributes(renewable, limitName, map, featureKeys);

        return renewable;
    }

    private static ResponseDriven parseMapToResponseDriven(String limitName, Map<String, Object> map,
            Set<String> featureKeys) {
        ResponseDriven responseDriven = new ResponseDriven();

        loadBasicAttributes(responseDriven, limitName, map, featureKeys);

        return responseDriven;
    }

    private static TimeDriven parseMapToTimeDriven(String limitName, Map<String, Object> map, Set<String> featureKeys) {
        TimeDriven timeDriven = new TimeDriven();

        loadBasicAttributes(timeDriven, limitName, map, featureKeys);

        return timeDriven;
    }

    private static void loadBasicAttributes(UsageLimit limit, String limitName, Map<String, Object> map,
            Set<String> featureKeys) {
        limit.setName(limitName);
        limit.setDescription((String) map.get("description"));
        try {
            limit.setValueType(ValueType.valueOf((String) map.get("valueType")));
        } catch (IllegalArgumentException e) {
            throw new InvalidValueTypeException("The feature " + limitName
                    + " does not have a supported valueType. Current valueType: " + (String) map.get("valueType"));
        }
        try {
            switch (limit.getValueType()) {
                case NUMERIC:
                    limit.setDefaultValue(map.get("defaultValue"));
                    if (!(limit.getDefaultValue() instanceof Integer || limit.getDefaultValue() instanceof Double
                            || limit.getDefaultValue() instanceof Long)) {
                        throw new InvalidDefaultValueException("The usageLimit " + limitName
                                + " does not have a valid defaultValue. Current valueType:"
                                + limit.getValueType().toString() + "; Current defaultValue: "
                                + map.get("defaultValue").toString());
                    }
                    break;
                case BOOLEAN:
                    limit.setDefaultValue((boolean) map.get("defaultValue"));
                    break;
                case TEXT:
                    limit.setDefaultValue((String) map.get("defaultValue"));
                    break;
            }
            if (limit.getDefaultValue() == null) {
                throw new InvalidDefaultValueException("The usageLimit " + limitName
                        + " does not have a valid defaultValue. The actual value is null");
            }
        } catch (ClassCastException e) {
            throw new InvalidDefaultValueException("The feature " + limitName
                    + " does not have a valid defaultValue. Current valueType:" + limit.getValueType().toString()
                    + "; Current defaultValue: " + map.get("defaultValue").toString());
        }
        limit.setUnit((String) map.get("unit"));

        if (map.get("linkedFeatures") == null) {
            limit.setLinkedFeatures(null);
        } else {

            List<String> linkedFeatures = (List<String>) map.get("linkedFeatures");

            for (String linkedFeature : linkedFeatures) {
                if (!featureKeys.contains(linkedFeature)) {
                    throw new InvalidLinkedFeatureException("The feature " + limitName
                            + " is linked to a nonexistent feature. Current linkedFeature: " + linkedFeature);
                }
            }

            limit.setLinkedFeatures(linkedFeatures);
        }
    }

}
