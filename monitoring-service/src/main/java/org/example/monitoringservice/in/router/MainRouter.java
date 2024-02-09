package org.example.monitoringservice.in.router;

import org.example.monitoringservice.factory.AuthComponentFactoryImpl;
import org.example.monitoringservice.factory.ReadingComponentFactoryImpl;
import org.example.monitoringservice.in.controller.AuthController;
import org.example.monitoringservice.in.controller.ReadingController;
import org.example.monitoringservice.in.receiver.AuthenticationCommandReceiver;
import org.example.monitoringservice.in.receiver.ReadingsCommandReceiver;

import java.util.Scanner;

/**
 * Input commands router class
 */
public class MainRouter {
    AuthController authController = new AuthComponentFactoryImpl().createAuthController();
    ReadingController readingController = new ReadingComponentFactoryImpl().createReadingController();
    AuthenticationCommandReceiver authenticationCommandReceiver = new AuthenticationCommandReceiver(authController);
    ReadingsCommandReceiver readingsCommandReceiver = new ReadingsCommandReceiver(readingController);

    public void run() {
        printGreeting();
        while (true) {
            String input = new Scanner(System.in).nextLine().toUpperCase();
            checkCommand(input);
        }
    }

    private void checkCommand(String input) {
        switch (input) {
            case "REGISTER" -> authenticationCommandReceiver.register();
            case "LOGIN" -> authenticationCommandReceiver.login();
            case "AUTHORITY" -> authenticationCommandReceiver.authority();
            case "LOGOUT" -> authenticationCommandReceiver.logout();
            case "SEND" -> readingsCommandReceiver.sendReading();
            case "ADD" -> readingsCommandReceiver.addReadingType();
            case "ACTUAL" -> readingsCommandReceiver.actualReadings();
            case "HISTORY" -> readingsCommandReceiver.historyOfReadings();
            case "MONTH" -> readingsCommandReceiver.readingsByMonth();
            case "HELP" -> printHelp();

            default -> System.out.println("UNKNOWN COMMAND, enter \"HELP\"");
        }
    }
    private void printHelp() {

        String helpMessage = """
                Список команд:
                REGISTER - зарегистрировать аккаунт
                LOGIN - выполнить вход в личный кабинет
                AUTHORITY - получить информацию о полномочиях на сайте
                LOGOUT - выйти из личного кабинета
                SEND - отправить показания счетчика (только для авторизованных пользователей)
                ACTUAL - получить актуальные показания всех счетчиков (только для авторизованных пользователей)
                MONTH - получить показания за указанный месяц (только для авторизованных пользователей)
                HISTORY - получть историю подачи показаний (только для авторизованных пользователей)
                ADD - добавить тип показания (только для администраторов)
                """;
        System.out.println(helpMessage);
    }
    private void printGreeting() {
        String greetingMessage = """
                Здравствуйте. Вы вошли в программу. Зарегистрируйтесь или войдите в личный кабинет. 
                Чтобы получить список возможных команд введите HELP
                """;
        System.out.println(greetingMessage);
    }
}
