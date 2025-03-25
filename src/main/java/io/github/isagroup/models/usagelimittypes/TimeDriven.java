package io.github.isagroup.models.usagelimittypes;

import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;

public class TimeDriven extends UsageLimit {

    public TimeDriven() {
        this.type = UsageLimitType.TIME_DRIVEN;
    }

}
