## MediConnect – Job‑Ready Healthcare Appointment System

This project is designed to look like a **small real-world product** instead of a toy CRUD app. It shows that you can build a secure, layered system end‑to‑end: backend, database, and frontend.

---

### 1. High‑level Overview

- **Domain**: Healthcare appointment booking (Doctor ↔ Patient).
- **Backend**: Spring Boot 3 (Java 17), layered architecture, JWT auth, MySQL persistence.
- **Frontend**: React + Vite + TypeScript, Axios, plain CSS.
- **Key features**:
  - User authentication with **JWT** (login + signup).
  - **Role‑based access control**: `ADMIN` vs `PATIENT`.
  - **Doctor management** (ADMIN creates; public listing).
  - **Appointment booking** with real business rules:
    - Cannot book in the **past**.
    - Doctor limited to **5 appointments per day**.
    - Each appointment has a **status**: `PENDING`, `CONFIRMED`, `CANCELLED`.
  - **Patient “My Appointments” page** that lists the logged‑in user’s bookings.
  - **Admin dashboard** with aggregate statistics and tools to confirm/cancel appointments.
  - **MySQL** database with entities auto‑mapped via JPA/Hibernate.

This is exactly the kind of full‑stack app a fresher can explain confidently in interviews.

---

### 2. Backend Architecture (Spring Boot)

- **Tech stack**
  - Spring Boot 3.x (Java 17)
  - Spring Web (REST APIs)
  - Spring Data JPA (repository pattern)
  - Spring Security + JWT
  - Bean Validation (`jakarta.validation`)
  - Lombok (reduces boilerplate)
  - MySQL (`mysql-connector-j`)

- **Package structure**
  - `com.mediconnect.controller`
    - `AuthController` – `/api/auth/login`, `/api/auth/register`.
    - `DoctorController` – `/api/doctors` (create + list).
    - `AppointmentController` – `/api/appointments` (book + list for current patient).
    - `AdminController` – `/api/admin` endpoints for stats and managing appointments.
  - `com.mediconnect.service`
    - `UserService` – user CRUD helpers + initial admin creation.
    - `UserDetailsServiceImpl` – integrates users with Spring Security.
    - `AppointmentService` – **business logic** for booking appointments and fetching patient view.
    - `AdminService` – aggregates dashboard stats and updates appointment status.
  - `com.mediconnect.model`
    - `User` – represents patients/admins, with `Role` enum.
    - `Doctor` – name + specialization.
    - `Appointment` – links `Doctor`, `User (patient)`, time and **status**.
    - `AppointmentStatus` – enum for `PENDING`, `CONFIRMED`, `CANCELLED`.
  - `com.mediconnect.repository`
    - `UserRepository`, `DoctorRepository`, `AppointmentRepository`.
  - `com.mediconnect.security`
    - `JwtService` – generate/validate JWT tokens.
    - `JwtAuthenticationFilter` – reads `Authorization: Bearer` header.
  - `com.mediconnect.config`
    - `SecurityConfig` – configures Spring Security, CORS, and JWT filter.
    - `DataInitializer` – seeds an admin user and sample doctors.
  - `com.mediconnect.dto`
    - Clean request/response objects for auth and appointments.
  - `com.mediconnect.exception`
    - `DoctorOverloadedException` – raised when doctor hits daily limit.
    - `GlobalExceptionHandler` – `@RestControllerAdvice` for JSON errors.

---

### 3. Core Backend Flows

#### 3.1 Authentication & Authorization

- **Login**
  - Endpoint: `POST /api/auth/login`.
  - Flow:
    1. Spring Security authenticates username/password via `AuthenticationManager` + `DaoAuthenticationProvider`.
    2. On success, `AuthController` asks `JwtService` to generate a JWT with `username` and `role`.
    3. The token is returned to the client and stored in `localStorage`.
  - Every subsequent request passes `Authorization: Bearer <token>`.  
    `JwtAuthenticationFilter` validates the token and sets the security context.

- **Signup**
  - Endpoint: `POST /api/auth/register`.
  - Flow:
    1. `AuthController` delegates to `UserService.registerPatient`.
    2. Password is encoded with `BCryptPasswordEncoder`.
    3. User is saved as `PATIENT` in MySQL.
    4. Unique username enforced at DB + global error handler returns a friendly JSON message.

- **Role‑based access**
  - `@PreAuthorize("hasRole('ADMIN')")` on doctor creation.
  - `@PreAuthorize("hasRole('PATIENT')")` for booking and listing own appointments.
  - `@PreAuthorize("hasRole('ADMIN')")` on admin stats and appointment‑management endpoints.

#### 3.2 Appointment Business Rules

Business logic lives in `AppointmentService`:

- Validate **future time**:
  - Reject if `appointmentTime < now` (prevents past bookings).
