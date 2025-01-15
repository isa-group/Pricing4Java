package io.github.isagroup.services.updaters;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.exceptions.UpdateException;
import io.github.isagroup.exceptions.VersionException;

public class YamlUpdater {

    private static final Map<Version, Updater> updaters = new LinkedHashMap<>();

    static {
        updaters.put(Version.V1_0, new V10ToV11Updater(null));
        updaters.put(Version.V1_1, new V11ToV20Updater(updaters.get(Version.V1_0)));
        updaters.put(Version.V2_0, new V20ToV21Updater(updaters.get(Version.V1_1)));
        updaters.put(Version.V2_1, new V21ToV22Updater(updaters.get(Version.V2_0)));
    }

    public static void update(Map<String, Object> configFile) throws UpdateException {

        if (configFile.get("syntaxVersion") == null && configFile.get("version") == null) {
            throw new VersionException("The syntax version field of the pricing must not be null or undefined. Please ensure that the version field is present and correctly formatted");
        }


        Object versionField = configFile.get("syntaxVersion");

        if (versionField == null) {
            versionField = configFile.get("version");
        }

        if (versionField instanceof String string && (string.isBlank() || string.isEmpty())){
            throw new PricingParsingException("The syntax version field of the pricing must not be empty. Please ensure that the syntax version field is present and correctly formatted");
        }

        if (versionField instanceof Double || versionField instanceof String) {
            Version version = Version.version(versionField);
            if (updaters.get(version) == null) {
                return;
            }

            updaters.get(Version.V2_1).update(configFile);
        }else{
            throw new PricingParsingException("The syntax version field of the pricing must be a string or a double. Please ensure that the version field is present and correctly formatted");
        }
    }
}
