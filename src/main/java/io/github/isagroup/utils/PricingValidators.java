package io.github.isagroup.utils;

import java.util.List;
import java.util.stream.Collectors;

import io.github.isagroup.exceptions.FeatureNotFoundException;
import io.github.isagroup.exceptions.InvalidDefaultValueException;
import io.github.isagroup.exceptions.InvalidValueTypeException;
import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.models.AddOn;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.ValueType;

public class PricingValidators {

    public static void validateAndFormatFeature(Feature feature) {

        if (feature == null) {
            throw new IllegalArgumentException("A null feature cannot be added to the pricing configuration");
        }

        String item = "feature " + feature.getName();

        validateName(feature.getName(), item);
        if (feature.getName().contains(" ")) {
            feature.setName(parseStringToCamelCase(feature.getName()));
        }
        validateValueType(feature.getValueType(), item);
        validateDefaultValue(feature.getDefaultValue(), feature.getValueType(), item);
        validateValue(feature.getValue(), feature.getValueType(), item);
        validateExpression(feature.getExpression(), item);
        if (feature.getServerExpression() != null)
            validateExpression(feature.getServerExpression(), item);
        validateValueTypeConsistency(feature.getValueType(), feature.getDefaultValue(), feature.getValue(),
                feature.getExpression(), feature.getServerExpression(), item);
    }

    public static void validateAndFormatUsageLimit(PricingManager pricingManager, UsageLimit usageLimit) {

        if (usageLimit == null) {
            throw new IllegalArgumentException("A null usageLimit cannot be added to the pricing configuration");
        }

        String item = "usage limit " + usageLimit.getName();

        validateName(usageLimit.getName(), item);
        if (usageLimit.getName().contains(" ")) {
            usageLimit.setName(parseStringToCamelCase(usageLimit.getName()));
        }
        validateValueType(usageLimit.getValueType(), item);
        validateDefaultValue(usageLimit.getDefaultValue(), usageLimit.getValueType(), item);
        validateUnit(usageLimit.getUnit(), item);
        validateAllFeaturesExist(pricingManager, usageLimit.getLinkedFeatures());

    }

    public static void validateAndFormatPlan(PricingManager pricingManager, Plan plan) {

        if (plan == null) {
            throw new IllegalArgumentException("A null plan cannot be added to the pricing configuration");
        }

        String item = "plan " + plan.getName();

        validateName(plan.getName(), item);
        if (plan.getName().contains(" ")) {
            plan.setName(parseStringToCamelCase(plan.getName()));
        }

        validatePlanPrice(plan);

        validateUnit(plan.getUnit(), item);

        if (plan.getFeatures() != null) {
            validateAllFeaturesExist(pricingManager, plan.getFeatures().keySet().stream().collect(Collectors.toList()));
            for (Feature feature : plan.getFeatures().values()) {
                validateAndFormatFeature(feature);
            }
        }

        if (plan.getUsageLimits() != null) {
            validateAllUsageLimitsExist(pricingManager,
                    plan.getUsageLimits().keySet().stream().collect(Collectors.toList()), item);
            for (UsageLimit usageLimit : plan.getUsageLimits().values()) {
                validateAndFormatUsageLimit(pricingManager, usageLimit);
            }
        }
    }

    public static void validateAndFormatAddOn(PricingManager pricingManager, AddOn addOn) {

        if (addOn == null) {
            throw new IllegalArgumentException("A null add on cannot be added to the pricing configuration");
        }

        String item = "add-on " + addOn.getName();

        validateName(addOn.getName(), item);

        if (addOn.getName().contains(" ")) {
            addOn.setName(parseStringToCamelCase(addOn.getName()));
        }

        validateAddOnPrice(addOn, item);
        validateUnit(addOn.getUnit(), item);
        if (addOn.getFeatures() != null)
            validateAllFeaturesExist(pricingManager,
                    addOn.getFeatures().keySet().stream().collect(Collectors.toList()));
        if (addOn.getUsageLimits() != null)
            validateAllUsageLimitsExist(pricingManager,
                    addOn.getUsageLimits().keySet().stream().collect(Collectors.toList()), item);
        if (addOn.getUsageLimitsExtensions() != null)
            validateAllUsageLimitsExist(pricingManager,
                    addOn.getUsageLimitsExtensions().keySet().stream().collect(Collectors.toList()),
                    item);

    }

