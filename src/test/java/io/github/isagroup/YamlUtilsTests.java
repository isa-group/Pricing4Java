package io.github.isagroup;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.isagroup.exceptions.FilepathException;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;

public class YamlUtilsTests {

    @Test
    void givenNullPathWhenReadingConfigShouldThrowIOException() {
        assertThrows(FilepathException.class, () -> YamlUtils.retrieveManagerFromYaml(null));
    }

    @Test
    void givenNullPathWhenWritingConfigShouldThrowIOException() {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("pricing/petclinic.yml");
        assertThrows(FilepathException.class, () -> YamlUtils.writeYaml(pricingManager, null));
    }

}