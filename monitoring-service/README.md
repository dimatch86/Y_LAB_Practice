# Monitoring-Service App

---
Приложение для передачи показаний со счетчиков горячей, холодной воды и т.д.
Взаимодействие с приложением происходит через Http клиент, например Postman.

---
Стек используемых технологий:
- Java 17
- JDBC
- PostgreSQL
- Liquibase
- Mapstruct
- AspectJ
- Lombok
- Logback
- Testcontainers
- Javadoc

---
Диаграмма главных классов

![pic1](classes.jpg)

---
## Локальный запуск

### Требования

Проект использует синтаксис Java 17. Для локального запуска вам потребуется
установленный JDK 17, сервер Tomcat, а также Docker Desktop для запуска контейнера с базой данных Postgres.

### Используя локальный сервер Tomcat
Скачайте сервер Tomcat для вашей операционной системы и распакуйте в произвольную папку.
В системных переменных вашей операционной системы добавьте переменную окружения JAVA_HOME и укажите путь к вашей JDK в качестве значения (например, C:\Program Files\Java\jdk1.8.0_291).
Откройте проект. Дождитесь индексации. Во вкладке maven выполните команду "package". После этого в папке "target" появится папка с проектом "monitoring-service".
Скопируйте папку в директорию "webapps" ранее установленного сервера Tomcat.
Следующий шаг - запуск контейнера базы данных 'monitoring_service_db'.
В корне проекта находится файл docker-compose.yml c контейнером базы данных PostgreSQL.
Откройте файл docker-compose в среде разработки и нажмите на значок запуска напротив имени контейнера (зеленый треугольник),
либо в командной строке перейдите в папку с проектом и запустите командой:
```
docker-compose run --service-ports monitoring_service_database
```
Затем в командной строке перейдите в директорию bin сервера Tomcat и выполните команду startup.bat, например:
```
E:\apache-tomcat-10.1.18-windows-x64\apache-tomcat-10.1.18\bin>startup.bat
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

## Настриваемые параметры

- порт для подключения к базе данных: устанавливается в файле [src/main/resources/config.properties](src/main/resources/config.properties), по умолчанию установлен порт 5434,
  при изменении необходимо задать такой же внешний порт для контейнера 'monitoring_service_database' в файле docker-compose.yml;