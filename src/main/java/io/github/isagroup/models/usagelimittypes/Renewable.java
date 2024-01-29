package io.github.isagroup.models.usagelimittypes;

import java.util.Map;

import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Renewable extends UsageLimit {

    @Override
    public Map<String, Object> serializeUsageLimit() {

        Map<String, Object> attributes = usageLimitAttributes();
        attributes.put("type", UsageLimitType.RENEWABLE.toString());
        return attributes;
    }

    @Override
    public String toString() {
        return "Renewable[valueType: " + this.getValueType() + ", defaultValue: " + this.getDefaultValue() + ", value: "
                + this.getValue() + ", linkedFeature: " + this.getLinkedFeatures() + "]";
    }
}
