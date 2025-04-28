package io.github.isagroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.isagroup.exceptions.InvalidDefaultValueException;
import io.github.isagroup.exceptions.InvalidValueTypeException;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.featuretypes.Domain;
import io.github.isagroup.services.yaml.YamlUtils;
import io.github.isagroup.utils.PricingValidators;

public class PricingValidatorTests {

  @Test
  void givenNullFeatureNameShouldThrowIllegalArgumentExceptionException() {

    Feature feature = new Domain();
    feature.setName(null);

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));
    assertEquals("The feature null name must not be null", ex.getMessage());
  }

  @Test
  void givenFeatureNameWithEmptyStringShouldThrowIllegalArgumentExceptionException() {

    String name = "";
    Feature feature = new Domain();
    feature.setName(name);

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));
    // FIXME Weird formatting of error message, since plan name is ""
    assertEquals("The feature " + name + " name must not be empty", ex.getMessage());
  }

  @Test
  void givenFeatureNameWith2CharactersShouldThrowIllegalArgumentExceptionException() {

    String name = "ab";
    Feature feature = new Domain();
    feature.setName(name);

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));
    assertEquals("The feature " + name + " name must have at least 3 characters", ex.getMessage());
  }

  @Test
  void givenPlanWithThreeSpacesInNameShouldThrowIllegalArgumentExceptionException() {

    Feature feature = new Domain();
    feature.setName("   ");

    assertThrows(IllegalArgumentException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));
  }

  @Test
  void givenFeatureNameWith51CharactersShouldThrowIllegalArgumentExceptionException() {

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 51; i++) {
      sb.append("a");
    }
    String name = sb.toString();
    Feature feature = new Domain();
    feature.setName(name);

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));
    assertEquals("The feature " + name + " name must have at most 50 characters",
        ex.getMessage());
  }

  @Test
  void givenNullFeatureShouldThrowIllegalArgumentException() {

    Feature feature = null;
    assertThrows(IllegalArgumentException.class, () -> PricingValidators.validateAndFormatFeature(feature));
  }

  @Test
  void givenNullExpressionShouldThrowIllegalArgumentException() {

    String expression = null;
    PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("pricing/petclinic.yml");
    Feature feature = pricingManager.getFeatures().get("pets");
    feature.setExpression(expression);
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));

    assertEquals(
        "The feature " + feature.getName()
            + " expression must have at most 1000 characters and must be a string",
        ex.getMessage());

  }

  @Test
  void given10001CharactersExpressionShouldThrowIllegalArgumentException() {

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1001; i++) {
      sb.append("a");
    }
    String expression = sb.toString();
    PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("pricing/petclinic.yml");
    Feature feature = pricingManager.getFeatures().get("pets");
    feature.setExpression(expression);

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));
    assertEquals(
        "The feature " + feature.getName()
            + " expression must have at most 1000 characters and must be a string",
        ex.getMessage());

  }

  @Test
  void givenNullValueTypeShouldThrowInvalidValueTypeException() {

    PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("pricing/petclinic.yml");
    Feature feature = pricingManager.getFeatures().get("pets");
    feature.setValueType(null);

    // FIXME: REMOVE VALUE TYPE CHECK
    InvalidValueTypeException ex = assertThrows(InvalidValueTypeException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));
    assertEquals(
        "The feature " + feature.getName() + " valueType must not be null",
        ex.getMessage());

  }

  @Test
  void givenNullDefaultValueShouldThrowWhenValueTypeIsNumeric() {

    PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("pricing/petclinic.yml");
    Feature feature = pricingManager.getFeatures().get("pets");
    feature.setDefaultValue(null);

    InvalidDefaultValueException ex = assertThrows(InvalidDefaultValueException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));
    assertEquals(
        "The feature " + feature.getName()
            + " defaultValue must not be null",
        ex.getMessage());

  }

  @Test
  void givenStringDefaultValueShouldThrowWhenValueTypeIsBoolean() {

    PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("pricing/petclinic.yml");
    Feature feature = pricingManager.getFeatures().get("haveCalendar");
    String defaultValue = "";
    feature.setDefaultValue(defaultValue);

    InvalidDefaultValueException ex = assertThrows(InvalidDefaultValueException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));
    assertEquals(
        "The feature " + feature.getName()
            + " defaultValue must be a boolean if valueType is BOOLEAN",
        ex.getMessage());

  }

  @Test
  void givenBooleanDefaultValueShouldThrowWhenValueTypeIsText() {

    PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("pricing/petclinic.yml");
    Feature feature = pricingManager.getFeatures().get("supportPriority");
    Boolean defaultValue = true;
    feature.setDefaultValue(defaultValue);

    InvalidDefaultValueException ex = assertThrows(InvalidDefaultValueException.class,
        () -> PricingValidators.validateAndFormatFeature(feature));
    assertEquals(
        "The feature " + feature.getName()
            + " defaultValue must be a string if valueType is TEXT",
        ex.getMessage());

  }

  // Remove logic from validateValueTypeConsistency

  // @Test
  // void givenNumericExpressionShouldThrowIllegalArgumentExceptionWhenIsBoolean() {
  //   PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("pricing/petclinic.yml");
  //   Feature feature = pricingManager.getFeatures().get("haveCalendar");
  //   String expression = "a < b";
  //   feature.setExpression(expression);

  //   IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
  //       () -> PricingValidators.validateAndFormatFeature(feature));
  //   assertEquals("Expression of feature " + feature.getName()
  //       + " should only include the feature value/defaultValue and the operators '&&', '||' and '!', as it is BOOLEAN",
  //       ex.getMessage());
  // }

}
