# Lumin Consultancy Web Portal & Laboratory Management Platform

Production-ready Spring Boot 3.3.5 and Vanilla Web application providing marketing, accreditation guidance, consultation lead capture, automated SMTP notifications, and an administrative lead triage dashboard for **Lumin Consultancy**.

---

## Technical Stack & Architecture
- **Language**: Java 17+
- **Backend Framework**: Spring Boot 3.3.5 (Spring MVC, Spring Data JPA, Spring Mail)
- **Database**: MySQL 8.0+ via Hibernate ORM
- **Frontend**: HTML5, Vanilla CSS3 (Custom Properties & Responsive Design), ES6+ JavaScript, Canvas particles (`particles.js`)
- **Documentation**: [Software Requirements Specification (SRS.md)](file:///d:/subbu/Final%20Projects/lumin-site/SRS.md)

---

## Environment Variables Configuration

| Variable | Description | Default / Example |
|---|---|---|
| `PORT` | Embedded web server port | `8080` |
| `ENQUIRY_TO` | Target executive inbox receiving lead alerts | `ceo.lumin@gmail.com` |
| `SMTP_USER` | Originating email address for SMTP dispatch | *(Required for live email)* |
| `SMTP_PASS` | Gmail App Password or SMTP relay password | *(Required for live email)* |
| `SMTP_HOST` | Outbound SMTP relay host | `smtp.gmail.com` |
| `SMTP_PORT` | Outbound SMTP port | `587` |
| `MYSQL_URL` | MySQL JDBC database connection string | `jdbc:mysql://localhost:3306/lumindb` |
| `MYSQLUSER` | MySQL database username | `root` |
| `MYSQLPASSWORD` | MySQL database password | `root` |

---

## Quick Start (Development)

### Linux / macOS:
```bash
export ENQUIRY_TO=ceo.lumin@gmail.com SMTP_USER=you@gmail.com SMTP_PASS=app-password
mvn spring-boot:run
```

### Windows (PowerShell):
```powershell
$env:ENQUIRY_TO="ceo.lumin@gmail.com"
$env:SMTP_USER="you@gmail.com"
$env:SMTP_PASS="app-password"
mvn spring-boot:run
```

Access the application in your browser:
- Public Portal: `http://localhost:8080/`
- Visual Gallery: `http://localhost:8080/gallery.html`
- Administrative Dashboard: `http://localhost:8080/admin.html` *(Credentials: `admin` / `admin123`)*

---

## Production Packaging
Build an executable fat JAR:
```bash
mvn clean package
java -jar target/lumin-site-1.0.0.jar
```

