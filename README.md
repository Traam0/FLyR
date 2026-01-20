# flyR
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=awhite)
![Java Swing](https://img.shields.io/badge/Java_Swing-007396.svg?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)

flyR is a comprehensive flight booking application demonstrating a client-server architecture using Java. It features a modern Java Swing desktop client and a robust JAX-RS backend, designed with a clean, decoupled architecture.

## Overview

The project is split into two main components:

1.  **Client Application (`/src`)**: A desktop application built with Java Swing that provides the user interface for searching and viewing flights. It follows the Model-View-ViewModel (MVVM) pattern for a clean separation of UI, presentation logic, and data.
2.  **Server Application (`/server`)**: A RESTful backend built with JAX-RS (Jersey and Grizzly) that handles business logic and data persistence. It exposes API endpoints for flight data and connects to a MySQL database.

## Features

*   **Flight Search**: Search for one-way or round-trip flights based on departure/arrival cities, dates, and number of passengers.
*   **Dynamic UI**: A responsive and modern user interface built with custom Swing components and layouts.
*   **MVVM Architecture**: The client application uses a custom MVVM framework for maintainability and testability, with data-binding properties and commands.
*   **RESTful Backend**: A lightweight server providing flight data via a well-defined REST API.
*   **Dependency Injection**: A custom IoC container manages object lifecycles and dependencies in both the client and server.
*   **Database Connection Pooling**: The server uses a custom connection pool for efficient management of database connections to MySQL.

## Architecture & Core Concepts

flyR is built upon a custom lightweight framework that promotes separation of concerns and modern development practices.

*   **Dependency Injection**: The `core.dependencyInjection` package provides a custom IoC container.
    *   `ServiceCollection`: Used to register services.
    *   `ServiceProvider`: Used to resolve dependencies.
    *   `ServiceLifetime`: Supports `Singleton`, `Scoped`, and `Transient` service lifetimes.
*   **MVVM Framework**: The client leverages an MVVM implementation in `core.mvvm`.
    *   `Property<T>`: A wrapper for observable properties that allows UI components to subscribe to changes.
    *   `Command` & `RelayCommand`: Implements the command pattern to decouple UI actions from the logic in the ViewModel.
    *   `View` & `ViewModel`: Establishes a clear separation between the UI (`FlightSearchView`) and its state/logic (`FlightSearchViewModel`).
*   **Networking**: The `core.networking` package abstracts API communication.
    *   `HttpRestClient`: An interface for making HTTP requests.
    *   `JerseyHttpRestClient`: A concrete implementation using the Jersey client for communicating with the backend.
*   **Custom UI Components**: The `shared` package contains reusable UI elements to create a consistent look and feel.
    *   `FlexPanelH` & `FlexPanelV`: Flexible layout managers for arranging components horizontally and vertically with alignment and spacing options.
    *   `Button`, `TextInput`: Custom-styled components with rounded borders and hover effects.
    *   `MaterialColors`: A utility class providing a palette of Material Design colors.

## Getting Started

### Prerequisites

*   JDK 17 or later
*   MySQL Server

### Database Setup

1.  Create a MySQL database named `flyR`.
2.  The project includes a SQL script to set up the necessary tables and populate them with sample data (located at the root of the database folder in the repo). Execute this script against your `flyR` database.
3.  Configure your database credentials in the server's properties file:
    *   Open `server/src/main/resources/application.properties`.
    *   Update the `db.connectionString` with your MySQL username and password.

    ```properties
    db.connectionString:jdbc:mysql://localhost:3306/flyR?user=YOUR_USERNAME&password=YOUR_PASSWORD&useSSL=false&allowPublicKeyRetrieval=true
    ```

### Running the Application

You can run the server and client directly from your IDE.

1.  **Run the Backend Server**:
    *   Locate and run the `main` method in `server/src/main/java/Server.java`.
    *   The server will start on `http://localhost:8085/`.

2.  **Run the Client Application**:
    *   After the server is running, locate and run the `main` method in `src/main/java/Application.java`.
    *   The desktop application window will open, ready to search for flights.

## Project Structure

```
└── traam0-flyr/
    ├── server/             # JAX-RS Backend Application
    │   └── src/
    │       └── main/
    │           ├── java/
    │           │   ├── controllers/      # REST API endpoints
    │           │   ├── core/             # Core backend framework (DI, Server Abstraction)
    │           │   └── repositories/     # Data access layer
    │           └── resources/
    │               └── application.properties # Database configuration
    └── src/                # Java Swing Client Application
        └── main/
            ├── java/
            │   ├── contracts/            # Data Transfer Objects (DTOs)
            │   ├── core/                 # Core client framework (MVVM, DI, Networking, etc.)
            │   ├── mvvm/
            │   │   ├── models/           # Data models (Flight, Client, etc.)
            │   │   ├── viewModels/       # Presentation logic
            │   │   └── views/            # Swing UI panels
            │   ├── services/             # Client-side services to interact with the API
            │   └── shared/               # Reusable UI components and layouts
            └── resources/
                └── icons/
```

## API Endpoints

The server exposes the following primary endpoints:

*   `GET /flights`: Retrieves a list of all flights.
*   `GET /flights/search`: Searches for flights with query parameters.
    *   **Params**: `departure`, `arrival`, `from`, `to` (optional), `passengers`, `roundTrip` (boolean).
*   `GET /flights/{id}`: Retrieves a single flight by its ID.