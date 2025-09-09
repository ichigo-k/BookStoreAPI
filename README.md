# 📚 BookStoreAPI

BookStoreAPI is a backend application built with **Spring Boot** for managing books and authors.  
It exposes RESTful endpoints for performing CRUD operations and can be easily run using **Docker**.

---

## 🚀 Features
- Manage **Books** (create, read, update, delete).
- Manage **Authors**.
- Manage **Categories**.
- User Authentication and Auhtorisation
- RESTful API design.
- Containerized with **Docker** and **Docker Compose** for easy setup.

---

## 🛠️ Prerequisites
Before you begin, ensure you have the following installed:
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

---

## ▶️ Getting Started

### 1. Clone the Repository
```bash
  git clone https://github.com/ichigo-k/BookStoreAPI.git
  cd BookStoreAPI
```

### 2. Run with Docker

##### Start the application using Docker Compose:
```bash
  docker-compose up --build
```

##### The API will be available at:
```bash
  http://localhost:8080
```