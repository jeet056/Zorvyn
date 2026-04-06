# Finance Dashboard Backend

Spring Boot backend for a finance dashboard system with role-based access control, financial record management, and dashboard summary APIs.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- MySQL
- Lombok
- springdoc OpenAPI / Swagger UI

## Implemented Features

- User and role management
- Financial records CRUD
- Record filtering by type, category, date range, and user
- Pagination and sorting for record listing
- Dashboard summary APIs
- Role-based access control
- Validation and centralized error handling
- MySQL persistence
- Swagger API documentation
- Integration tests

## Roles

- `ADMIN`: manage users and financial records
- `ANALYST`: read records and access dashboard summaries
- `VIEWER`: access dashboard summaries only

## Default Users

- Admin: `admin@zorvyn.io` / `Admin@123`
- Analyst: `analyst@zorvyn.io` / `Analyst@123`
- Viewer: `viewer@zorvyn.io` / `Viewer@123`

## Database Setup

Create the database:

```sql
CREATE DATABASE finance_dashboard;
```

Default datasource configuration is in `src/main/resources/application.properties`.

## Running the Project

1. Make sure MySQL is running.
2. Create the `finance_dashboard` database.
3. Update datasource credentials in `src/main/resources/application.properties` if needed.
4. Start the application:

```bash
mvn spring-boot:run
```

Application base URL:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Postman collection:

```text
Finance-Dashboard-API.postman_collection.json
```

## Authentication

The API uses HTTP Basic authentication with Spring Security.

In Swagger UI:

1. Click `Authorize`
2. Enter a valid email and password
3. Call the secured endpoints based on the user role

## Main API Endpoints

### Users

- `POST /api/users`
- `GET /api/users`
- `GET /api/users/{id}`
- `PATCH /api/users/{id}/role`
- `PATCH /api/users/{id}/status`

### Financial Records

- `POST /api/records`
- `GET /api/records`
- `GET /api/records/{id}`
- `PUT /api/records/{id}`
- `DELETE /api/records/{id}`

### Dashboard

- `GET /api/dashboard/summary`

## Sample Filters

- `GET /api/records?type=INCOME`
- `GET /api/records?category=Cloud`
- `GET /api/records?startDate=2026-04-01&endDate=2026-04-30`
- `GET /api/records?page=0&size=5`
- `GET /api/records?sortBy=amount&direction=ASC`

## Dashboard Summary Response Includes

- total income
- total expenses
- net balance
- category-wise totals
- recent activity
- monthly trends

## Testing

Run tests with:

```bash
mvn test
```

## Notes

- Authentication uses HTTP Basic instead of JWT for simplicity.
- Records currently use hard delete.
- Rate limiting is not implemented.
- The backend is the main focus of this submission.
