package org.example.monitoringservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CaseConverter {

    public String camelCaseToSnakeCase(String camelCaseString) {
        StringBuilder result = new StringBuilder();
        char c = camelCaseString.charAt(0);
        result.append(Character.toLowerCase(c));
        for (int i = 1; i < camelCaseString.length(); i++) {
            char ch = camelCaseString.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            }
            else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
