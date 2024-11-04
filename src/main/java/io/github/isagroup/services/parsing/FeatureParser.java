package io.github.isagroup.services.parsing;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import io.github.isagroup.exceptions.InvalidAutomationTypeException;
import io.github.isagroup.exceptions.InvalidDefaultValueException;
import io.github.isagroup.exceptions.InvalidIntegrationTypeException;
import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.FeatureType;
import io.github.isagroup.models.ValueType;
import io.github.isagroup.models.featuretypes.Automation;
import io.github.isagroup.models.featuretypes.AutomationType;
import io.github.isagroup.models.featuretypes.Domain;
import io.github.isagroup.models.featuretypes.Guarantee;
import io.github.isagroup.models.featuretypes.Information;
import io.github.isagroup.models.featuretypes.Integration;
import io.github.isagroup.models.featuretypes.IntegrationType;
import io.github.isagroup.models.featuretypes.Management;
import io.github.isagroup.models.featuretypes.Payment;
import io.github.isagroup.models.featuretypes.PaymentType;
import io.github.isagroup.models.featuretypes.Support;

public class FeatureParser {

    private FeatureParser() {
    }

    public static Feature parseMapToFeature(String featureName, Map<String, Object> featureMap) {

        if (featureMap.get("type") == null) {
            throw new PricingParsingException("feature 'type' is mandatory");
        }

        try {

            switch (FeatureType.valueOf((String) featureMap.get("type"))) {

                case INFORMATION:
                    return parseMapToInformation(featureName, featureMap);

                case INTEGRATION:
                    return parseMapToIntegration(featureName, featureMap);

                case DOMAIN:
                    return parseMapToDomain(featureName, featureMap);

                case AUTOMATION:
                    return parseMapToAutomation(featureName, featureMap);

                case MANAGEMENT:
                    return parseMapToManagement(featureName, featureMap);

                case GUARANTEE:
                    return parseMapToGuarantee(featureName, featureMap);

                case SUPPORT:
                    return parseMapToSupport(featureName, featureMap);

                case PAYMENT:
                    return parseMapToPayment(featureName, featureMap);

                default:
                    return null;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The feature " + featureName
                    + " does not have a supported feature type. Current value: " + (String) featureMap.get("type"));
        }
    }

    private static Information parseMapToInformation(String featureName, Map<String, Object> map) {
        Information information = new Information();

        loadBasicAttributes(information, featureName, map);

        return information;
    }

    private static Integration parseMapToIntegration(String featureName, Map<String, Object> map) {
        Integration integration = new Integration();

        loadBasicAttributes(integration, featureName, map);

        try {
            integration.setIntegrationType(IntegrationType.valueOf((String) map.get("integrationType")));
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new InvalidIntegrationTypeException(
                    "The feature " + featureName + " does not have a supported integrationType. Current value: "
                            + (String) map.get("integrationType"));
        }

        if (integration.getIntegrationType().equals(IntegrationType.WEB_SAAS)) {
            integration.setPricingUrls((List<String>) map.get("pricingUrls"));
        }

        return integration;
    }

    private static Domain parseMapToDomain(String featureName, Map<String, Object> map) {
        Domain domain = new Domain();

        loadBasicAttributes(domain, featureName, map);

        return domain;
    }

    private static Automation parseMapToAutomation(String featureName, Map<String, Object> map) {
        Automation automation = new Automation();

        loadBasicAttributes(automation, featureName, map);

        try {
            automation.setAutomationType(AutomationType.valueOf((String) map.get("automationType")));
        } catch (IllegalArgumentException e) {
            throw new InvalidAutomationTypeException(
                    "The feature " + featureName + " does not have a supported automationType. Current value: "
                            + (String) map.get("automationType"));
        }

        return automation;
    }

    private static Management parseMapToManagement(String featureName, Map<String, Object> map) {
        Management management = new Management();

        loadBasicAttributes(management, featureName, map);

        return management;
    }

    private static Guarantee parseMapToGuarantee(String featureName, Map<String, Object> map) {
        Guarantee guarantee = new Guarantee();

        loadBasicAttributes(guarantee, featureName, map);

        guarantee.setDocURL((String) map.get("docURL"));

        return guarantee;
    }

    private static Support parseMapToSupport(String featureName, Map<String, Object> map) {
        Support support = new Support();

        loadBasicAttributes(support, featureName, map);

        return support;
    }

    private static Payment parseMapToPayment(String featureName, Map<String, Object> map) {
        Payment payment = new Payment();

        loadBasicAttributes(payment, featureName, map);

        return payment;
    }

    private static void loadBasicAttributes(Feature feature, String featureName, Map<String, Object> map) {

        if (featureName == null) {
            throw new PricingParsingException("A feature cannot have the name null");
        }

        feature.setName(featureName);
        feature.setDescription((String) map.get("description"));

        if (map.get("valueType") == null) {
            throw new PricingParsingException("Feature value type is null");
        }

        try {
            feature.setValueType(ValueType.valueOf((String) map.get("valueType")));
        } catch (IllegalArgumentException e) {
            throw new PricingParsingException("The feature " + featureName
                    + " does not have a supported valueType. Current valueType: " + (String) map.get("valueType"));
        }
        try {
            switch (feature.getValueType()) {
                case NUMERIC:
                    feature.setDefaultValue(map.get("defaultValue"));
                    if (!(feature.getDefaultValue() instanceof Integer || feature.getDefaultValue() instanceof Double
                            || feature.getDefaultValue() instanceof Long)) {
                        throw new InvalidDefaultValueException(
                                "The feature " + featureName + " does not have a valid defaultValue. Current valueType:"
                                        + feature.getValueType().toString() + "; Current defaultValue: "
                                        + map.get("defaultValue").toString());
                    }
                    break;
                case BOOLEAN:
                    feature.setDefaultValue((boolean) map.get("defaultValue"));
                    break;
                case TEXT:
                    if (feature instanceof Payment) {
                        parsePaymentValue(feature, featureName, map);
                    } else {
                        feature.setDefaultValue((String) map.get("defaultValue"));
                    }
                    break;
            }
            
        } catch (ClassCastException e) {
            throw new ClassCastException("The feature " + featureName
                    + " does not have a valid defaultValue. Current valueType:" + feature.getValueType().toString()
                    + "; Current defaultValue: " + (String) map.get("defaultValue"));
        }

        try {
            feature.setExpression((String) map.get("expression"));
            feature.setServerExpression((String) map.get("serverExpression"));
        } catch (NoSuchElementException e) {
            throw new PricingParsingException("The feature " + featureName
                    + " does not have either an evaluation expression or serverExpression.");
        }

        try {
            feature.setTag((String) map.get("tag"));
        } catch (NoSuchElementException e) {
            feature.setTag(null);
        }
    }

    private static void parsePaymentValue(Feature feature, String featureName, Map<String, Object> map) {

        List<String> allowedPaymentTypes = (List<String>) map.get("defaultValue");
        for (String type : allowedPaymentTypes) {
            try {
                PaymentType.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new InvalidDefaultValueException("The feature " + featureName
                        + " does not have a supported paymentType. PaymentType that generates the issue: " + type);
            }
        }

        feature.setDefaultValue(allowedPaymentTypes);

    }
}
