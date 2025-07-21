# Hitachi Test API

This is a Spring Boot RESTful API for user authentication and authorization, featuring user registration, login, and a dynamic superadmin role based on a secret key.

## Features

*   **User Registration**: Create new user accounts.
*   **User Login**: Authenticate users and issue JSON Web Tokens (JWT).
*   **JWT-based Authentication**: Secure API endpoints using JWTs.
*   **User Profile Endpoint**: Retrieve authenticated user's details (`/api/v1/auth/me`).
*   **Dynamic Superadmin Role**: Elevate user privileges to `ROLE_SUPER_ADMIN` for a session by providing a secret key during login.
*   **Custom Authentication Error Handling**: Provides a custom JSON response for unauthenticated access.
*   **Admin Seeder**: Automatically creates a default admin user and roles (`ROLE_USER`, `ROLE_ADMIN`, `ROLE_SUPER_ADMIN`) on application startup (for development/testing).
*   **Swagger/OpenAPI Documentation**: Automatically generated API documentation for easy testing and understanding.

## Technologies Used

*   **Spring Boot**: Framework for building the API.
*   **Spring Security**: For authentication and authorization.
*   **JWT (JSON Web Tokens)**: For stateless authentication.
*   **Spring Data JPA**: For database interaction.
*   **Lombok**: To reduce boilerplate code.
*   **Maven**: Build automation tool.
*   **H2 Database (in-memory)**: Default for development (can be configured for other databases).
*   **Swagger UI / OpenAPI 3**: For API documentation.

## Getting Started

### Prerequisites

*   Java 17 or higher
*   Maven 3.6.0 or higher

### Cloning the Repository

```bash
git clone <repository_url>
cd hitachi-test
```

### Configuration

#### `application.properties`

The `src/main/resources/application.properties` file contains essential configurations.

*   **JWT Secret and Expiration**:
    ```properties
    app.jwt.secret=YOUR_JWT_SECRET_KEY_HERE # IMPORTANT: Change this to a strong, unique key
    app.jwt.expirationMs=86400000 # 24 hours in milliseconds
    ```
*   **Superadmin Secret Key**:
    ```properties
    superadmin.secret-key=YOUR_SUPERADMIN_SECRET_KEY_HERE # IMPORTANT: Change this to a strong, unique key
    ```
    **Note**: For production environments, it's highly recommended to manage these sensitive keys using environment variables or a dedicated secrets management service.

### Building the Project

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Endpoints

### Base URL

`http://localhost:8080/api/v1/auth`

### 1. Register a New User

*   **Endpoint**: `POST /register`
*   **Description**: Creates a new user account.
*   **Request Body (JSON)**:
    ```json
    {
      "username": "testuser",
      "email": "test@example.com",
      "password": "password123"
    }
    ```
*   **Success Response (200 OK)**:
    ```json
    {
      "success": true,
      "message": "User registered successfully",
      "data": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "roles": [
          {
            "id": 1,
            "name": "ROLE_USER"
          }
        ]
      }
    }
    ```

### 2. User Login

*   **Endpoint**: `POST /login`
*   **Description**: Authenticates a user and returns a JWT.
*   **Request Body (JSON)**:
    ```json
    {
      "usernameOrEmail": "test@example.com",
      "password": "password123",
      "superAdminSecretKey": "YOUR_SUPERADMIN_SECRET_KEY_HERE" // Optional: for superadmin access
    }
    ```
*   **Success Response (200 OK)**:
    ```json
    {
      "success": true,
      "message": "Login successful",
      "data": {
        "token": "eyJhbGciOiJIUzI1Ni...",
        "userId": 1,
        "username": "testuser",
        "email": "test@example.com",
        "roles": [
          {
            "id": 1,
            "name": "ROLE_USER"
          },
          {
            "id": 3,
            "name": "ROLE_SUPER_ADMIN" // Only if superAdminSecretKey is provided and correct
          }
        ]
      }
    }
    ```

### 3. Get Current Authenticated User

*   **Endpoint**: `GET /me`
*   **Description**: Retrieves details of the currently authenticated user. Requires a valid JWT.
*   **Headers**:
    *   `Authorization: Bearer <YOUR_JWT_TOKEN>`
*   **Success Response (200 OK)**:
    ```json
    {
      "success": true,
      "message": "User data retrieved successfully",
      "data": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "roles": [
          {
            "id": null, // Role ID might be null for dynamically added roles
            "name": "ROLE_USER"
          },
          {
            "id": null, // Role ID might be null for dynamically added roles
            "name": "ROLE_SUPER_ADMIN" // Will be present if superAdminSecretKey was used and user is an admin
          }
        ]
      }
    }
    ```

**Note**: The `roles` array in the `/me` endpoint now reflects the roles present in the current session's JWT, including any dynamically assigned roles like `ROLE_SUPER_ADMIN`. For dynamically added roles, the `id` field will be `null` as it's not retrieved from the database.

## Authentication

This API uses JWT (JSON Web Tokens) for authentication. After a successful login, the server returns a JWT. This token must be included in the `Authorization` header of subsequent requests to protected endpoints in the format `Bearer <YOUR_JWT_TOKEN>`.

## Dynamic Superadmin Role

The API supports a dynamic superadmin role. If a user provides the correct `superAdminSecretKey` in the login request **and already possesses the `ROLE_ADMIN`**, a `ROLE_SUPER_ADMIN` authority is temporarily added to their JWT. This allows for session-based elevation of privileges for administrators without permanently assigning the role in the database.

## Error Handling

The API provides custom JSON error responses for authentication failures (e.g., 401 Unauthorized) and validation errors (e.g., 400 Bad Request).

## Database

The project uses Spring Data JPA. By default, it's configured with an in-memory H2 database for easy development and testing. You can configure it to use other relational databases by modifying `application.properties`.

## API Documentation (Swagger UI)

Access the interactive API documentation (Swagger UI) at:
`http://localhost:8080/swagger-ui.html`

This interface allows you to explore all available endpoints, their request/response schemas, and even test them directly from your browser.
