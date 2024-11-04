package io.github.isagroup.models;

import java.time.LocalDate;
import java.util.Date;
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

    private Version version;
    private String saasName;
    private LocalDate createdAt;
    private Date starts;
    private Date ends;
    private String currency;
    private Boolean hasAnnualPayment;
    private Map<String, Feature> features;
    private Map<String, UsageLimit> usageLimits;
    private Map<String, Plan> plans;
    private Map<String, AddOn> addOns;
    private Map<String, Object> variables;
    private List<String> tags;

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
