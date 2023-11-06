package io.github.isagroup.models.featuretypes;

import java.util.List;

import io.github.isagroup.models.Feature;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Integration extends Feature{
    private IntegrationType integrationType;
    private List<String> pricingUrls;

    public String toString(){
        return "Integration[valueType: " + valueType + ", defaultValue: " + defaultValue + ", integrationType: " + integrationType + ", pricingUrls: " + pricingUrls + "]";
    }
        
}
