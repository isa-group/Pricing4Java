package io.github.isagroup.models.usagelimittypes;

import java.util.Map;

import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeDriven extends UsageLimit {

    @Override
    public Map<String, Object> serializeUsageLimit() {

        Map<String, Object> attributes = usageLimitAttributes();
        attributes.put("type", UsageLimitType.TIME_DRIVEN.toString());
        return attributes;
    }

    @Override
    public String toString() {
        return "TimeDriven[valueType: " + this.getValueType() + ", defaultValue: " + this.getDefaultValue()
                + ", value: " + this.getValue() + ", linkedFeature: " + this.getLinkedFeatures() + "]";
    }
}
