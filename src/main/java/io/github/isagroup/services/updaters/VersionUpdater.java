package io.github.isagroup.services.updaters;

import java.util.Map;

import io.github.isagroup.exceptions.UpdateException;

public abstract class VersionUpdater implements Updater {

    private final Version source;
    private final Updater versionUpdater;

    public VersionUpdater(Version target, Updater updater) {
        this.source = target;
        this.versionUpdater = updater;
    }

    @Override
    public void update(Map<String, Object> configFile) throws UpdateException {

        if (this.versionUpdater == null) {
            return;
        }

        Object version = configFile.getOrDefault("syntaxVersion", configFile.get("version"));

        if (isSpecOutdated(version)) {
            this.versionUpdater.update(configFile);
        }

    }

    public Version getSource() {
        return source;
    }

    public boolean isSpecOutdated(Object version) {
        return Version.version(version).lessThan(this.getSource());
    }
}
