package io.github.isagroup.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.isagroup.models.AddOn;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.serializer.PricingManagerSerializer;
import io.github.isagroup.services.yaml.YamlUtils;

public class AddOnSerializerTest {

    @Test
    @DisplayName("Check that add-on usageLimitsExtensions serializes given a pricing with usageLimitsExtensions")
    void givenAnAddOnShouldSerializeUsageLimitExtensions() {

        String addOnUnderTest = "TEST-ADDON";
        Map<String, Object> addOnUsageLimitExtensions = new LinkedHashMap<>();
        addOnUsageLimitExtensions.put("storageLimit", new LinkedHashMap<>(Map.of("value", 20)));

        PricingManager pm = YamlUtils.retrieveManagerFromYaml(Path.of("serializing", "addOn", "addOn.yml").toString());
        PricingManagerSerializer pms = new PricingManagerSerializer();
        Map<String, Object> actual = pms.serialize(pm);

        Map<String, Map<String, Object>> addOns = (Map<String, Map<String, Object>>) actual.get("addOns");
        Map<String, Map<String, Object>> actualAddOnUsageLimitsExtensions = (Map<String, Map<String, Object>>) addOns
                .get(addOnUnderTest)
                .get("usageLimitsExtensions");

        assertTrue(actual.containsKey("addOns"));
        assertInstanceOf(Map.class, actual);
        assertTrue(addOns.get(addOnUnderTest).containsKey("usageLimitsExtensions"));
        assertTrue(actualAddOnUsageLimitsExtensions.containsKey("storageLimit"));
        assertEquals(addOnUsageLimitExtensions, actualAddOnUsageLimitsExtensions);
    }

    @Test
    @DisplayName("Optional add-on default values should not be dumped")
    void givenAddOnsOptionalDefaultValuesShouldNotSerialize() {

        AddOn addOn = new AddOn();
        addOn.setDescription(null);
        addOn.setIsPrivate(false);
        addOn.setUnit(null);
        addOn.setAvailableFor(new ArrayList<>());
        addOn.setDependsOn(new ArrayList<>());
        addOn.setExcludes(new ArrayList<>());
        addOn.setUnit(null);

        Map<String, Object> serializedAddOn = addOn.serializeAddOn();

        assertFalse(serializedAddOn.containsKey("description"));
        assertFalse(serializedAddOn.containsKey("private"));
        assertFalse(serializedAddOn.containsKey("unit"));
        assertFalse(serializedAddOn.containsKey("availableFor"));
        assertFalse(serializedAddOn.containsKey("dependsOn"));
        assertFalse(serializedAddOn.containsKey("excludes"));

    }

}
