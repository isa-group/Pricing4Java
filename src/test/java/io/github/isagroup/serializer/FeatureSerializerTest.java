package io.github.isagroup.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

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
import io.github.isagroup.models.featuretypes.Support;

class FeatureSerializerTest {

    private Yaml yaml;

    @BeforeEach
    public void setUp() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            DESCRIPTION, VALUE_TYPE, DEFAULT_VALUE, AUTOMATION_TYPE, EXPRESSION,PATH
            Testing bot,TEXT,Bar,BOT,1==1,automation-bot-feature
            Testing filtering,TEXT,Bar,FILTERING,1==1,automation-filtering-feature
            Testing tracking,TEXT,Bar,TRACKING,1==1,automation-tracking-feature
            Testing task automation,TEXT,Bar,TASK_AUTOMATION,1==1,automation-task-automation-feature
            """)
    void givenDifferentAutomationTypesShouldSerializeToMap(String description, ValueType valueType,
            String defaultValue, AutomationType automationType, String expression,
            String path) {

        Automation automation = new Automation();
        automation.setDescription(description);
        automation.setValueType(valueType);
        automation.setDefaultValue(defaultValue);
        automation.setAutomationType(automationType);
        automation.setExpression(expression);

        Map<String, Object> actual = automation.serializeFeature();

        try (FileInputStream file = new FileInputStream(String.format("src/test/resources/serializing/%s.yml", path))) {
            Map<String, Object> expected = yaml.load(file);
            assertEquals(expected, actual);

        } catch (IOException e) {
            fail(String.format("file with path %s was not found", path));
        }

    }

    @Test
    void given_DomainFeature_should_SerializeToMap() {

        Domain domain = new Domain();
        domain.setDescription("Foo");
        domain.setDefaultValue("Bar");
        domain.setExpression("Baz");
        domain.setServerExpression("John");

        Map<String, Object> map = domain.serializeFeature();

        String expected = """
                description: Foo
                defaultValue: Bar
                expression: Baz
                serverExpression: John
                type: DOMAIN
                """;
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    void givenExpressionAndServerExpressionEqualShouldNotSerialize() {

        Domain domain = new Domain();
        domain.setDescription("Foo");
        domain.setValueType(ValueType.TEXT);
        domain.setDefaultValue("Bar");
        domain.setExpression("Baz");

        Map<String, Object> map = domain.serializeFeature();

        String test = """
                description: Foo
                valueType: TEXT
                defaultValue: Bar
                expression: Baz
                type: DOMAIN
                """;

        Map<String, Object> expected = yaml.load(test);

        assertEquals(expected, map);

    }

    @Test
    void given_GuaranteeFeature_should_SerializeToMap() {

        Guarantee guarantee = new Guarantee();
        guarantee.setDescription("Foo");
        guarantee.setDefaultValue("Bar");
        guarantee.setExpression("Baz");
        guarantee.setServerExpression("John");
        guarantee.setDocURL("https://foobar.com");

        Map<String, Object> map = guarantee.serializeFeature();

        String expected = """
                description: Foo
                defaultValue: Bar
                expression: Baz
                serverExpression: John
                type: GUARANTEE
                docUrl: https://foobar.com
                """;
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    void given_InformationFeature_should_SerializeToMap() {

        Information information = new Information();
        information.setDescription("Foo");
        information.setDefaultValue("Bar");
        information.setExpression("Baz");
        information.setServerExpression("John");

        Map<String, Object> map = information.serializeFeature();

        String expected = """
                description: Foo
                defaultValue: Bar
                expression: Baz
                serverExpression: John
                type: INFORMATION
                """;
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    void given_IntegrationFeature_should_SerializeToMap() {

        Integration integration = new Integration();
        integration.setDescription("Foo");
        integration.setDefaultValue("Bar");
        integration.setExpression("Baz");
        integration.setServerExpression("John");
        integration.setIntegrationType(IntegrationType.API);
        List<String> pricignUrls = new LinkedList<>();
        pricignUrls.add("https://foo.com");
        pricignUrls.add("https://bar.com");
        pricignUrls.add("https://baz.com");
        integration.setPricingUrls(pricignUrls);

        Map<String, Object> map = integration.serializeFeature();

        String expected = """
                description: Foo
                defaultValue: Bar
                expression: Baz
                serverExpression: John
                type: INTEGRATION
                integrationType: API
                pricingUrls:
                - https://foo.com
                - https://bar.com
                - https://baz.com
                """;
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    void given_ManagementFeature_should_SerializeToMap() {

        Management management = new Management();
        management.setDescription("Foo");
        management.setDefaultValue("Bar");
        management.setExpression("Baz");
        management.setServerExpression("John");

        Map<String, Object> map = management.serializeFeature();

        String expected = """
                description: Foo
                defaultValue: Bar
                expression: Baz
                serverExpression: John
                type: MANAGEMENT
                """;
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    void given_PaymentFeature_should_SerializeToMap() {

        Payment payment = new Payment();
        payment.setDescription("Foo");
        List<String> paymentOptions = new LinkedList<>();
        paymentOptions.add("CARD");
        paymentOptions.add("GATEWAY");

        payment.setDefaultValue(paymentOptions);
        payment.setExpression("Baz");
        payment.setServerExpression("John");

        Map<String, Object> map = payment.serializeFeature();

        String expected = """
                description: Foo
                defaultValue:
                - CARD
                - GATEWAY
                expression: Baz
                serverExpression: John
                type: PAYMENT
                """;
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    void given_SupportFeature_should_SerializeToMap() {

        Support support = new Support();
        support.setDescription("Foo");
        support.setDefaultValue("Bar");
        support.setExpression("Baz");
        support.setServerExpression("John");

        Map<String, Object> map = support.serializeFeature();

        String expected = """
                description: Foo
                defaultValue: Bar
                expression: Baz
                serverExpression: John
                type: SUPPORT
                """;
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

}
