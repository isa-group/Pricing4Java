package io.github.isagroup.models;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import io.github.isagroup.services.updaters.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Object to model pricing configuration
 */
@Getter
@Setter
@EqualsAndHashCode
public class PricingManager {

    private Version syntaxVersion;
    private String saasName;
    private String url;
    private LocalDate createdAt;
    private String version;
    private String currency;
    private List<String> tags;
    private Map<String, Double> billing;
    private Map<String, Object> variables;
    private Map<String, Feature> features;
    private Map<String, UsageLimit> usageLimits;
    private Map<String, Plan> plans;
    private Map<String, AddOn> addOns;

    /**
     * TODO: Check if this method should be here or where
     * Validate that all the features have tags that are defined in the pricing
     * configuration.
     */
    public void validateFeatureTags() {
        for (Feature feature : this.features.values()) {
            if (feature.getTag() != null) {
                if (!this.tags.contains(feature.getTag())) {
                    throw new IllegalArgumentException("Tag " + feature.getTag() + " not found in pricing configuration");
                }
            }
        }
    }

}
