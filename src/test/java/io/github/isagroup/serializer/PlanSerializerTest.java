package io.github.isagroup.serializer;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.isagroup.models.Plan;

public class PlanSerializerTest {

    @Test
    @DisplayName("Optional default values should not be present if not overriden")
    void givenOptionalPropertiesShouldNotSerializeThem() {

        Plan plan = new Plan();
        plan.setPrivate(false);

        Map<String, Object> serializedPlan = plan.serializePlan();

        assertFalse(serializedPlan.containsKey("description"));
        assertFalse(serializedPlan.containsKey("unit"));
        assertFalse(serializedPlan.containsKey("private"));

    }
}
