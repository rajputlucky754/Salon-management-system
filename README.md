# 🌸 桜咲き Sakura Saki – Beauty Salon Appointment System

Sakura Saki is a Spring Boot and Thymeleaf based web application for managing salon and spa operations. It provides public service browsing, customer appointment booking, staff dashboards, admin management tools, service/package management, review moderation, and a loyalty voucher system.

The project is designed as a full-stack Java MVC application using Spring Security, Spring Data JPA, Hibernate, MySQL, Thymeleaf templates, Docker, and GitHub Actions/Render deployment support.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Live Demo](#live-demo)
- [Screenshots](#screenshots)
- [Main Features](#main-features)
- [User Roles](#user-roles)
- [Website Pages and Functions](#website-pages-and-functions)
- [Admin Functions](#admin-functions)
- [Customer Functions](#customer-functions)
- [Staff Functions](#staff-functions)
- [Public Website Functions](#public-website-functions)
- [Technical Stack](#technical-stack)
- [Architecture](#architecture)
- [Database and Seed Data](#database-and-seed-data)
- [Environment Variables](#environment-variables)
- [How to Run Locally](#how-to-run-locally)
- [Docker Deployment](#docker-deployment)
- [Render Deployment](#render-deployment)
- [API and Postman Testing](#api-and-postman-testing)
- [Project Structure](#project-structure)
- [Validation and Business Rules](#validation-and-business-rules)
- [Security Notes](#security-notes)
- [CI/CD](#cicd)
- [Development Workflow](#development-workflow)
- [Future Improvements](#future-improvements)
- [License](#license)

---

## Project Overview

Sakura Saki is built for a beauty salon/spa business that needs to manage customers, staff, services, packages, bookings, reviews, reports, loyalty points, and reward vouchers from one web application.

The application follows a server-side rendered MVC pattern using Spring Boot controllers, service classes, repositories, JPA entities, and Thymeleaf pages.

---

## Live Demo

🔗 [View Sakura Saki Live](https://sakura-saki.onrender.com)

---

## Screenshots

### Home Page

![Home Page](docs/screenshots/01-home-hero.png)

### Public Landing Page

![Public Landing Page](docs/screenshots/02-public-landing-page.png)

### Service Catalog

![Service Catalog](docs/screenshots/03-service-catalog.png)

### Appointment Booking

![Appointment Booking](docs/screenshots/04-appointment-booking.png)

### Customer Dashboard

![Customer Dashboard](docs/screenshots/05-customer-dashboard.png)

### Staff Dashboard

![Staff Dashboard](docs/screenshots/06-staff-dashboard.png)

### Admin Dashboard

![Admin Dashboard](docs/screenshots/07-admin-dashboard.png)

### Login Page

![Login Page](docs/screenshots/08-login-page.png)

### Registration Page

![Registration Page](docs/screenshots/09-register-page.png)

## Main Features

### Public Website

- Landing page with recent visible reviews
- Public service catalog
- Service category filtering
- Service search by name or description
- Service detail pages
- Package detail pages
- Public staff listing
- Authentication-aware navigation

### Authentication and Accounts

- Customer registration
- Login and logout
- BCrypt password hashing
- Role-based login redirects
- Customer, staff, and admin role separation
- Customer profile/settings management
- Password change
- Soft account deletion and anonymization

### Customer Booking

- Browse active services and packages
- Select service/package before booking
- Select date and time
- View available staff dynamically
- Book appointments
- View appointment history
- Cancel appointments with cancellation policy
- Submit, edit, and delete own reviews for completed appointments

### Staff Management

- Admin can create staff members
- Admin can assign staff type, specialization, working days, and shift times
- Admin can update, activate/deactivate, or delete staff
- Staff can view assigned appointments
- Staff can complete appointments
- Staff dashboard shows daily appointments, booked hours, and estimated tips

### Service and Package Management

- Admin can create salon services
- Admin can upload service images
- Images are stored as Base64 data URLs in the database
- Admin can update services
- Admin can activate/deactivate services
- Admin can delete services if safe to delete
- Admin can create packages from multiple services
- Admin can apply package discount percentage
- Admin can activate/deactivate or delete packages

### Appointment Management

- Admin can view all appointments
- Filter appointments by status
- Filter appointments by date
- Reschedule appointments
- Change appointment status
- Cancel appointments
- Staff availability checks prevent double booking
- Loyalty points are awarded when an appointment is completed

### Review System

- Customer can submit reviews for completed appointments
- Customer can edit own reviews
- Customer can delete own reviews
- Public service pages show visible reviews
- Admin can view all reviews
- Admin can toggle review visibility
- Admin can delete reviews

### Loyalty and Voucher System

- Customers earn loyalty points from completed appointments
- Membership tier is updated based on points
- Customers can redeem points for rewards
- Redeemed rewards become active vouchers
- Staff verification PIN is used to mark vouchers as used

### Admin Dashboard and Reports

- Admin dashboard shows operational summary
- Total customers
- Total services
- Total staff
- Today's appointments
- Completed appointment revenue
- Latest appointments
- Reports page uses dashboard summary data

---

## User Roles

| Role | Main Access |
|---|---|
| `ROLE_USER` | Customer dashboard, booking, loyalty, settings, reviews |
| `ROLE_STAFF` | Staff dashboard, calendar, performance, clients, inventory, appointment completion |
| `ROLE_ADMIN` | Full admin dashboard, users, customers, admins, services, packages, staff, appointments, reviews, reports |

---

## Website Pages and Functions

### Public Routes

| Route | Function |
|---|---|
| `/` | Landing page/home page |
| `/login` | Login page |
| `/register` | Customer registration page |
| `/services` | Public service and package catalog |
| `/services/{id}` | Public service detail page |
| `/services/package/{id}` | Package detail page |
| `/staff` | Public staff listing |
| `/reviews/service/{serviceId}` | Reviews for a service |

### Customer Routes

| Route | Function |
|---|---|
| `/booking` | Booking form |
| `/booking/available-staff` | AJAX endpoint for available staff |
| `/my-appointments` | Customer appointment list |
| `/my-appointments/{id}/cancel` | Cancel appointment |
| `/customer/dashboard` | Customer dashboard |
| `/customer/bookings` | Customer bookings and review actions |
| `/customer/loyalty` | Loyalty points and voucher page |
| `/customer/loyalty/redeem` | Redeem loyalty points |
| `/customer/loyalty/use-voucher` | Mark voucher as used through staff PIN |
| `/customer/rituals` | Customer rituals page |
| `/customer/settings` | Customer account settings |
| `/customer/settings/update` | Update profile details |
| `/customer/settings/change-password` | Change password |
| `/customer/settings/delete-account` | Soft delete customer account |

### Staff Routes

| Route | Function |
|---|---|
| `/staff/dashboard` | Staff dashboard |
| `/staff/calendar` | Staff calendar |
| `/staff/performance` | Staff performance page |
| `/staff/clients` | Staff clients page |
| `/staff/inventory` | Staff inventory page |
| `/staff/appointments/{id}/complete` | Mark appointment as completed |

### Admin Routes

| Route | Function |
|---|---|
| `/admin/dashboard` | Admin dashboard |
| `/admin/reports` | Reports page |
| `/admin/users` | User management |
| `/admin/users/{id}/make-admin` | Promote eligible user to admin role |
| `/admin/users/{id}/toggle-enabled` | Enable/disable user |
| `/admin/manage` | Admin account management |
| `/admin/manage/create` | Create admin |
| `/admin/manage/{id}/update` | Update admin |
| `/admin/manage/{id}/deactivate` | Deactivate admin |
| `/admin/manage/{id}/demote` | Demote admin |
| `/admin/customers` | Customer management |
| `/admin/customers/{id}/activate` | Activate customer |
| `/admin/customers/{id}/deactivate` | Deactivate customer |
| `/admin/customers/{id}/delete` | Soft delete/anonymize customer |
| `/admin/services` | Service management |
| `/admin/services/create` | Create service |
| `/admin/services/{id}/update` | Update service |
| `/admin/services/{id}/toggle-active` | Activate/deactivate service |
| `/admin/services/{id}/delete` | Delete service |
| `/admin/packages` | Package management |
| `/admin/packages/create` | Create package |
| `/admin/packages/{id}/update` | Update package |
| `/admin/packages/{id}/toggle-active` | Activate/deactivate package |
| `/admin/packages/{id}/delete` | Delete package |
| `/admin/staff` | Staff management |
| `/admin/staff/create` | Create staff |
| `/admin/staff/{id}/update` | Update staff |
| `/admin/staff/{id}/toggle-active` | Activate/deactivate staff |
| `/admin/staff/{id}/delete` | Delete staff |
| `/admin/appointments` | Appointment management |
| `/admin/appointments/{id}/status` | Change appointment status |
| `/admin/appointments/{id}/reschedule` | Reschedule appointment |
| `/admin/reviews` | Review moderation |
| `/admin/reviews/{id}/toggle-visibility` | Show/hide review |
| `/admin/reviews/{id}/delete` | Delete review |

### API Routes

| Route | Function |
|---|---|
| `/api/staff/available` | Returns available staff for a selected date/time/duration |

---

## Admin Functions

The admin role can:

- View dashboard summaries
- View latest appointments
- Manage users
- Manage customer accounts
- Create and manage admin accounts
- Manage staff accounts and schedules
- Manage services
- Manage service packages
- Manage appointments
- Reschedule appointments
- Cancel appointments
- Change appointment statuses
- Moderate reviews
- View reports

---

## Customer Functions

The customer role can:

- Register and log in
- Browse services/packages
- Book appointments
- View upcoming and past appointments
- Cancel appointments according to cancellation rules
- Submit reviews for completed services
- Edit own reviews
- Delete own reviews
- View loyalty points
- Redeem reward vouchers
- Update profile details
- Change password
- Delete account through soft deletion/anonymization

---

## Staff Functions

The staff role can:

- Log in to staff dashboard
- View assigned appointments
- View today's appointments
- View calendar page
- View performance page
- View clients page
- View inventory page
- Mark assigned appointments as completed

---

## Public Website Functions

Visitors can:

- View the landing page
- Browse services
- Search services
- Filter services by category
- View service details
- View packages
- View staff
- View public visible reviews
- Register as customers
- Log in

---

## Technical Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Web | Spring Web MVC |
| Security | Spring Security |
| Template Engine | Thymeleaf |
| Database Access | Spring Data JPA |
| ORM | Hibernate |
| Database | MySQL |
| Test/Dev Dependency | H2 |
| Build Tool | Maven |
| Packaging | Executable JAR |
| Deployment | Docker, Render |
| CI/CD | GitHub Actions |

---

## Architecture

The project follows a layered MVC architecture:

```text
Browser / Client
      ↓
Thymeleaf Views
      ↓
Spring MVC Controllers
      ↓
Service Layer
      ↓
Spring Data JPA Repositories
      ↓
MySQL Database
```

### Main Layers

| Layer | Purpose |
|---|---|
| `controller` | Handles routes, form submissions, page rendering, REST endpoints |
| `service` | Contains business logic, validation, booking rules, loyalty logic |
| `repository` | Spring Data JPA database access |
| `model` | JPA entities and domain objects |
| `dto` | Dashboard/report data transfer objects |
| `config` | Security, MVC config, seed data |
| `templates` | Thymeleaf UI pages |
| `static` | CSS, JS, images |

### OOP Structure

The user model uses inheritance:

```text
User
├── Customer
├── Admin
└── Staff
    ├── Stylist
    └── Therapist
```

This keeps shared login/account fields in `User`, while specialized fields are added by `Customer`, `Admin`, and `Staff`.

---

## Database and Seed Data

The app uses MySQL by default.

The application can seed sample data when the database is empty. The seed data includes:

- Admin users
- Customer users
- Staff users
- Salon services
- Service packages
- Scheduled appointments
- Completed appointments
- Cancelled appointments
- Reviews

### Local Demo Accounts

These accounts are intended for local development/demo use only. Change or remove them before production use.

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `Admin@123` |
| Manager/Admin | `manager` | `Manager@123` |
| Customer | `sakura` | `User@123` |
| Customer | `yuki` | `User@123` |
| Customer | `hana` | `User@123` |
| Customer | `mei` | `User@123` |
| Staff | `mika.honda` | `Staff@123` |
| Staff | `koji.yamamoto` | `Staff@123` |
| Staff | `yui.nakamura` | `Staff@123` |
| Staff | `aiko.mori` | `Staff@123` |

### Seed Reset Behavior

- H2 databases are reset and reseeded automatically.
- External databases are not reset unless `app.seed.reset=true` is provided.
- If data already exists, sample creation is skipped.

---

## Environment Variables

Use environment variables for all database credentials.

| Variable | Purpose |
|---|---|
| `SPRING_DATASOURCE_URL` | MySQL JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `SPRING_PROFILES_ACTIVE` | Spring profile, for example `prod` or `local` |
| `RENDER_DEPLOY_HOOK_URL` | Optional Render deploy hook secret for GitHub Actions |
| `app.seed.reset` | Set to `true` only when external DB should be cleared and reseeded |

Example local MySQL environment values:

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/defaultdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="your_mysql_password"
```

---

## How to Run Locally

### 1. Clone the Repository

```bash
git clone https://github.com/CJChANu/Sakura_Saki.git
cd Sakura_Saki
```

### 2. Check Java Version

```bash
java -version
```

Required:

```text
Java 21 or higher
```

### 3. Configure MySQL

Create the local database:

```sql
CREATE DATABASE IF NOT EXISTS defaultdb;
```

Run the application with database environment variables:

```bash
SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/defaultdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
SPRING_DATASOURCE_USERNAME="root" \
SPRING_DATASOURCE_PASSWORD="your_mysql_password" \
./mvnw spring-boot:run
```

### 4. Open the Website

```text
http://localhost:8080
```

### 5. Build the Project

```bash
./mvnw clean package -DskipTests
```

### 6. Run Tests

```bash
./mvnw test
```

---

## Local Configuration Option

Create an ignored local config file:

```text
src/main/resources/application-local.yml
```

Example:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/defaultdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: your_mysql_password
```

Run with:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Do not commit local database credentials.

---

## Docker Deployment

### Build Image

```bash
docker build -t sakura-saki .
```

### Run Container

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE="prod" \
  -e SPRING_DATASOURCE_URL="your_db_url" \
  -e SPRING_DATASOURCE_USERNAME="your_db_user" \
  -e SPRING_DATASOURCE_PASSWORD="your_db_password" \
  sakura-saki
```

---

## Render Deployment

The repository includes `render.yaml`.

Database values should be configured as Render environment variables, not committed into the repository:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_PROFILES_ACTIVE`

For GitHub Actions deployment trigger, add this GitHub secret if needed:

```text
RENDER_DEPLOY_HOOK_URL
```

---

## API and Postman Testing

The Postman collection is available at:

```text
docs/postman/Sakura_Saki_Postman_Collection.json
```

Import it into Postman to test authentication, admin service management, staff management, appointment flows, booking, reviews, and customer account actions.

---

## Project Structure

```text
Sakura_Saki/
├── .github/
│   └── workflows/
│       └── deploy.yml
├── docs/
│   └── postman/
│       └── Sakura_Saki_Postman_Collection.json
├── src/
│   ├── main/
│   │   ├── java/com/cjcc/yakalabs/sakurasaki/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
│   │       ├── templates/
│   │       │   ├── admin/
│   │       │   ├── auth/
│   │       │   ├── booking/
│   │       │   ├── customer/
│   │       │   ├── layout/
│   │       │   ├── public/
│   │       │   ├── review/
│   │       │   └── staff/
│   │       └── application.yml
│   └── test/
├── Dockerfile
├── LICENSE
├── README.md
├── mvnw
├── pom.xml
└── render.yaml
```

---

## Validation and Business Rules

### Registration

- Username must be 3–30 characters.
- Username can contain letters, numbers, and underscores.
- Email must be valid and unique.
- Password must include uppercase, lowercase, digit, and special character.
- Phone number must be 10 digits.
- First name and last name are required.

### Booking

- Past dates are not allowed.
- Past times for today are not allowed.
- Appointments must stay within shop hours: `09:00` to `20:00`.
- Staff must work on the selected day.
- Appointment must fit within staff shift time.
- Double-booking is prevented through overlap checks.

### Staff

- Staff shift cannot end after `20:00`.
- Staff shift must be at least 5 hours.
- Staff can be activated/deactivated.
- Staff can be categorized by type such as Stylist or Therapist.

### Loyalty

- Customers earn loyalty points from completed appointments.
- Points are based on completed service price.
- Membership tiers:
   - Bronze
   - Silver
   - Gold
   - Platinum
- Customers can redeem points for vouchers.
- Vouchers can be marked as used through staff verification.

### Cancellation

- Appointments can only be cancelled up to 2 days before the appointment date.

---

## Security Notes

- Passwords are encoded using BCrypt.
- Access is protected using Spring Security.
- Role-based authorization separates admin, staff, and customer areas.
- Database credentials must be provided through environment variables.
- Do not commit `.env`, `application-local.yml`, local cookies, runtime uploads, or generated files.
- Demo seed accounts should be changed or removed before production deployment.
- Rotate database credentials if they were ever committed in Git history.

---

## CI/CD

GitHub Actions workflow:

- Runs on pushes to `main`
- Runs on pull requests to `main`
- Sets up JDK 21
- Builds the project with Maven
- Skips tests during package build
- Optionally triggers Render deploy using `RENDER_DEPLOY_HOOK_URL`

Build command used in CI:

```bash
mvn -B package --file pom.xml -DskipTests
```

---

## Development Workflow

Recommended branch flow:

```text
feature branch → develop → main
```

Recommended commands:

```bash
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name
```

After changes:

```bash
./mvnw clean package -DskipTests
git add -A
git commit -m "type: short description"
git push -u origin feature/your-feature-name
```

Then create a Pull Request into `develop`.

After testing `develop`, create a Pull Request from `develop` into `main`.

---

## Future Improvements

Potential improvements:

- Add stronger review endpoint authorization.
- Replace hard-coded voucher staff PIN with database or environment-based verification.
- Add full automated tests and enable tests in CI.
- Add email service instead of simulated email logs.
- Add better role-specific profile management.
- Add API documentation with Swagger/OpenAPI.
- Add production-ready database migration tool such as Flyway or Liquibase.

---

## License

This project is licensed under a Custom Proprietary License. See the `LICENSE` file for details.
