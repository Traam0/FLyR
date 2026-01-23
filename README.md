# flyR Flight Booking System

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Java Swing](https://img.shields.io/badge/Java_Swing-007396.svg?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)

A comprehensive flight booking application demonstrating modern client-server architecture using Java. Features a desktop client built with Java Swing and a robust JAX-RS backend, designed with clean, decoupled architecture following MVVM pattern.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture--core-concepts)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Technologies Used](#technologies-used)
- [Team Members](#team-members)
- [Known Issues](#known-issues)
- [License](#license)

## 🎯 Overview

The project is split into two main components:

1. **Client Application (`/src`)**: A desktop application built with Java Swing that provides the user interface for searching and viewing flights. It follows the Model-View-ViewModel (MVVM) pattern for a clean separation of UI, presentation logic, and data.

2. **Server Application (`/server`)**: A RESTful backend built with JAX-RS (Jersey and Grizzly) that handles business logic and data persistence. It exposes API endpoints for flight data and connects to a MySQL database.

## ✨ Features

- **Flight Search**: Search for one-way or round-trip flights based on departure/arrival cities, dates, and number of passengers
- **Seat Selection**: Interactive seat map with real-time availability
- **User Authentication**: Secure login system with role-based access
- **Dynamic UI**: Responsive and modern user interface built with custom Swing components
- **MVVM Architecture**: Clean separation of concerns with data-binding and commands
- **RESTful Backend**: Lightweight server providing flight data via well-defined REST API
- **Dependency Injection**: Custom IoC container manages object lifecycles
- **Database Connection Pooling**: Efficient management of database connections

## 🏗️ Architecture & Core Concepts

flyR is built upon a custom lightweight framework that promotes separation of concerns and modern development practices.

### Dependency Injection
The `core.dependencyInjection` package provides a custom IoC container:
- `ServiceCollection`: Used to register services
- `ServiceProvider`: Used to resolve dependencies
- `ServiceLifetime`: Supports `Singleton`, `Scoped`, and `Transient` service lifetimes

### MVVM Framework
The client leverages an MVVM implementation in `core.mvvm`:
- `Property<T>`: A wrapper for observable properties that allows UI components to subscribe to changes
- `Command` & `RelayCommand`: Implements the command pattern to decouple UI actions from logic
- `View` & `ViewModel`: Establishes clear separation between UI and state/logic

### Networking
The `core.networking` package abstracts API communication:
- `HttpRestClient`: Interface for making HTTP requests
- `JerseyHttpRestClient`: Concrete implementation using Jersey client

### Custom UI Components
The `shared` package contains reusable UI elements:
- `FlexPanelH` & `FlexPanelV`: Flexible layout managers with alignment and spacing options
- `Button`, `TextInput`: Custom-styled components with rounded borders and hover effects
- `MaterialColors`: Utility class providing Material Design color palette

## 📁 Project Structure

```
└── flyR/
    ├── server/                    # JAX-RS Backend Application
    │   └── src/
    │       └── main/
    │           ├── java/
    │           │   ├── controllers/      # REST API endpoints
    │           │   ├── core/             # Core backend framework (DI, Server)
    │           │   └── repositories/     # Data access layer
    │           └── resources/
    │               └── application.properties
    └── src/                       # Java Swing Client Application
        └── main/
            ├── java/
            │   ├── contracts/            # Data Transfer Objects (DTOs)
            │   ├── core/                 # Core client framework
            │   ├── mvvm/
            │   │   ├── models/           # Data models
            │   │   ├── viewModels/       # Presentation logic
            │   │   └── views/            # Swing UI panels
            │   ├── services/             # Client-side services
            │   └── shared/               # Reusable UI components
            └── resources/
                └── icons/
```

## 🚀 Getting Started

### Prerequisites

- **JDK 17** or later
- **MySQL Server**
- **Gradle** (wrapper included)

### Database Setup

1. Create a MySQL database named `flyR`:
   ```sql
   CREATE DATABASE flyR;
   ```

2. Execute the SQL script located in the database folder to set up tables and populate sample data.

3. Configure database credentials:
   - Open `server/src/main/resources/application.properties`
   - Update the connection string with your credentials:
   ```properties
   db.connectionString=jdbc:mysql://localhost:3306/flyR?user=YOUR_USERNAME&password=YOUR_PASSWORD&useSSL=false&allowPublicKeyRetrieval=true
   ```

### Running the Application

#### Option 1: Using IDE

1. **Run the Backend Server**:
   - Locate and run the `main` method in `server/src/main/java/Server.java`
   - Server will start on `http://localhost:8085/`

2. **Run the Client Application**:
   - After the server is running, locate and run the `main` method in `src/main/java/Application.java`
   - The desktop application window will open

#### Option 2: Using Gradle

```bash
# Build the project
./gradlew build

# Run the server (in one terminal)
./gradlew :server:run

# Run the client (in another terminal)
./gradlew :client:run
```


## 🔌 API Endpoints

The server exposes the following primary endpoints:

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/flights` | Retrieve all flights | - |
| GET | `/flights/search` | Search for flights | `departure`, `arrival`, `from`, `to`, `passengers`, `roundTrip` |
| GET | `/flights/{id}` | Get single flight by ID | `id` (path) |
| GET | `/flights/{id}/details` | Get flight with seat details | `id` (path) |
| POST | `/auth/login` | User authentication | `username`, `password` (body) |

## 🛠️ Technologies Used

### Client
- Java 17
- Java Swing
- Jackson (JSON processing)
- Jersey Client (HTTP communication)

### Server
- JAX-RS (Jersey)
- Grizzly HTTP Server
- MySQL
- JDBC

### Custom Framework
- Custom IoC Container
- Custom MVVM Framework
- Custom Connection Pooling
- Custom UI Components

## 👥 Team Members

| Name | Role | Contribution |
|------|------|--------------|
| KHTOU YOUNES | REST | Swing App UI and the JAX-RS API |
| Boukhris Hamza | SOAP | SOAP SERVICES |
| Chalabi Nada | RMI | RMI Service |
| Ivora Only | Socket | Notifications via sockets |

## ⚠️ Known Issues

- **Potential Memory Leaks**: The `Property<>` class keeps strong references to lambdas registered in views. Memory leaks may occur if `PropertyChangeListener` references the view using `this` keyword. 
  - **Proposed Fix**: Add a dispose method to the `View` interface OR register `PropertyChangeListener` as `WeakReference` within the bind method.

## 📝 Future Enhancements

- [ ] Implement booking confirmation and payment processing
- [ ] Add reservation management for users
- [ ] Implement email notifications
- [ ] Add flight history and analytics
- [ ] Support for multiple currencies
- [ ] Mobile application support
- [ ] Real-time flight status updates

## 📄 License

This project is developed for educational purposes as part of [Course Name/Project Name].

---

**Built with ❤️ by the flyR Team**
