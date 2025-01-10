package io.github.isagroup.services.updaters;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.isagroup.exceptions.UpdateException;

public class V10ToV11Updater extends VersionUpdater {

    public V10ToV11Updater(Updater updater) {
        super(Version.V1_0, updater);
    }

    @Override
    public void update(Map<String, Object> configFile) throws UpdateException {

        configFile.put("version", "1.1");

        updateDayMonthYearToASingleField(configFile);
        removeSeverExpressionWhenBothExpressionAreEqual(configFile);
    }



    private void updateDayMonthYearToASingleField(Map<String,Object> configFile) throws UpdateException {
        List<String> errors = new ArrayList<>();

        if (configFile.get("day") == null) {
            errors.add("day is mandatory");
        } else if (!(configFile.get("day") instanceof Integer)) {
            errors.add("day must be an integer");
        }

        if (configFile.get("month") == null) {
            errors.add("month is mandatory");
        } else if (!(configFile.get("month") instanceof Integer)) {
            errors.add("month must be an integer");
        }

        if (configFile.get("year") == null) {
            errors.add("year is mandatory");
        } else if (!(configFile.get("year") instanceof Integer)) {
            errors.add("year must be an integer");
        }

        if (!errors.isEmpty()) {
            throw new UpdateException(String.join("\n", errors), configFile);
        }

        int day = (int) configFile.get("day");
        int month = (int) configFile.get("month");
        int year = (int) configFile.get("year");

        try {
            configFile.put("createdAt", LocalDate.of(year, month, day).toString());
            configFile.remove("day");
            configFile.remove("month");
            configFile.remove("year");
        } catch (DateTimeException e) {
            throw new UpdateException(e.getMessage(), configFile);
        }
    }

    private void removeSeverExpressionWhenBothExpressionAreEqual(Map<String,Object> configFile) throws UpdateException {
        if (configFile.get("features") instanceof Map) {
            removeServerExpressionFromFeatures((Map<String, Object>) configFile.get("features"));
        }
    }

    private void removeServerExpressionFromFeatures(Map<String, Object> features) throws UpdateException {
        for (Entry<String, Object> feature : features.entrySet()) {

            if (!(feature.getValue() instanceof Map)) {
                continue;
            }
            modifyFeature((Map<String,Object>) feature.getValue());

        }

    }

    private void modifyFeature(Map<String, Object> attributes) {
        if (attributes.get("expression") != null &&
            attributes.get("serverExpression") != null &&
            attributes.get("expression").equals(attributes.get("serverExpression"))) {
            attributes.remove("serverExpression");
        }
    }

}
