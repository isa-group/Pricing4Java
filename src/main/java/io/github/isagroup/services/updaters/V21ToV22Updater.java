package io.github.isagroup.services.updaters;

import java.util.Map;

import io.github.isagroup.exceptions.UpdateException;
import io.github.isagroup.exceptions.VersionException;

public class V21ToV22Updater extends VersionUpdater {

    public V21ToV22Updater(Updater updater) {
        super(Version.V2_1, updater);
    }

    @Override
    public void update(Map<String, Object> configFile) throws UpdateException {

        super.update(configFile);

        configFile.put("syntaxVersion", "2.1");
    }

}
