package org.example.monitoringservice.util;

import lombok.experimental.UtilityClass;
import org.example.monitoringservice.model.user.RoleType;

import java.text.MessageFormat;

/**
 * Utility class for storage validation patterns
 */
@UtilityClass
public class ValidationUtils {

    public final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public final String USER_ROLE_PATTERN = MessageFormat.format("{0}|{1}",
            RoleType.USER, RoleType.ADMIN);
    public final String READING_VALUE_PATTERN = "^[0-9]*\\.?[0-9]+$";
    public final String MONTH_PATTERN =
            "ЯНВАРЬ|ФЕВРАЛЬ|МАРТ|АПРЕЛЬ|МАЙ|ИЮНЬ|ИЮЛЬ|АВГУСТ|СЕНТЯБРЬ|ОКТЯБРЬ|НОЯБРЬ|ДЕКАБРЬ";
}
