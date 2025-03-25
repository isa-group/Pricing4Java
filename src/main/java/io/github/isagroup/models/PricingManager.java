package io.github.isagroup.models;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import io.github.isagroup.services.updaters.Version;

/**
 * Object to model pricing configuration
 */
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

    public Version getSyntaxVersion() {
        return syntaxVersion;
    }

    public void setSyntaxVersion(Version syntaxVersion) {
        this.syntaxVersion = syntaxVersion;
    }

    public String getSaasName() {
        return saasName;
    }

    public void setSaasName(String saasName) {
        this.saasName = saasName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<String, Double> getBilling() {
        return billing;
    }

    public void setBilling(Map<String, Double> billing) {
        this.billing = billing;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Feature> features) {
        this.features = features;
    }

    public Map<String, UsageLimit> getUsageLimits() {
        return usageLimits;
    }

    public void setUsageLimits(Map<String, UsageLimit> usageLimits) {
        this.usageLimits = usageLimits;
    }

    public Map<String, Plan> getPlans() {
        return plans;
    }

    public void setPlans(Map<String, Plan> plans) {
        this.plans = plans;
    }

    public Map<String, AddOn> getAddOns() {
        return addOns;
    }

    public void setAddOns(Map<String, AddOn> addOns) {
        this.addOns = addOns;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((syntaxVersion == null) ? 0 : syntaxVersion.hashCode());
        result = prime * result + ((saasName == null) ? 0 : saasName.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
        result = prime * result + ((billing == null) ? 0 : billing.hashCode());
        result = prime * result + ((variables == null) ? 0 : variables.hashCode());
        result = prime * result + ((features == null) ? 0 : features.hashCode());
        result = prime * result + ((usageLimits == null) ? 0 : usageLimits.hashCode());
        result = prime * result + ((plans == null) ? 0 : plans.hashCode());
        result = prime * result + ((addOns == null) ? 0 : addOns.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PricingManager other = (PricingManager) obj;
        if (syntaxVersion != other.syntaxVersion)
            return false;
        if (saasName == null) {
            if (other.saasName != null)
                return false;
        } else if (!saasName.equals(other.saasName))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        } else if (!createdAt.equals(other.createdAt))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (tags == null) {
            if (other.tags != null)
                return false;
        } else if (!tags.equals(other.tags))
            return false;
        if (billing == null) {
            if (other.billing != null)
                return false;
        } else if (!billing.equals(other.billing))
            return false;
        if (variables == null) {
            if (other.variables != null)
                return false;
        } else if (!variables.equals(other.variables))
            return false;
        if (features == null) {
            if (other.features != null)
                return false;
        } else if (!features.equals(other.features))
            return false;
        if (usageLimits == null) {
            if (other.usageLimits != null)
                return false;
        } else if (!usageLimits.equals(other.usageLimits))
            return false;
        if (plans == null) {
            if (other.plans != null)
                return false;
        } else if (!plans.equals(other.plans))
            return false;
        if (addOns == null) {
            if (other.addOns != null)
                return false;
        } else if (!addOns.equals(other.addOns))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PricingManager [syntaxVersion=" + syntaxVersion + ", saasName=" + saasName + ", url=" + url
                + ", createdAt=" + createdAt + ", version=" + version + ", currency=" + currency + ", tags=" + tags
                + ", billing=" + billing + ", variables=" + variables + ", features=" + features + ", usageLimits="
                + usageLimits + ", plans=" + plans + ", addOns=" + addOns + "]";
    }

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