- Enforce **max 5 appointments per doctor per day**:
  - `AppointmentRepository` exposes a method to count appointments per doctor + day.
  - If count ≥ 5, throw `DoctorOverloadedException`.
- Link appointment to **current logged‑in patient** using `SecurityContextHolder`.
- Maintain an explicit **status** field:
  - New bookings start as `PENDING`.
  - Admins can later update to `CONFIRMED` or `CANCELLED` via the `AdminService`.
- Wrap booking logic in a **`@Transactional`** service method so checks and write happen in one transaction, helping keep booking rules consistent under concurrent requests.

This mimics realistic scheduling constraints you’d see in actual healthcare or booking systems.

---

### 4. Database Layer (MySQL + JPA)

- **MySQL** configured in `application.properties` with `ddl-auto=update`, so tables are created automatically from entities.
- Entities use:
  - `@Entity`, `@Table`, `@Id`, `@GeneratedValue`.
  - `@ManyToOne` relationships between `Appointment`, `Doctor`, and `User`.
- Using JPA and repositories demonstrates:
  - Understanding of **ORM**.
  - Writing **derived queries** and custom `@Query` for daily appointment counts.

---

### 5. Frontend Architecture (React + Vite + TypeScript)

- **Tech stack**
  - React + ReactDOM
  - TypeScript
  - Vite (fast dev server + bundler)
  - Axios for HTTP calls
  - Plain CSS with organized folders (no heavy UI framework)

- **Folder structure**
  - `src/pages/App.tsx` – top‑level layout, auth flows, and role‑based views.
  - `src/components/DoctorList.tsx` – doctor cards + booking UI (prevents past dates on the client).
  - `src/components/MyAppointments.tsx` – patient’s appointment history with statuses.
  - `src/components/AdminDashboard.tsx` – admin stats, doctor creation form, and appointment management table.
  - `src/services/api.ts` – Axios instance + API functions (auth, doctors, appointments, admin).
  - `src/styles/global.css` – base styling.
  - `src/components/*.css` – component‑specific styles for doctor list, admin dashboard, and appointments tables.

- **Frontend flows**
  - **Login**:
    - Calls `/api/auth/login`.
    - On success, stores `token` and `role` in `localStorage`.
    - Shows different content for `PATIENT` vs `ADMIN`.
  - **Signup**:
    - Simple toggle between login and signup in `App.tsx`.
    - Calls `/api/auth/register`, then switches back to login with a success message.
  - **Doctor list + booking (patient)**:
    - `DoctorList` loads `/api/doctors` and displays cards using CSS Flexbox.
    - User selects a `datetime-local` value; the input is constrained to **future times** and the component also validates this before sending the request.
    - Uses `/api/appointments` to create an appointment, shows success or error text from the backend.
  - **My Appointments page (patient)**:
    - `MyAppointments` calls `/api/appointments/me`.
    - Displays a table of the patient’s bookings with doctor name, specialization, time, and a colored status badge.
  - **Admin dashboard**:
    - Loads `/api/admin/stats` to show cards for total patients, doctors, total appointments, and today’s appointments.
    - Provides a simple UI form to create new doctors via `/api/doctors`.
    - Lists all appointments via `/api/admin/appointments`, with buttons that update status via `/api/admin/appointments/{id}/status`.

This gives clear evidence you can consume a REST API and handle JWT in a frontend.

---

### 6. Why This is Good for a Fresher Resume

- **End‑to‑end ownership**: You can talk about backend, database, and frontend together.
- **Security awareness**: JWT + Spring Security + role‑based access.
- **Realistic domain logic**: Not just CRUD; includes constraints, validation, statuses, and custom exceptions.
- **Modern stack**: Spring Boot 3, Java 17, React, TypeScript, Vite.
- **Database integration**: MySQL with JPA, rather than only in‑memory DB.
- **Clean architecture**: Controller → Service → Repository, DTOs, and global exception handling.
 - **Analytics and admin tooling**: Shows you can build simple dashboards and aggregate data for non‑technical users.

---

### 7. Sample Resume Bullet Points

- Built a **full‑stack healthcare appointment system (MediConnect)** using **Spring Boot 3, Java 17, React, TypeScript, and MySQL**, implementing end‑to‑end booking flows between doctors and patients.
- Implemented **JWT‑based authentication and role‑based authorization** with Spring Security, securing admin operations (doctor management) separately from patient features (viewing and booking appointments).
- Designed and enforced **business rules** such as “no past appointments”, “maximum of 5 appointments per doctor per day”, and explicit appointment **status transitions** (`PENDING`, `CONFIRMED`, `CANCELLED`) using a dedicated service layer and custom JPA queries.
- Integrated a **MySQL database** with Spring Data JPA, mapping entities and relationships (User, Doctor, Appointment) and leveraging `ddl-auto=update` for schema management.
- Developed a **React + TypeScript frontend** with reusable components, Axios API layer, and JWT token handling for login, signup, doctor listing, appointment booking, and a patient **My Appointments** view.
- Added **global error handling** and validation to provide clear JSON responses for domain errors (e.g., doctor overload, duplicate usernames, invalid input).
- Built an **admin dashboard** that surfaces key metrics (total patients, doctors, appointments, today’s appointments) and allows confirming/cancelling bookings and creating doctors directly from the UI.

