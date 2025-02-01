package io.github.isagroup.utils.consistencychecker;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;

public class InconsistentciesCalculator {

    private PricingManager pricing;

    public InconsistentciesCalculator(PricingManager pricing) {
        this.pricing = pricing;
    }

    public Optional<InconsistentPricingReport> getInconsistentItems() {
        if (this.pricing.getUsageLimits() == null || filterInconsistentValues().isEmpty()) {
            return Optional.empty();
        }

        String info = this.pricing.getSaasName() + " - " + this.pricing.getCreatedAt().getYear();
        return Optional.of(new InconsistentPricingReport(info, filterInconsistentValues()));
    }

    /*
     * Get a list of inconsistent features with usage limits.
     * An enabled feature and disabled usage limit is inconsistent, i.e., feature =
     * true, usageLimit=0
     * A disabled feature and enabled usage limit is inconsistent, i.e, feature =
     * false, usageLimit > 0
     */
    private List<FeatUsageLimitInfo> filterInconsistentValues() {
        return this.pricing.getFeatures().values().stream()
                .flatMap(feature -> getFeaturesAndUsageLimitsRecords(feature).stream())
                .filter(item -> item.isFeatureDisabledUsageLimitEnabled() || item.isFeatureEnabledUsageLimitDisabled())
                .collect(Collectors.toList());

    }

    private List<FeatUsageLimitInfo> getFeaturesAndUsageLimitsRecords(Feature feature) {
        return this.pricing.getUsageLimits().values().stream()
                .filter(usageLimit -> usageLimit.getLinkedFeatures() != null
                        && usageLimit.getLinkedFeatures().contains(feature.getName()))
                .map(usageLimit -> new FeatUsageLimitInfo(usageLimit.getName(),
                        usageLimit.getDefaultValue(), feature.getName(), feature.getDefaultValue()))
                .collect(Collectors.toList());
    }

    public static boolean isPlanFeatureEnabled(Plan plan, String featureName) {
        return plan.getFeatures().containsKey(featureName)
                && plan.getFeatures().get(featureName).getValue() instanceof Boolean
                && (Boolean) plan.getFeatures().get(featureName).getValue();
    }

    public static boolean isUsageLimitUndefined(Plan plan, String usageLimitName) {
        return plan.getUsageLimits() == null || !plan.getUsageLimits().containsKey(usageLimitName)
                || plan.getUsageLimits().get(usageLimitName).getValue() == null;
    }

    public void transformPricing(PricingManager pricing) {

        if (pricing.getPlans() == null) {
            return;
        }

        InconsistentciesCalculator inconsistentciesCalc = new InconsistentciesCalculator(pricing);

        if (inconsistentciesCalc.getInconsistentItems().isEmpty()) {
            return;
        }

        for (FeatUsageLimitInfo item : inconsistentciesCalc.getInconsistentItems().get().getInconsistent()) {
            for (Plan plan : pricing.getPlans().values()) {
                if (isPlanFeatureEnabled(plan, item.getFeatName()) &&
                        isUsageLimitUndefined(plan, item.getUsageLimitName())) {
                    plan.getUsageLimits().get(item.getUsageLimitName()).setValue(item.getUsageLimitValue());
                }
                if (item.isFeatureDisabledUsageLimitEnabled()) {
                    pricing.getUsageLimits().get(item.getUsageLimitName()).setDefaultValue(0);
                }

                if (!isPlanFeatureEnabled(plan, item.getFeatName())
                        && !isUsageLimitUndefined(plan, item.getUsageLimitName())) {
                    plan.getFeatures().get(item.getFeatName()).setValue(true);
                }

                if (item.isFeatureEnabledUsageLimitDisabled()) {
                    pricing.getFeatures().get(item.getFeatName()).setDefaultValue(false);
                }
            }
        }

    }

}