    public static void checkPriceType(Object price, String planName) {
        if (price == null) {
            throw new PricingParsingException("plan " + planName + ": \"price\" is mandatory");
        }

        if (!(price instanceof Long || price instanceof Integer ||
            price instanceof Double || price instanceof String)) {
            throw new PricingParsingException("\"price\" is expected to be a real number, a formula or a string");
        }
    }

    private static void validateName(String name, String item) {

        if (name == null) {
            throw new IllegalArgumentException("The " + item + " name must not be null");
        }

        String trimmedName = name.trim();

        if (trimmedName.trim().isEmpty()) {
            throw new IllegalArgumentException("The " + item + " name must not be empty");
        }

        if (trimmedName.length() < 3) {
            throw new IllegalArgumentException("The " + item + " name must have at least 3 characters");
        }

        if (trimmedName.length() > 50) {
            throw new IllegalArgumentException("The " + item + " name must have at most 50 characters");
        }
    }

    private static void validatePlanPrice(Plan plan) {

        if (!isValidPrice(plan.getPrice())) {
            throw new IllegalArgumentException("Invalid price type");
        }

    }

    private static void validateAddOnPrice(AddOn addOn, String item) {

        if (addOn.getPrice() == null) {
            throw new IllegalArgumentException(
                    "Either an " + item + " price or a monthlyPrice/annualPrice configuration must be specified");
        }

        if (addOn.getPrice() != null && !(addOn.getPrice() instanceof Double)) {
            throw new IllegalArgumentException("The " + item + " price must be a double");
        }
    }

    private static void validateValueType(ValueType valueType, String item) {
        if (valueType == null) {
            throw new InvalidValueTypeException("The " + item + " valueType must not be null");
        }

        if (valueType != ValueType.BOOLEAN && valueType != ValueType.NUMERIC && valueType != ValueType.TEXT) {
            throw new InvalidValueTypeException("The " + item + " valueType must be either BOOLEAN, NUMERIC or TEXT");
        }
    }

    private static void validateDefaultValue(Object defaultValue, ValueType valueType, String item) {
        if (defaultValue == null) {
            throw new InvalidDefaultValueException("The " + item + " defaultValue must not be null");
        }

        if (valueType == ValueType.BOOLEAN && !isCondition(defaultValue)) {
            throw new InvalidDefaultValueException(
                    "The " + item + " defaultValue must be a boolean if valueType is BOOLEAN");
        }

        if (valueType == ValueType.NUMERIC && !isNumeric(defaultValue)) {
            throw new InvalidDefaultValueException(
                    "The " + item + " defaultValue must be one of the supported numeric types if valueType is NUMERIC");
        }

        if (valueType == ValueType.TEXT && !isText(defaultValue)) {
            throw new InvalidDefaultValueException(
                    "The " + item + " defaultValue must be a string if valueType is TEXT");
        }
    }

    /**
     * Validates the value based on the specified value type.
     *
     * @param value     The value to be validated.
     * @param valueType The type of value to be validated (BOOLEAN, NUMERIC, TEXT).
     * @param item      The name of the item being validated.
     * @throws IllegalArgumentException If the value is not of the expected type or
     *                                  if it is null.
     */
    private static void validateValue(Object value, ValueType valueType, String item) {

        if (value != null) {
            if (valueType == ValueType.BOOLEAN && !isCondition(value)) {
                throw new IllegalArgumentException("The " + item + " value must be a boolean if valueType is BOOLEAN");
            }

            if (valueType == ValueType.NUMERIC && !isNumeric(value)) {
                throw new IllegalArgumentException(
                        "The " + item + " value must be a one of the supported numeric types if valueType is NUMERIC");
            }

            if (valueType == ValueType.TEXT && !isText(value)) {
                throw new IllegalArgumentException("The " + item + " value must be a string if valueType is TEXT");
            }
        }

    }

    private static void validateUnit(String unit, String item) {
        if (unit == null || unit.length() > 50) {
            throw new IllegalArgumentException(
                    "The " + item + " unit must have at most 10 characters and cannot be null");
        }
    }

    private static void validateAllFeaturesExist(PricingManager pricingManager, List<String> featureNames) {

        for (String featureName : featureNames) {
            if (!pricingManager.getFeatures().containsKey(featureName)) {
                throw new FeatureNotFoundException("The feature " + featureName
                        + " to which you're trying to attach an usage limit does not exist within the pricing");
            }
        }
    }

