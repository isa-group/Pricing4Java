package io.github.isagroup.utils.consistencychecker;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class FeatUsageLimitInfo {

    private String usageLimitName;
    private Object usageLimitValue;
    private String featName;
    private Object featValue;

    public FeatUsageLimitInfo(String usageLimitName, Object usageLimitValue, String featName, Object featValue) {
        this.usageLimitName = usageLimitName;
        this.usageLimitValue = usageLimitValue;
        this.featName = featName;
        this.featValue = featValue;
    }

    public boolean isFeatureEnabledUsageLimitDisabled() {
        return this.featValue instanceof Boolean && this.usageLimitValue instanceof Number && (Boolean) this.featValue
                && ((Number) this.usageLimitValue).intValue() == 0;
    }

    public boolean isFeatureDisabledUsageLimitEnabled() {
        return this.featValue instanceof Boolean && this.usageLimitValue instanceof Number && !(Boolean) this.featValue
                && ((Number) this.usageLimitValue).intValue() > 0;
    }

    @Override
    public String toString() {
        return "{\n  \"ulName\": \"" + this.usageLimitName + "\",\n  \"ulValue\": " + usageLimitValue +
                ",\n  \"fName\": \"" + featName + "\",\n  \"fValue\": " + featValue + "\n}\n";
    }

}
