# Job4j Cars 
## Overview
In this project I tried to create a web application about car selling. There are car sale announcemens, which include: description, car brand, body type and photo. Also, the announcement has a status - sold or not. 
Here I used bottom-up design - the application has been being created from database to user's interface. I used hibernate to work with database. 
##Functions 
- Announcements management:
  - Place new announcements
  - Edit your announcements
  - Delete your announcements
- Announcements status:
  - It can be sold or available
- Announcements listing:
  - View all announcements
  - View only available
  - View only sold
- Users:
  - Register your user
  - Login to your account 
## Technologies
- Java 17
- Spring Boot
- Sptring MVC / Sptring WEB
- Thymeleaf
- Hibernate
- PostgreSQL
- Liquibase
- Bootstrap 5
- Maven
- Lombok
## Views 

## How to run 
1. Clone the repository:
   ```bash
   git clone https://github.com/1oogabooga1/job4j_cars.git
   cd job4j_cars
   ```
2. Configure DB:
   ```bash
   spring.datasource.url=jdbc:postgresql://localhost:5432/job4j_cars
   spring.datasource.username=postgres
   spring.datasource.password=password
   spring.datasource.driver-class-name=org.postgresql.Driver
   spring.jpa.show-sql=false
   ```
3. Build & run:
   ```bash
   mvn clean package
   java -jar target/job4j_cars.jar
   ```
4. Open in browser:
https://localhost:8080/

## Links 
| Page   | Link   |
|--------|--------|
| Main | http://localhost:8080   |

## Screenshots
