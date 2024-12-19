package io.github.isagroup.services.updaters;

import java.util.List;
import java.util.Map;

import io.github.isagroup.exceptions.UpdateException;
import io.github.isagroup.exceptions.VersionException;

public class V11ToV20Updater extends VersionUpdater {

    public V11ToV20Updater(Updater updater) {
        super(Version.V1_1, updater);
    }

    @Override
    public void update(Map<String, Object> configFile) throws UpdateException {

        try {
            if (Version.version(configFile.get("version")).compare(this.getSource()) < 0) {
                super.update(configFile);

            }
        } catch (VersionException e) {
            throw new UpdateException(e.getMessage(), configFile);
        }

        updateContainersWithOnlyOnePriceField(configFile);
        removeHasAnnualPaymentField(configFile);
        removeStartsField(configFile);
        removeEndsField(configFile);
        configFile.put("version", "2.0");
    }

    private boolean isValidPrice(Object price) {
        return price instanceof Double || price instanceof Long || price instanceof Integer || price instanceof String || price == null;
    }

    private void removeHasAnnualPaymentField(Map<String,Object> configFile) {
        if (configFile.get("hasAnnualPayment") != null) {
            System.out.println("[V20 UPDATER WARNING] hasAnnualPayment field is deprecated, it will be removed");
            configFile.remove("hasAnnualPayment");
        }
    }

    private void removeStartsField(Map<String,Object> configFile) {
        if (configFile.get("starts") != null) {
            System.out.println("[V20 UPDATER WARNING] starts field is deprecated, it will be removed");
            configFile.remove("starts");
        }
    }

    private void removeEndsField(Map<String,Object> configFile) {
        if (configFile.get("ends") != null) {
            System.out.println("[V20 UPDATER WARNING] ends field is deprecated, it will be removed");
            configFile.remove("ends");
        }
    }

    private void updateContainersWithOnlyOnePriceField(Map<String,Object> configFile) throws UpdateException {

        List<String> containers = List.of("plans", "addOns");

        for (String container: containers) {
            if (!(configFile.get(container) instanceof Map)) {
                return;
            }

            String containerName = container.equals("plans") ? "plan" : "addOn";
    
            for (Map.Entry<?,?> entry:  ((Map<?, ?>) configFile.get(container)).entrySet()) {
    
                if (entry.getValue() == null) {
                    throw new UpdateException(containerName + " is null", configFile);
                }
    
                if (!(entry.getValue() instanceof Map)) {
                    throw new UpdateException(containerName + " " + entry.getValue() + "is not a map", configFile);
                }
    
                Map<String,Object> containerAttributes = (Map<String, Object>) entry.getValue();
    
                if (containerAttributes.get("price") != null){
                    continue;
                }else{
                    if (containerAttributes.get("monthlyPrice") == null && containerAttributes.get("annualPrice") == null) {
                        throw new UpdateException("You have to specify either a monthlyPrice and/or an annualPrice, or a price; for the " + container + " " + entry.getKey(), configFile);
                    }
        
                    if (!isValidPrice(containerAttributes.get("monthlyPrice")) || !isValidPrice(containerAttributes.get("annualPrice"))) {
                        throw new UpdateException("Either the monthlyPrice or annualPrice of the " + containerName + " " + entry.getKey()
                            + " is neither a valid number nor String", configFile);
                    }
        
                    if (containerAttributes.get("monthlyPrice") != null) {
                        containerAttributes.put("price", containerAttributes.get("monthlyPrice"));
                    } else {
                        System.out.println("[V20 UPDATER WARNING] " + containerName + " " + entry.getKey() + " does not have a monthlyPrice but annualPrice instead, keep in mind that we are copying this value to price");
                        containerAttributes.put("price", containerAttributes.get("annualPrice"));
                    }   
                }

                containerAttributes.remove("monthlyPrice");
                containerAttributes.remove("annualPrice");
            }
        }
    }

}