---

### 8. How to Explain MediConnect in an Interview

**Q: Can you walk me through your MediConnect project?**  
**A:** MediConnect is a small but realistic healthcare appointment booking system I built end‑to‑end. On the backend, I used Spring Boot 3 with Java 17, Spring Web, Spring Data JPA, and Spring Security. The domain has three main entities: `User` (patients/admins), `Doctor`, and `Appointment`. Patients can sign up and log in, browse doctors, and book appointments; they also have a “My Appointments” page that shows their upcoming bookings and status. Admins get a dashboard where they can see key stats, create new doctors, and confirm or cancel appointments. I use JWT to secure APIs and distinguish admin vs patient roles, and MySQL as the persistent data store. On the frontend, I used React with TypeScript and Vite, with Axios for API calls and plain CSS for styling. The frontend handles login, signup, doctor listing, appointment booking, the patient view, and the admin dashboard, attaching the JWT token to each request.

**Q: How did you implement authentication and authorization?**  
**A:** I used Spring Security with a custom `UserDetailsService` and `DaoAuthenticationProvider`. When a user logs in, the backend validates the credentials and issues a JWT token using the `jjwt` library. The token stores the username and role. On each request, a filter (`OncePerRequestFilter`) extracts and validates the token, then sets the authentication in the security context. I use `@PreAuthorize` annotations to restrict certain endpoints to `ADMIN` or `PATIENT` roles. On the frontend, I store the token in `localStorage` and add it to the `Authorization` header via an Axios interceptor.

**Q: What kind of business rules did you implement?**  
**A:** The main rules are around scheduling and status. First, an appointment cannot be booked in the past; I validate the appointment time both in the service layer and via bean validation, and the frontend also prevents choosing past dates. Second, a doctor can have at most 5 appointments per day. I implemented a repository method that counts the number of appointments for a doctor on a given date. If that count is already 5 or more, I throw a custom `DoctorOverloadedException`, which is translated into a clean JSON error by a `@RestControllerAdvice` class. Finally, each appointment has a status (`PENDING`, `CONFIRMED`, `CANCELLED`) so that the admin can control which bookings are accepted or cancelled via the dashboard.

**Q: How does the “My Appointments” page work?**  
**A:** For patients, I expose a `GET /api/appointments/me` endpoint that uses the current security context to find the logged‑in user and then queries all appointments for that user, ordered by time. The service maps those entities to a DTO that includes doctor name, specialization, time, and status. On the frontend, there is a separate `MyAppointments` React component that calls this endpoint and renders the results in a table with colored status badges. The main `App` component lets the patient toggle between “Book Appointment” and “My Appointments” views using simple state.

**Q: What does the admin dashboard show and how did you build it?**  
**A:** The admin dashboard is backed by an `AdminService` and `AdminController`. The service uses repository methods and a custom query to calculate aggregated stats: total number of users, total doctors, total appointments, and appointments for the current day. These are exposed via `/api/admin/stats` and rendered as small cards in the React dashboard. There is also an `/api/admin/appointments` endpoint that returns all appointments joined with doctor and patient names, and a `PATCH /api/admin/appointments/{id}/status` endpoint that updates the appointment status. In the dashboard, I show the appointments in a table with Confirm and Cancel buttons that call the status update API and update local state.

**Q: Why did you choose JWT instead of HTTP sessions?**  
**A:** JWT fits well with stateless REST APIs and makes it easy to consume the backend from a separate React frontend. The server doesn’t need to store session state; it only needs the secret key to validate tokens. This keeps the architecture simple and ready for future scaling or even consuming the API from mobile apps. It is also a commonly used pattern in modern microservice and SPA architectures, so I wanted that experience.

**Q: How did you structure your code to keep it maintainable?**  
**A:** I followed a layered approach. Controllers are thin and only handle HTTP details. Services contain business logic and orchestrate calls to repositories. Repositories encapsulate persistence and use Spring Data JPA. I separated DTOs from entities to avoid exposing internal models directly. Security configuration and filters are in their own packages, and exception handling is centralized with a `@RestControllerAdvice`. On the frontend, I separated pages, components, styles, and services (API layer) for clarity.

**Q: What would you improve next if you had more time?**  
**A:** I’d like to add pagination and filtering for doctors and appointments, implement email notifications or reminders, and introduce more detailed availability per doctor (time slots). I’d also add unit and integration tests for the service layer and controller endpoints, and maybe containerize the app with Docker to make it easier to run in different environments.

---

This explanation file is meant to help you **revise the project quickly before interviews** and copy‑paste strong resume bullets when needed.

