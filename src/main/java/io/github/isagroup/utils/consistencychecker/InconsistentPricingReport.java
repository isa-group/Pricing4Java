package io.github.isagroup.utils.consistencychecker;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InconsistentPricingReport {

    private String info;
    private List<FeatUsageLimitInfo> inconsistent;

    public InconsistentPricingReport(String info, List<FeatUsageLimitInfo> inconsistent) {
        this.info = info;
        this.inconsistent = inconsistent;
    }

    @Override
    public String toString() {
        return "\n{\n  \"info\": \"" + this.info + "\",\n  \"inconsistent: " + inconsistent + "\n}\n";
    }
}
