package io.github.isagroup.services.updaters;

import java.util.Map;

import io.github.isagroup.exceptions.UpdateException;

public class V20ToV21Updater extends VersionUpdater {

    public V20ToV21Updater(Updater updater) {
        super(Version.V2_0, updater);
    }

    @Override
    public void update(Map<String, Object> configFile) throws UpdateException {

        super.update(configFile);
        refactorPricingVersion(configFile);
    }

    private void refactorPricingVersion(Map<String,Object> configFile) {
        configFile.put("syntaxVersion", "2.1");
        
        try{
            configFile.put("version", configFile.get("createdAt").toString());
        }catch(Exception e){
            configFile.put("version", "latest");
        }
    }

}
