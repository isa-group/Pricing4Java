package io.github.isagroup.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import io.github.isagroup.models.PricingManager;
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
import io.github.isagroup.services.yaml.YamlUtils;

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
    @DisplayName("Given a Domain object should serialize to a map")
    void givenDomainFeatureShouldSerializeToMap() {

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
    @DisplayName("Given a Guarantee object should serialize to a map")
    void givenGuaranteeFeatureShouldSerializeToMap() {

        Guarantee guarantee = new Guarantee();
        guarantee.setDescription("Foo");
        guarantee.setDefaultValue("99.9%");
        guarantee.setValueType(ValueType.TEXT);
        guarantee.setExpression("0 < 1");
        guarantee.setServerExpression("0 < 1");
        guarantee.setDocURL("https://example.org");

        Map<String, Object> map = guarantee.serializeFeature();

        assertEquals("Foo", map.get("description"));
        assertEquals("99.9%", map.get("defaultValue"));
        assertEquals("TEXT", map.get("valueType"));
        assertEquals("GUARANTEE", map.get("type"));
        assertEquals("https://example.org", map.get("docUrl"));
        assertEquals("0 < 1", map.get("expression"));
        assertEquals("0 < 1", map.get("serverExpression"));

    }

    @Test
    @DisplayName("Given a Domain object should serialize to a map")
    void givenInformationFeatureShouldSerializeToMap() {

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
    @DisplayName("Given a Integration object should serialize to a map")
    void givenIntegrationFeatureShouldSerializeToMap() {

        Integration integration = new Integration();
        integration.setDescription("Foo");
        integration.setDefaultValue(100);
        integration.setValueType(ValueType.NUMERIC);
        integration.setIntegrationType(IntegrationType.WEB_SAAS);
        List<String> pricignUrls = new LinkedList<>();
        pricignUrls.add("https://foo.com");
        pricignUrls.add("https://bar.com");
        pricignUrls.add("https://baz.com");
        integration.setPricingUrls(pricignUrls);

        Map<String, Object> map = integration.serializeFeature();

        assertEquals("Foo", map.get("description"));
        assertEquals(100, map.get("defaultValue"));
        assertEquals("NUMERIC", map.get("valueType"));
        assertEquals("INTEGRATION", map.get("type"));
        assertEquals("WEB_SAAS", map.get("integrationType"));
        assertEquals(pricignUrls, map.get("pricingUrls"));

    }

    @Test
    @DisplayName("Given a Management object should serialize to a map")
    void givenManagementFeatureShouldSerializeToMap() {

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
    @DisplayName("Given a Payment object should serialize to a map")
    void givenPaymentFeatureShouldSerializeToMap() {

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
    @DisplayName("Given a Support object should serialize to a map")
    void givenSupportFeatureShouldSerializeToMap() {

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