    private static void validateAllUsageLimitsExist(PricingManager pricingManager, List<String> usageLimitNames,
            String item) {

        for (String usageLimitName : usageLimitNames) {
            if (!pricingManager.getUsageLimits().containsKey(usageLimitName)) {
                throw new IllegalArgumentException("The usage limit " + usageLimitName
                        + " to which you're trying to attach an " + item + " does not exist within the pricing");
            }
        }
    }

    private static void validateExpression(String expression, String item) {
        if (!(expression instanceof String) || expression.length() > 1000) {
            throw new IllegalArgumentException(
                    "The " + item + " expression must have at most 1000 characters and must be a string");
        }
    }

    private static void validateValueTypeConsistency(ValueType valueType, Object defaultValue, Object value,
            String expression,
            String serverExpression, String item) {

        switch (valueType) {
            case BOOLEAN:
                if (!isCondition(defaultValue)) {
                    throw new InvalidDefaultValueException(
                            "The defaultValue of " + item + " must be a boolean if valueType is BOOLEAN");
                }

                if (value != null && !isCondition(value)) {
                    throw new IllegalArgumentException(
                            "The value of " + item + " must be a boolean if valueType is BOOLEAN");
                }

                // if (expression != null && expression.matches(".*[<>=].*")) {
                //     throw new IllegalArgumentException("Expression of " + item
                //             + " should only include the feature value/defaultValue and the operators '&&', '||' and '!', as it is BOOLEAN");
                // }

                // if (serverExpression != null && serverExpression.matches(".*[<>=].*")) {
                //     throw new IllegalArgumentException("ServerExpression of " + item
                //             + " should only include the feature value/defaultValue and the operators '&&', '||' and '!', as it is BOOLEAN");
                // }

                break;
            case NUMERIC:

                if (!isNumeric(defaultValue)) {
                    throw new InvalidDefaultValueException(
                            "The defaultValue of " + item + " must be a numeric type if valueType is NUMERIC");
                }

                if (value != null && !isNumeric(value)) {
                    throw new IllegalArgumentException(
                            "The value of " + item + " must be a numeric type if valueType is NUMERIC");
                }

                if (expression != null && !expression.matches(".*[<>=].*") && !expression.equals("")) {
                    throw new IllegalArgumentException("Expression of " + item
                            + " should include comparison operators such as: <, > or ==, as it is NUMERIC");
                }

                if (serverExpression != null && !serverExpression.matches(".*[<>=].*")
                        && !serverExpression.equals("")) {
                    throw new IllegalArgumentException("ServerExpression of " + item
                            + " should include comparison operators such as: <, > or ==, as it is NUMERIC");
                }

                break;
            case TEXT:

                if (!isText(defaultValue)) {
                    throw new InvalidDefaultValueException(
                            "The defaultValue of " + item + " must be a string if valueType is TEXT");
                }

                if (value != null && !isText(value)) {
                    throw new IllegalArgumentException(
                            "The value of " + item + " must be a string if valueType is TEXT");
                }

                if (expression != null && expression.matches(".*[<>&|].*")) {
                    throw new IllegalArgumentException(
                            "Expression of " + item + " should only include == or != operators, as it is TEXT");
                }

                if (serverExpression != null && serverExpression.matches(".*[<>&|].*")) {
                    throw new IllegalArgumentException(
                            "ServerExpression of " + item + " should only include == or != operators, as it is TEXT");
                }

                break;
            default:
                throw new IllegalArgumentException(
                        "The valueType must be one of the supported types (BOOLEAN, NUMERIC, TEXT)");
        }
    }

    private static boolean isNumeric(Object value) {
        return value instanceof Integer || value instanceof Double || value instanceof Float || value instanceof Long
                || value instanceof Short || value instanceof Byte;
    }

    private static boolean isText(Object value) {
        return value instanceof String;
    }

    private static boolean isCondition(Object value) {
        return value instanceof Boolean;
    }

    private static String parseStringToCamelCase(String text) {
        String[] words = text.split(" ");
        String camelCase = words[0].toLowerCase();

        for (int i = 1; i < words.length; i++) {
            camelCase += words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }

        return camelCase;
    }

    private static boolean isValidPrice(Object price) {
        return price instanceof Double || price instanceof Long || price instanceof Integer || price instanceof String;
    }


}
