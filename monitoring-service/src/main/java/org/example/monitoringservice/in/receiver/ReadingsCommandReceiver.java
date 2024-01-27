package org.example.monitoringservice.in.receiver;

import lombok.extern.slf4j.Slf4j;
import org.example.monitoringservice.exception.NotAvailableReadingException;
import org.example.monitoringservice.in.controller.ReadingController;
import org.example.monitoringservice.dto.ReadingDto;
import org.example.monitoringservice.exception.TooRecentReadingException;
import org.example.monitoringservice.repository.InMemoryReadingRepository;
import org.example.monitoringservice.response.Response;
import org.example.monitoringservice.service.ReadingService;
import org.example.monitoringservice.util.AvailableReadingsUtil;
import org.example.monitoringservice.util.UserContext;
import org.example.monitoringservice.util.ValidationUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

/**
 * Interaction with the console for actions with readings service
 */
@Slf4j
public class ReadingsCommandReceiver {
    private final ReadingController readingController =
            new ReadingController(new ReadingService(new InMemoryReadingRepository(new ArrayList<>())));

    public void sendReading() {
        if (UserContext.isNotAuthenticated()) {
            return;
        }
        System.out.println(MessageFormat.format("Введите тип показаний. В настоящее время доступны: {0}",
                AvailableReadingsUtil.getAvailableReadings()));
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
        } catch (TooRecentReadingException | NotAvailableReadingException e) {
            System.out.println(e.getLocalizedMessage());

        }
    }

    public void addReadingType() {
        if (UserContext.isNotAuthenticated() || UserContext.isNotAdmin()) {
            return;
        }
        System.out.println("Введите новый тип показаний");
        String newReadingType = new Scanner(System.in).nextLine().toUpperCase().trim();
        Response response = readingController.addNewReadingType(newReadingType);
        log.info(response.getMessage());
    }


    public void readingsByMonth() {
        if (UserContext.isNotAuthenticated()) {
            return;
        }
        System.out.println("Введите месяц");
        String month = new Scanner(System.in).nextLine().toUpperCase().trim();
        if (isInvalidMonth(month)) {
            return;
        }
        Response response = readingController.getReadingsByMonth(month);
        log.info(response.getMessage());
    }

    public void actualReadings() {
        if (UserContext.isNotAuthenticated()) {
            return;
        }
        Response response = readingController.getActualReadings();
        log.info(response.getMessage());
    }

    public void historyOfReadings() {
        if (UserContext.isNotAuthenticated()) {
            return;
        }
        Response response = readingController.getHistoryOfReadings();
        log.info(response.getMessage());
    }

    private boolean isInvalidReadingValue(String readingValue) {
        if (!readingValue.matches(ValidationUtils.READING_VALUE_PATTERN)) {
            System.out.println("Некорректный ввод значения передаваемых показаний. " +
                    "Введите положительное целое число или дробное число с точкой");
            return true;
        }
        return false;
    }

    private boolean isInvalidMonth(String month) {
        if (!month.matches(ValidationUtils.MONTH_PATTERN)) {
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
