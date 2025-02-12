# Kotlin Developer Technical Assessment

## Overview
Create a reactive REST API for a job application tracking system using Kotlin and Spring WebFlux. The system should allow creating and querying job applications with different statuses.

## Time Limit
1 hour

## Technical Requirements

### Required Stack
- Kotlin
- Spring WebFlux

### Core Features

#### 1. Data Model
```kotlin
data class JobApplication(
    val id: String = UUID.randomUUID().toString(),
    val candidateName: String,
    val email: String,
    val position: String,
    val status: ApplicationStatus = ApplicationStatus.NEW
)

enum class ApplicationStatus {
    NEW,
    IN_REVIEW,
    REJECTED,
    ACCEPTED
}

data class CreateApplicationRequest(
    val candidateName: String,
    val email: String,
    val position: String
)

data class UpdateStatusRequest(
    val status: ApplicationStatus
)
```

#### 2. API Endpoints
Implement the following reactive endpoints:

```kotlin
@RestController
@RequestMapping("/api/applications")
class ApplicationController(private val service: ApplicationService) {
    // POST /api/applications
    // Create new application
    // Returns: JobApplication

    // GET /api/applications
    // Get all applications
    // Returns: JobApplication

    // GET /api/applications/{id}
    // Get application by id
    // Returns: JobApplication

    // PUT /api/applications/{id}/status
    // Update application status
    // Returns: obApplication

    // GET /api/applications/position/{position}
    // Find applications by position
    // Returns: JobApplication
}
```

#### 3. Service Layer
Implement a reactive service layer:

```kotlin
@Service
class ApplicationService(private val repository: ApplicationRepository) {
    suspend fun submitApplication(request: CreateApplicationRequest): JobApplication
    suspend fun updateApplicationStatus(id: String, status: ApplicationStatus): JobApplication
    suspend fun findByPosition(position: String): List<JobApplication>
}
```

#### 4. Repository Layer
Implement an in-memory reactive repository:

```kotlin
interface ApplicationRepository {
    suspend fun save(application: JobApplication): JobApplication
    suspend fun findById(id: String): JobApplication?
    suspend fun findAll(): List<JobApplication>
    suspend fun findByPosition(position: String): List<JobApplication>
}
```

### Required Implementations

1. Validation
    - Email format validation using extension function
    - Required fields validation

2. Error Handling
    - Use appropriate HTTP status codes
    - Return meaningful error messages
    - Handle not found scenarios

3. Testing
    - Write tests for endpoints / services
    - Test error scenarios
    - Test happy path paths

## Example Usage

```kotlin
// Create Application
POST /api/applications
{
    "candidateName": "John Doe",
    "email": "john@email.com",
    "position": "Software Engineer"
}

// Update Status
PUT /api/applications/{id}/status
{
    "status": "IN_REVIEW"
}

// Get Applications by Position
GET /api/applications/position/engineer
```

## Expected Behavior
- Return appropriate HTTP status codes:
    - 201 for successful creation
    - 200 for successful updates/retrieval
    - 404 when application not found
    - 400 for validation errors
- Case-insensitive position search
- Reactive streams for list operations

## Bonus Points (if time allows)
- Implement reactive validation using Validator interface
- Add rate limiting

## Definition Of Done
- Make sure tests are runnable with good coverage
- Can start the application 