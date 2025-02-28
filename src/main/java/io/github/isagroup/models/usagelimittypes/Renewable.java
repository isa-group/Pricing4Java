package io.github.isagroup.models.usagelimittypes;

import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;

public class Renewable extends UsageLimit {

    public Renewable() {
        this.type = UsageLimitType.RENEWABLE;
    }

}
