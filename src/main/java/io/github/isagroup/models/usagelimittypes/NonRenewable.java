package io.github.isagroup.models.usagelimittypes;

import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;

public class NonRenewable extends UsageLimit {

    public NonRenewable() {
        this.type = UsageLimitType.NON_RENEWABLE;
    }

}
