package org.example.monitoringservice;

import org.example.auditstarter.aop.EnableAudit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Monitoring-Service Application
 * @author Dimatch86
 * @version 1.0
 */
@SpringBootApplication
@EnableAudit
public class MonitoringApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonitoringApplication.class, args);
    }
}