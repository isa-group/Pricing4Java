package io.github.isagroup.updaters;

import io.github.isagroup.exceptions.UpdateException;
import io.github.isagroup.exceptions.VersionException;
import io.github.isagroup.services.updaters.Version;
import io.github.isagroup.services.updaters.YamlUpdater;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class UpdatersTest {


    @Test
    void givenOutOfBoundsMajorShouldThrow() {

        try {
            Version.version("9999999999.0");
            fail();
        } catch (VersionException e) {
            assertEquals("major 9999999999 overflows an int", e.getMessage());
        }
    }

    @Test
    void givenOutOfBoundsMinorShouldThrow() {

        try {
            Version.version("1.9999999999");
            fail();
        } catch (VersionException e) {
            assertEquals("minor 9999999999 overflows an int", e.getMessage());
        }
    }


    @Test
    void givenInvalidVersionFormatShouldThrow() {
        try {
            Version.version("alpha");
        } catch (VersionException e) {
            assertEquals("Invalid version \"alpha\", use <major>.<minor> version format", e.getMessage());
        }
    }

    @Test
    void givenBothNullAnnualAndMonthlyPriceShouldThrow() {

        Yaml yaml = new Yaml();

        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/updating/v11-v20/monthly-annual-price-are-null.yml")) {
            Map<String,Object> configFile = yaml.load(fileInputStream);
            YamlUpdater.update(configFile);
            fail();
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (UpdateException e) {
            assertEquals("You have to specify, at least, either a monthlyPrice or an annualPrice for the plan BASIC", e.getMessage());
        }
    }

    @Test
    void givenV20PriceShouldHoldMonthlyPriceV11() {

        Yaml yaml = new Yaml();

        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/updating/v11-v20/price-holds-monthlyPrice.yml")) {
            Map<String,Object> configFile = yaml.load(fileInputStream);
            YamlUpdater.update(configFile);
            Map<String,Object> plans = (Map<String, Object>) configFile.get("plans");
            Double actualPrice = (Double) ((Map<String,Object>) plans.get("BASIC")).get("price");
            assertEquals(14.99, actualPrice);
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (UpdateException e) {
             fail(e.getMessage());
        }
    }

    @Test
    void givenV20PriceShouldHoldV11AnnualPriceWhenMonthlyPriceIsNull() {

        Yaml yaml = new Yaml();

        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/updating/v11-v20/price-holds-annualPrice.yml")) {
            Map<String,Object> configFile = yaml.load(fileInputStream);
            YamlUpdater.update(configFile);
            Map<String,Object> plans = (Map<String, Object>) configFile.get("plans");
            Double actualPrice = (Double) ((Map<String,Object>) plans.get("BASIC")).get("price");
            assertEquals(17.99, actualPrice);
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (UpdateException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void givenInvalidTypeInMonthlyPriceShouldThrow() {
        Yaml yaml = new Yaml();

        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/updating/v11-v20/monthlyPrice-is-boolean.yml")) {
            Map<String,Object> configFile = yaml.load(fileInputStream);
            YamlUpdater.update(configFile);
            fail();
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (UpdateException e) {
            assertEquals("Either the monthlyPrice or annualPrice of the plan BASIC is neither a valid number nor String", e.getMessage());
        }
    }

}
