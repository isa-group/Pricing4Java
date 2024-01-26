package io.github.isagroup.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

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

public class FeatureSerializerTest {

    private Yaml yaml;

    @BeforeEach
    public void setUp() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    @Test
    public void given_AutomationFeature_should_SerializeToMap() {

        Automation automation = new Automation();
        automation.setDescription("Foo");
        automation.setDefaultValue("Bar");
        automation.setAutomationType(AutomationType.BOT);
        automation.setExpression("Baz");
        automation.setServerExpression("John");

        Map<String, Object> map = automation.serializeFeature();

        // Order of YAML KEYS depends on the order of DECLARATION of the attributes of
        // (Feature,Automation,...etc)
        String expected = "description: Foo\n"
                + "defaultValue: Bar\n"
                + "expression: Baz\n"
                + "serverExpression: John\n"
                + "type: AUTOMATION\n"
                + "automationType: BOT\n";
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    public void given_DomainFeature_should_SerializeToMap() {

        Domain domain = new Domain();
        domain.setDescription("Foo");
        domain.setDefaultValue("Bar");
        domain.setExpression("Baz");
        domain.setServerExpression("John");

        Map<String, Object> map = domain.serializeFeature();

        String expected = "description: Foo\n"
                + "defaultValue: Bar\n"
                + "expression: Baz\n"
                + "serverExpression: John\n"
                + "type: DOMAIN\n";
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    public void given_GuaranteeFeature_should_SerializeToMap() {

        Guarantee guarantee = new Guarantee();
        guarantee.setDescription("Foo");
        guarantee.setDefaultValue("Bar");
        guarantee.setExpression("Baz");
        guarantee.setServerExpression("John");
        guarantee.setDocURL("https://foobar.com");

        Map<String, Object> map = guarantee.serializeFeature();

        String expected = "description: Foo\n"
                + "defaultValue: Bar\n"
                + "expression: Baz\n"
                + "serverExpression: John\n"
                + "type: GUARANTEE\n"
                + "docUrl: https://foobar.com\n";
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    public void given_InformationFeature_should_SerializeToMap() {

        Information information = new Information();
        information.setDescription("Foo");
        information.setDefaultValue("Bar");
        information.setExpression("Baz");
        information.setServerExpression("John");

        Map<String, Object> map = information.serializeFeature();

        String expected = "description: Foo\n"
                + "defaultValue: Bar\n"
                + "expression: Baz\n"
                + "serverExpression: John\n"
                + "type: INFORMATION\n";
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    public void given_IntegrationFeature_should_SerializeToMap() {

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

        String expected = "description: Foo\n"
                + "defaultValue: Bar\n"
                + "expression: Baz\n"
                + "serverExpression: John\n"
                + "type: INTEGRATION\n"
                + "integrationType: API\n"
                + "pricingUrls:\n"
                + "- https://foo.com\n"
                + "- https://bar.com\n"
                + "- https://baz.com\n";
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    public void given_ManagementFeature_should_SerializeToMap() {

        Management management = new Management();
        management.setDescription("Foo");
        management.setDefaultValue("Bar");
        management.setExpression("Baz");
        management.setServerExpression("John");

        Map<String, Object> map = management.serializeFeature();

        String expected = "description: Foo\n"
                + "defaultValue: Bar\n"
                + "expression: Baz\n"
                + "serverExpression: John\n"
                + "type: MANAGEMENT\n";
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    public void given_PaymentFeature_should_SerializeToMap() {

        Payment payment = new Payment();
        payment.setDescription("Foo");
        List<String> paymentOptions = new LinkedList<>();
        paymentOptions.add("CARD");
        paymentOptions.add("GATEWAY");

        payment.setDefaultValue(paymentOptions);
        payment.setExpression("Baz");
        payment.setServerExpression("John");

        Map<String, Object> map = payment.serializeFeature();

        String expected = "description: Foo\n"
                + "defaultValue:\n"
                + "- CARD\n"
                + "- GATEWAY\n"
                + "expression: Baz\n"
                + "serverExpression: John\n"
                + "type: PAYMENT\n";
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

    @Test
    public void given_SupportFeature_should_SerializeToMap() {

        Support support = new Support();
        support.setDescription("Foo");
        support.setDefaultValue("Bar");
        support.setExpression("Baz");
        support.setServerExpression("John");

        Map<String, Object> map = support.serializeFeature();

        String expected = "description: Foo\n"
                + "defaultValue: Bar\n"
                + "expression: Baz\n"
                + "serverExpression: John\n"
                + "type: SUPPORT\n";
        String output = yaml.dump(map);

        assertEquals(expected, output);

    }

}
