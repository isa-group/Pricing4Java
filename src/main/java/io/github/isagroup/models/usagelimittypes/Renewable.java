package io.github.isagroup.models.usagelimittypes;

import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Renewable extends UsageLimit {

    public Renewable() {
        this.type = UsageLimitType.RENEWABLE;
    }

    @Override
    public String toString() {
        return "Renewable[valueType: " + this.getValueType() + ", defaultValue: " + this.getDefaultValue() + ", value: "
                + this.getValue() + ", linkedFeature: " + this.getLinkedFeatures() + "]";
    }
}
