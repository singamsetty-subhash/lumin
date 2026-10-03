# Lumin Consultancy website (Spring Boot)

Java 17+ and Maven required.

## Configure (environment variables)
| Variable | Meaning |
|---|---|
| ENQUIRY_TO | Client inbox that receives every enquiry |
| SMTP_USER | Email account used to send (e.g. Gmail address) |
| SMTP_PASS | That account's password / Gmail App Password |
| SMTP_HOST, SMTP_PORT | Optional, default smtp.gmail.com / 587 |

## Run
    export ENQUIRY_TO=client@example.com SMTP_USER=you@gmail.com SMTP_PASS=app-password
    mvn spring-boot:run
Open http://localhost:8080

## Build a deployable jar
    mvn clean package
    java -jar target/lumin-site-1.0.0.jar
# lumin
