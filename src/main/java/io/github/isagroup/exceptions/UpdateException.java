package io.github.isagroup.exceptions;

import java.util.Map;

public class UpdateException extends Exception {

    private final Map<String,Object> configFile;

    public UpdateException(String message, Map<String,Object> configFile) {
        super(message);
        this.configFile = configFile;
    }

    public Map<String, Object> getConfigFile() {
        return configFile;
    }
}
