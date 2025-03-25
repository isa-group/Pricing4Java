package io.github.isagroup.models.usagelimittypes;

import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.UsageLimitType;

public class ResponseDriven extends UsageLimit {

    public ResponseDriven() {
        this.type = UsageLimitType.RESPONSE_DRIVEN;
    }

}
