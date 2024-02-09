package org.example.monitoringservice.in.receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.dto.response.Response;
import org.example.monitoringservice.exception.DbException;
import org.example.monitoringservice.exception.NotAvailableReadingException;
import org.example.monitoringservice.exception.ReadingTypeAlreadyExistsException;
import org.example.monitoringservice.exception.TooRecentReadingException;
import org.example.monitoringservice.in.controller.ReadingController;
import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.util.UserContext;
import org.example.monitoringservice.util.ValidationUtils;

import java.util.Scanner;
import java.util.UUID;

/**
 * Interaction with the console for actions with readings service
 */
@Slf4j
@RequiredArgsConstructor
public class ReadingsCommandReceiver {
    private final ReadingController readingController;

    public void sendReading() {
        if (UserContext.isNotAuthenticated()) {
            return;
        }
        System.out.println("Введите тип показаний");
        String readingType = new Scanner(System.in).nextLine().toUpperCase().trim();

        System.out.println("Введите ваши показания");
        String readingValue = new Scanner(System.in).nextLine();
        if (isInvalidReadingValue(readingValue)) {
            return;
        }
        ReadingDto readingDto = inputDataToDto(readingValue, readingType, UserContext.getCurrentUser().getPersonalAccount());
        try {
            Response response = readingController.sendReading(readingDto);
            log.info(response.getMessage());
            System.out.println("Ваши показания сохранены");
        } catch (TooRecentReadingException | NotAvailableReadingException | DbException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void addReadingType() {
        if (UserContext.isNotAuthenticated() || UserContext.isNotAdmin()) {
            return;
        }
        System.out.println("Введите новый тип показаний");
        String newReadingType = new Scanner(System.in).nextLine().toUpperCase().trim();
        try {
            Response response = readingController.addNewReadingType(new ReadingType(newReadingType));
            log.info(response.getMessage());
            System.out.println("Новый тип показаний добавлен");
        } catch (ReadingTypeAlreadyExistsException | DbException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }


    public void readingsByMonth() {
        if (UserContext.isNotAuthenticated()) {
            return;
        }
        System.out.println("Введите целое число от 1 до 12");
        String monthNumber = new Scanner(System.in).nextLine().trim();
        if (isInvalidMonth(monthNumber)) {
            return;
        }
        try {
            Response response = readingController.getReadingsByMonth(monthNumber);
            log.info(response.getMessage());
            response.getData().forEach(System.out::println);
        } catch (DbException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

    public void actualReadings() {
        if (UserContext.isNotAuthenticated()) {
            return;
        }
        try {
            Response response = readingController.getActualReadings();
            log.info(response.getMessage());
            response.getData().forEach(System.out::println);
        } catch (DbException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

    public void historyOfReadings() {
        if (UserContext.isNotAuthenticated()) {
            return;
        }
        try {
            Response response = readingController.getHistoryOfReadings();
            log.info(response.getMessage());
            response.getData().forEach(System.out::println);
        } catch (DbException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

    private boolean isInvalidReadingValue(String readingValue) {
        if (!readingValue.matches(ValidationUtils.READING_VALUE_PATTERN)) {
            System.out.println("Некорректный ввод значения передаваемых показаний. " +
                    "Введите положительное целое число или дробное число с точкой");
            return true;
        }
        return false;
    }

    private boolean isInvalidMonth(String monthNumber) {
        if (!monthNumber.matches("\\d+")) {
            System.out.println("Некорректно введен месяц года");
            return true;
        }
        return false;
    }

    private ReadingDto inputDataToDto(String value, String type, UUID personalAccount) {
        return ReadingDto.builder()
                .value(Double.parseDouble(value))
                .type(type)
                .personalAccount(personalAccount)
                .build();
    }
}