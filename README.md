CPE Data Collection and RESTful API Development
1. Objective
This project is designed to evaluate the ability to work with large-scale XML data, interact with persistent databases, and build a high-performance RESTful API. The system retrieves data from the official NIST CPE (Common Platform Enumeration) dictionary, extracts specific attributes, and stores them in a PostgreSQL database for efficient querying.

2. Technical Stack
Language: Java 25

Framework: Spring Boot 3.x (Spring Web, Spring Data JPA)

Database: PostgreSQL 17

Build Tool: Maven

Parser: StAX (Streaming API for XML) for memory-efficient processing

3. Core Features
A. High-Volume Data Ingestion (ETL)
Streaming Parser: Uses XMLStreamReader to process the 650MB+ XML file without loading the entire content into RAM.

Batch Processing: Implements Hibernate batch inserts (size=500) to save 1.4 million records in minutes rather than hours.

Memory Management: Periodically flushes the EntityManager to prevent OutOfMemoryError during the ingestion of 1,430,488 entries.

B. Database Schema
The system maps the XML structure to a relational PostgreSQL schema:

CPE Title: String (VARCHAR)

CPE URIs: Version 2.2 and 2.3 URIs (TEXT)

Deprecation Dates: Handled as LocalDate objects

Reference Links: Stored in a one-to-many relationship table (cpe_reference_links)

C. REST API Endpoints
GET /api/cpes: Returns a paginated list of all entries.

Query Params: page (default 0), size (default 10).

GET /api/cpes/search: Dynamic searching.

cpeTitle: Partial match (case-insensitive).

deprecationDate: Returns all CPEs deprecated prior to the provided date.

4. Setup & Installation
Prerequisites
PostgreSQL: Install and create a database named cpe_database.

Java 25: Ensure your JDK is properly configured.

Data File: Download the official CPE Dictionary XML and place it in src/main/resources/official-cpe-dictionary_v2.3.xml.

Configuration
Update src/main/resources/application.properties:

Properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cpe_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
Running the Project
Run mvn clean install to build the project.

Start the application via SecurinApplication.java.

The DataInitializer will automatically trigger the ingestion process. Look for the >>> Processed X entries... logs in the terminal.

5. Challenges Overcome
XML Security Limits: Resolved jdk.xml.maxGeneralEntitySizeLimit by adjusting system properties for large-file processing.

Data Truncation: Fixed DataIntegrityViolationException by increasing column lengths for extremely long reference URLs.

GitHub File Limits: Successfully managed a repository by excluding the 650MB data file from version control while providing clear documentation for manual setup.
