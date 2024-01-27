package org.example.monitoringservice.model.reading;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MonthEnum {
    JANUARY("ЯНВАРЬ"),
    FEBRUARY("ФЕВРАЛЬ"),
    MARCH("МАРТ"),
    APRIL("АПРЕЛЬ"),
    MAY("МАЙ"),
    JUNE("ИЮНЬ"),
    JULY("ИЮЛЬ"),
    AUGUST("АВГУСТ"),
    SEPTEMBER("СЕНТЯБРЬ"),
    OCTOBER("ОКТЯБРЬ"),
    NOVEMBER("НОЯБРЬ"),
    DECEMBER("ДЕКАБРЬ"),
    EMPTY("EMPTY");

    private final String month;
}
