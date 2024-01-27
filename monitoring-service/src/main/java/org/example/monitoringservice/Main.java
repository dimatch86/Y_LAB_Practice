package org.example.monitoringservice;

import org.example.monitoringservice.in.router.MainRouter;

/**
 * Monitoring-Service Application
 * @author Dimatch86
 * @version 1.0
 */
public class Main {
    static MainRouter mainRouter = new MainRouter();
    public static void main(String[] args) {
        mainRouter.run();
    }
}