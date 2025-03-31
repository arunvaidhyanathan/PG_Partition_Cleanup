# PostgreSQL Partition Cleanup Application

This Spring Boot application helps identify and clean up empty partitions in PostgreSQL tables. It scans the CADS schema for tables with partitions, identifies empty partitions, and provides endpoints to drop them.

## Technologies Used

- Java 17
- Spring Boot 3.2.3
- PostgreSQL (Supabase)
- Spring Data JPA
- Maven

## Project Structure

The application follows a standard Spring Boot architecture with Controller-Service-Repository layers:

- **Controller**: REST endpoints for partition management
- **Service**: Business logic for identifying and dropping partitions
- **Repository**: Data access layer for storing table and partition information
- **Model**: Entity classes for database tables

## Database Tables

1. **TABLE_LIST**: Stores information about tables in the CADS schema
   - id: Primary key
   - tableName: Name of the table
   - tableSchema: Schema name (CADS)
   - hasPartitions: Boolean indicating if the table has partitions

2. **EMPTY_PARTITIONS**: Stores information about empty partitions
   - id: Primary key
   - tableName: Parent table name
   - partitionName: Name of the empty partition
   - identifiedAt: Timestamp when the partition was identified as empty
   - isDropped: Boolean indicating if the partition has been dropped
   - droppedAt: Timestamp when the partition was dropped

## Getting Started

### Prerequisites

- Java 17 JDK
- Maven

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Build the application:

```bash
mvn clean package
```

4. Run the application:

```bash
java -jar target/pg-partition-cleanup-0.0.1-SNAPSHOT.jar
```

Alternatively, you can run it directly with Maven:

```bash
mvn spring-boot:run
```

## API Endpoints

### 1. Identify Tables

Scans the CADS schema and identifies all tables, storing them in the TABLE_LIST table.

```bash
curl -X POST http://localhost:8080/api/partitions/identify-tables
```

### 2. Identify Empty Partitions

Scans all tables with partitions and identifies empty partitions, storing them in the EMPTY_PARTITIONS table.

```bash
curl -X POST http://localhost:8080/api/partitions/identify-empty-partitions
```

### 3. Drop Empty Partitions

Drops all identified empty partitions that have not been dropped yet.

```bash
curl -X POST http://localhost:8080/api/partitions/drop-empty-partitions
```

### 4. Get Empty Partitions

Returns a list of all empty partitions that have not been dropped yet.

```bash
curl -X GET http://localhost:8080/api/partitions/empty-partitions
```

## Workflow

1. Start by calling the `/identify-tables` endpoint to scan the CADS schema and identify all tables.
2. Call the `/identify-empty-partitions` endpoint to scan all tables with partitions and identify empty partitions.
3. Call the `/empty-partitions` endpoint to get a list of all empty partitions that have not been dropped yet.
4. Call the `/drop-empty-partitions` endpoint to drop all identified empty partitions.

## Notes

- The application uses transaction management to ensure data consistency.
- Empty partitions are detached from their parent tables before being dropped.
- The application logs all operations for auditing purposes.