package org.example.monitoringservice.model.reading;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * This class represents a type of reading, such as electricity, water, gas, etc.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadingType {
    /**
     * The type of reading (e.g., electricity, water, gas).
     */
    private String type;
}
