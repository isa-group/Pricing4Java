package io.github.isagroup.models.usagelimittypes;

import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResponseDriven extends UsageLimit {

    public ResponseDriven() {
        this.type = UsageLimitType.RESPONSE_DRIVEN;
    }

    @Override
    public String toString() {
        return "ResponseDriven[valueType: " + this.getValueType() + ", defaultValue: " + this.getDefaultValue()
                + ", value: " + this.getValue() + ", linkedFeature: " + this.getLinkedFeatures() + "]";
    }
}
