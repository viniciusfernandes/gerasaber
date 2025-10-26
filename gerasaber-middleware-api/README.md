# Gerasaber Middleware API

A Spring Boot middleware application that acts as a communication bridge between a user-facing browser client and a remote n8n automation server integrated with ChatGPT.

## Features

- **File Upload Endpoint**: Accepts multipart form data with files and processing instructions
- **Webhook Endpoint**: Receives processed PDF files from n8n/ChatGPT workflows
- **Local File Storage**: Stores files with timestamp-based directory organization
- **Clean Architecture**: Follows DDD principles with proper separation of concerns

## Tech Stack

- Java 17
- Spring Boot 3.x with Virtual Threads
- Gradle 8.7
- Docker & Docker Compose
- SLF4J + Logback for logging

## API Endpoints

### Upload Files
```
POST /api/upload
Content-Type: multipart/form-data

Parameters:
- files: One or more files (PDF, DOCX, TXT, etc.)
- promptDescription: Text describing processing instructions

Response:
{
  "data": {
    "message": "Request accepted for processing",
    "timestamp": "2025-01-26T12:00:00Z",
    "requestId": "uuid"
  }
}
```

### Webhook Response
```
POST /api/webhook/n8n-response
Content-Type: multipart/form-data

Parameters:
- file: Generated PDF file
- requestId: Original request identifier
- filename: Optional filename

Response:
{
  "data": {
    "message": "PDF successfully stored",
    "path": "/var/app/files/2025-01-26/summary.pdf",
    "filename": "summary.pdf"
  }
}
```

## Configuration

Configure the application via `application.yml` or environment variables:

```yaml
n8n:
  base-url: ${N8N_BASE_URL:http://localhost:5678}
  upload-endpoint: ${N8N_UPLOAD_ENDPOINT:/webhook/chatgpt-summarization}
  auth-token: ${N8N_AUTH_TOKEN:}

storage:
  local-path: ${STORAGE_LOCAL_PATH:/var/app/files}
```

## Running the Application

### Using Docker Compose
```bash
docker-compose up -d
```

### Using Gradle
```bash
./gradlew bootRun
```

### Building
```bash
./gradlew build
```

## Project Structure

```
src/main/java/br/com/gerasaber/
├── domain/
│   ├── entity/          # Domain entities
│   └── port/            # Port interfaces
├── infrastructure/
│   ├── adapter/         # Infrastructure adapters
│   ├── config/          # Configuration classes
│   └── exception/       # Exception handling
└── usecase/
    ├── file/upload/     # Upload use case
    │   ├── contract/    # DTOs
    │   ├── endpoint/    # REST controllers
    │   ├── mapping/     # Mappers
    │   └── service/     # Business logic
    └── webhook/         # Webhook use case
        ├── contract/    # DTOs
        ├── endpoint/    # REST controllers
        ├── mapping/     # Mappers
        └── service/     # Business logic
```

## Architecture Principles

- **Clean Architecture**: Clear separation between domain, application, and infrastructure layers
- **SOLID Principles**: Single responsibility, dependency inversion, and interface segregation
- **DDD Concepts**: Rich domain models with proper encapsulation
- **Storage Abstraction**: Pluggable storage implementations via port interfaces
