package io.github.isagroup.services.updaters;

import java.util.Map;

import io.github.isagroup.exceptions.UpdateException;

public interface Updater {
    void update(Map<String, Object> configFile) throws UpdateException;
}
