# MediConnect – Healthcare Appointment System

MediConnect is a **job‑ready full‑stack project** that demonstrates how to build a realistic appointment booking system for healthcare, with a **Spring Boot + MySQL backend** and a **React + JavaScript frontend**.
 
---

## Features

- **User authentication & signup**
  - JWT‑based login for patients and admins.
  - Signup endpoint and UI for registering new patients.
- **Role‑based access control**
  - `ADMIN` users can create doctors.
  - `PATIENT` users can view doctors and book appointments for themselves.
- **Doctor management**
  - Seeded doctors on startup.
  - Admin dashboard and API for creating more doctors.
- **Appointment booking**
  - Patients can book an appointment with a chosen doctor and time.
  - Business rules:
    - Appointment cannot be in the past.
    - A doctor cannot have more than **5 appointments per day**.
- **Appointment status & tracking**
  - Each appointment has a `PENDING`, `CONFIRMED`, or `CANCELLED` status.
  - Patients have a **“My Appointments”** page listing their bookings with status.
  - Admins can confirm or cancel appointments from the dashboard.
- **Admin dashboard & stats**
  - Summary cards for **Total Patients**, **Total Doctors**, **Total Appointments**, and **Appointments Today**.
  - Table of all appointments with inline status controls.
- **MySQL persistence**
  - Database schema generated automatically from JPA entities.
  - Relationships: `User` ↔ `Appointment` ↔ `Doctor`.
- **Frontend**
  - React + JavaScript + Vite.
  - Login/signup flow.
  - Doctor list with booking UI, patient “My Appointments” page, and admin dashboard, all with plain CSS styling.

---

## Tech Stack

- **Backend**
  - Java 17
  - Spring Boot 3
  - Spring Web
  - Spring Data JPA
  - Spring Security (JWT)
  - Bean Validation (Jakarta)
  - Lombok
  - MySQL (`mysql-connector-j`)

- **Frontend**
  - React
  - JavaScript (ES6+)
  - Vite
  - Axios
  - Plain CSS (no heavy UI framework)

---

## Prerequisites

- **Java** 17+
- **Maven**
- **Node.js** (LTS) + npm
- **MySQL** running locally

---

## Backend – Setup & Run

1. Navigate to the project root:

   ```bash
   cd MediConnect
   ```

2. Create your configuration file from the template:

   ```bash
   copy src\main\resources\application.properties.template src\main\resources\application.properties
   ```

3. Edit `src/main/resources/application.properties` and update:
   - MySQL password (replace `YOUR_MYSQL_PASSWORD_HERE`)
   - JWT secret key (replace `YOUR_JWT_SECRET_KEY_HERE` with a secure random string)

4. Run the backend:

   ```bash
   mvn spring-boot:run
   ```

5. On first successful start, the app will:
   - Create the `mediconnect` database and tables.
   - Insert a default admin user:
     - username: `admin`
     - password: `admin123`
   - Insert a couple of sample doctors.

---

## Frontend – Setup & Run

1. Navigate to the frontend folder:

   ```bash
   cd frontend
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Start the Vite dev server:

   ```bash
   npm run dev
   ```

4. Open the URL shown in the terminal (usually `http://localhost:5173`).

The frontend is configured to call the backend at `http://localhost:8081/api`.

---

## API Overview (Simplified)

Base URL: `http://localhost:8081/api`

### Auth

- `POST /auth/login`
  - Request: `{ "username": "admin", "password": "admin123" }`
  - Response: `{ "token": "...", "role": "ADMIN", "username": "admin" }`

- `POST /auth/register`
  - Request: `{ "username": "jane", "fullName": "Jane Doe", "password": "pass123" }`
  - Response: `"User registered successfully"`

### Doctors

- `GET /doctors`
  - Public – list all doctors.

- `POST /doctors`
  - Requires `ADMIN` JWT.
  - Body: `{ "name": "Dr. Smith", "specialization": "Cardiology" }`

### Appointments

- `POST /appointments`
  - Requires `PATIENT` JWT.
  - Body: `{ "doctorId": 1, "appointmentTime": "2026-02-15T10:00:00" }`
  - Enforces:
    - Future time.
    - Max 5 appointments per doctor per day.

- `GET /appointments/me`
  - Requires `PATIENT` JWT.
  - Returns current patient’s appointments with doctor details and status.

### Admin

- `GET /admin/stats`
  - Requires `ADMIN` JWT.
  - Returns summary counts for patients, doctors, total appointments, and today’s appointments.

- `GET /admin/appointments`
  - Requires `ADMIN` JWT.
  - Returns all appointments with doctor name, patient name, time, and status.

- `PATCH /admin/appointments/{id}/status`
  - Requires `ADMIN` JWT.
  - Body: `{ "status": "CONFIRMED" }` (or `PENDING` / `CANCELLED`).
  - Lets admins confirm or cancel bookings.

---

## Frontend Usage

1. Open the frontend in your browser.
2. Use the **Login** form:
   - For admin: `admin` / `admin123`.
   - Or sign up as a new patient and then log in.
3. As **PATIENT**:
   - Choose between **Book Appointment** and **My Appointments** in the toolbar.
   - In **Book Appointment**: see doctors, choose a future date/time, and click **Book**.
   - In **My Appointments**: see all your bookings in a table with status badges.
4. As **ADMIN**:
   - Use the **Admin Dashboard** to:
     - View stats cards (patients, doctors, appointments, today’s appointments).
     - Add new doctors from the UI form.
     - View all appointments and confirm/cancel them with one click.

---

 

---

## Architecture (Controller – Service – Repository)

- **Controller layer**
  - Handles HTTP endpoints and request/response mapping.
  - Examples: `AuthController`, `DoctorController`, `AppointmentController`, `AdminController`.
- **Service layer**
  - Contains business logic and transactional boundaries.
  - Examples: `UserService`, `AppointmentService`, `AdminService`.
- **Repository layer**
  - Talks to MySQL through Spring Data JPA repositories.
  - Examples: `UserRepository`, `DoctorRepository`, `AppointmentRepository`, `DoctorRepository`.
- **Cross‑cutting**
  - `SecurityConfig` + JWT filter for authentication and authorization.
  - `GlobalExceptionHandler` (`@RestControllerAdvice`) for consistent JSON error responses.

---

## Challenges Solved

- **Race conditions for bookings**
  - Appointment creation runs inside a **`@Transactional`** service method.
  - The service first checks how many appointments a doctor already has for the target day and then inserts the new one within the same transaction.
  - Combined with a clear business rule (max 5 per day) and central logic in `AppointmentService`, this reduces the chance of over‑booking when multiple patients try to book close together.

- **Global error handling & validation**
  - A single `GlobalExceptionHandler` class with `@RestControllerAdvice` handles domain and validation exceptions such as `DoctorOverloadedException`, `IllegalArgumentException`, and `MethodArgumentNotValidException`.
  - Every error response follows a consistent JSON shape: `timestamp`, `message`, and `details`.
  - DTOs such as `AppointmentRequestDto` use Bean Validation annotations like `@NotNull` and `@Future` so invalid data is rejected before it reaches the service layer, improving API reliability and client feedback.

