package io.github.isagroup.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import io.github.isagroup.models.ValueType;
import io.github.isagroup.models.usagelimittypes.Renewable;

class UsageLimitSerializerTest {

    private Yaml yaml;

    @BeforeEach
    void setUp() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    @Test
    void given_Renewable_return_Map() {

        Renewable renewable = new Renewable();
        renewable.setName("Name");
        renewable.setDescription("Foo");
        renewable.setValueType(ValueType.TEXT);
        renewable.setDefaultValue("Bar");
        renewable.setUnit("Baz");
        renewable.setExpression("1=1");

        List<String> linkedFeatures = new LinkedList<>();
        linkedFeatures.add("foo");
        linkedFeatures.add("bar");
        linkedFeatures.add("baz");
        renewable.setLinkedFeatures(linkedFeatures);

        String expected = """
                description: Foo
                valueType: TEXT
                defaultValue: Bar
                unit: Baz
                type: RENEWABLE
                linkedFeatures:
                - foo
                - bar
                - baz
                expression: 1=1
                """;

        String output = yaml.dump(renewable.serialize());

        assertEquals(expected, output);
    }
}
