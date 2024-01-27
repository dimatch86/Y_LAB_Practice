package org.example.monitoringservice.util;

import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class AvailableReadingsUtil {

    private final String[] defaultReadingsSet = {"ГОРЯЧАЯ ВОДА", "ХОЛОДНАЯ ВОДА", "ОТОПЛЕНИЕ"};

    private final Set<String> availableReadingsSet = new HashSet<>(List.of(defaultReadingsSet));

    public void addNewReadingType(String newType) {
        availableReadingsSet.add(newType);
    }
    public Set<String> getAvailableReadings() {
        return availableReadingsSet;
    }
}
