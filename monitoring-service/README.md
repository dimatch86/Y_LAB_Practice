# Monitoring-Service App

---
Приложение для передачи показаний со счетчиков горячей, холодной воды и т.д.
Взаимодействие с приложением происходит через Http клиент, например Postman.

---
Стек используемых технологий:
- Java 17
- Spring 6
- JDBC
- PostgreSQL
- Liquibase
- Mapstruct
- AOP
- Lombok
- Logback
- Testcontainers
- Mockito
- Javadoc
- Swagger-UI


---
## Локальный запуск

### Требования

Проект использует синтаксис Java 17. Для локального запуска вам потребуется 
Docker Desktop для запуска контейнеров с базой данных Postgres и сервером Tomcat.

### Используя Docker Desktop
Откройте проект. Дождитесь индексации. Во вкладке maven выполните команду "package".
В корне проекта находится файл docker-compose.yml c контейнерами базы данных PostgreSQL и сервера Tomcat.
Откройте файл docker-compose в среде разработки и нажмите на значок запуска напротив строки "services",
или в командной строке перейдите в папку с проектом и запустите командой:
```
docker-compose up -d
```

После запуска приложения можно с ним работать посредством HTTP-запросов из Postman или другого веб-клиента.
В корне проекта в папке rest-examples находится файл с коллекцией запросов, которую можно импортировать в Postman.

---
Описание запросов:
- http://localhost:8080/monitoring-service/auth/register - зарегистрировать аккаунт, POST - запрос, содержит тело запроса;
- http://localhost:8080/monitoring-service/auth/login - выполнение входа в личный кабинет, POST - запрос, содержит тело запроса;
- http://localhost:8080/monitoring-service/auth/info - получение информации о пользователе;
- http://localhost:8080/monitoring-service/auth/logout - выход из личного кабинета;
- http://localhost:8080/monitoring-service/reading/send - передача показаний счетчика;
- http://localhost:8080/monitoring-service/reading/add - добавление нового типа показания;
- http://localhost:8080/monitoring-service/reading/actual - вывод всех актуальных показаний, GET-запрос;
- http://localhost:8080/monitoring-service/reading/month?monthNum=2 - вывод показаний за конкретный месяц GET-запрос, содержит обязательный параметр;
- http://localhost:8080/monitoring-service/reading/history - вывод истории подачи показаний;
- http://localhost:8080/monitoring-service/audit - аудит действий пользователей;
---
Приложение логирует действия пользователя с отображением логов в консоли сервера.

---

## Swagger

При запущенном приложении вы можете просмотреть описание API, а также
выполнять запросы используя интерфейс Swagger.

http://localhost:8080/monitoring-service/swagger-ui/index.html