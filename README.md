# FreelancerService

FreelancerService is an API that allows freelancers to register and upload their documents for verification. Notifications are sent to freelancers for every document manipulation (create, update, delete).

## Technologies Used
This project utilizes the following technologies:
* Spring Boot 3.3.2
* Spring Data JPA
* PostgreSQL 14
* Maven
* Java 21
* JUnit 5
* Docker
* Kafka (for message delivery)
* [REST Assured](https://rest-assured.io/) and [Testcontainers](https://testcontainers.com/) (for Spring integration tests using a container)

## Project Structure
The service is divided into two modules:
1. freelancer-api
2. freelancer-web

### freelancer-api
This module contains the Application Programming Interfaces (APIs) for the service and generates Swagger documentation. The Swagger files are automatically generated in JSON and YAML formats and stored in the freelancer-api/target/swagger directory. These files can be used by downstream APIs for code generation or uploaded to a documentation repository. Additionally, these files can be moved to a separate Git repository, allowing consumers to submit pull requests for changes.

### freelancer-web
This module implements the APIs defined in the freelancer-api module and exposes them as REST endpoints.

## How to Run the Project

1. Clone the repository:
   ```sh
   git clone https://github.com/hi4566/freelancer-service.git

2. Navigate to the root directory and start Docker:
   ```sh
   docker compose up

3. Build the project:
   ```sh
   mvn clean install

4. Run the Spring Boot application:
   ```sh
   mvn spring-boot:run

## Viewing Swagger Documentation
The Swagger files (in JSON and YAML formats) can be found in the freelancer-api/target/swagger directory. You can upload these files to Swagger Editor to view the documentation.

## How to upload document via postman
To upload a document for a freelancer, use the POST /api/freelancer/documents endpoint. The request requires a JSON body and a file, as shown in the image below:

![img.png](img.png)