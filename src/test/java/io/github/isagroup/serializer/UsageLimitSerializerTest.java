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

public class UsageLimitSerializerTest {

    private Yaml yaml;

    @BeforeEach
    public void setUp() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    @Test
    public void given_Renewable_return_Map() {

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

        String expected = "description: Foo\n"
                + "valueType: TEXT\n"
                + "defaultValue: Bar\n"
                + "unit: Baz\n"
                + "type: RENEWABLE\n"
                + "linkedFeatures:\n"
                + "- foo\n"
                + "- bar\n"
                + "- baz\n"
                + "expression: 1=1\n";

        String output = yaml.dump(renewable.serialize());

        assertEquals(expected, output);
    }
}
