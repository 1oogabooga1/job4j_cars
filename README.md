# Job4j Cars 
![CI GitHubAction](https://github.com/1oogabooga1/job4j_cars/actions/workflows/maven.yml/badge.svg)
## Overview
Job4j Cars is a web application for publishing and managing car sale advertisements. Each post contains a description, car brand, engine type, photo, creation date, and sale status.
The project was developed using a bottom-up approach: from database schema design and Hibernate entity mapping to service logic, controllers, and the Thymeleaf-based user interface.

## Features 
- Post management:
  - Create new post.
  - Upload and display car photos.
  - Edit post description, car brand, engine type, and photo.
  - Mark a car as sold.
  - Delete posts.
- Listing and filtering:
  - View all car sale posts.
  - View posts with photos.
  - View posts published within the last day.
  - Filter posts by car brand.
  - Display post status: sold or in stock.
- User access:
  - Register a new account.
  - Log in and log out.
  - Restrict access to posts for unauthenticated users.
  - Allow only post owners to edit, delete, or mark their own posts as sold.
- File storage:
  - Store uploaded car images locally.
  - Save image metadata in the database.
  - Display uploaded images in post listings and post details.
- Database integration:
  - Store users, posts, cars, brands, engines, and photos in PostgreSQL.
  - Manage database schema changes with Liquibase.
  - Use Hibernate ORM for entity mapping and persistence.
## User Roles and Access Control
The application supports session-based user authentication. Only registered users can access car sale posts and use the main functionality of the application.
There are two main interaction scenarios:
- **Post owner**:
  - Can create new car sale posts.
  - Can edit their own posts.
  - Can mark their cars as sold.
  - Can delete their own posts.
- **Regular user**:
  - Can browse available car sale posts.
  - Can view post details and filter listings.
  - Cannot edit, delete, or mark as sold posts created by other users.
This access control logic helps separate owner actions from regular user actions and makes the application closer to a real marketplace system.
## Technologies
- Java 17
- Spring Boot
- Sptring MVC / Sptring WEB
- Thymeleaf
- Hibernate
- PostgreSQL
- H2
- Liquibase
- Bootstrap 5
- Maven
- Lombok
- Mockito
- Jacoco
## Architecture
The project follows a layered MVC architecture:
- **Controller layer** handles HTTP requests and prepares data for Thymeleaf views.
- **Service layer** contains business logic such as post creation, editing, photo replacement, access checks, and deletion logic.
- **Repository layer** works with Hibernate and database queries.
- **Database layer** stores application data and is managed through Liquibase migrations.
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
| Main | http://localhost:8080 |
| All Posts | http://localhost:8080/posts/allPosts |
| Posts with photo | http://localhost:8080/posts/photoPosts |
| Posts for the last day | http://localhost:8080/posts/lastDayPosts |
| Creation page | http://localhost:8080/posts/create |
| Post page | http://localhost:8080/posts/post{id} |
| Login page | http://localhost:8080/users/login |
| Register page | http://localhost:8080/users/register |
| Error page | http://localhost:8080/errors/404 |

## Screenshots
<img width="1440" height="595" alt="login" src="https://github.com/user-attachments/assets/aa8f7bce-2385-491e-bfa4-4c19fac71e23" />

<img width="1437" height="710" alt="register" src="https://github.com/user-attachments/assets/69375a98-569f-4d24-948a-f46b66805931" />

<img width="1440" height="637" alt="main" src="https://github.com/user-attachments/assets/a5af9bb6-68f7-4e1a-bd9c-6f5d5e3a40ac" />

<img width="1440" height="769" alt="create1" src="https://github.com/user-attachments/assets/585cc224-1b9d-441e-9295-c86c7befe26d" />

<img width="1440" height="754" alt="create2" src="https://github.com/user-attachments/assets/4c89afd9-ba80-44f5-905c-33856c5621c7" />

<img width="1440" height="737" alt="create3" src="https://github.com/user-attachments/assets/5087e86d-f3c7-47d4-b636-fc6d0bf5cefb" />

<img width="1440" height="728" alt="create4" src="https://github.com/user-attachments/assets/a8477a9e-c0f7-4d4f-b480-23826bff3e00" />

<img width="1439" height="746" alt="create5" src="https://github.com/user-attachments/assets/56fd47db-9333-4621-99d9-20bb3afdfe89" />

<img width="1435" height="853" alt="listWithSold" src="https://github.com/user-attachments/assets/28dffa3f-4ce0-4878-b751-25e1e04e85ae" />

<img width="1439" height="644" alt="filterHaval" src="https://github.com/user-attachments/assets/f0ceabf2-e2e5-46a4-b29b-5d919cdbbb07" />

<img width="1440" height="607" alt="filterBMW" src="https://github.com/user-attachments/assets/f6209848-8b51-4c43-a183-3206767a1f4d" />

<img width="1439" height="898" alt="lasDay" src="https://github.com/user-attachments/assets/b8749c4f-c9c1-408e-ad5b-e0b155f4472b" />

<img width="1440" height="897" alt="postsWithPhoto" src="https://github.com/user-attachments/assets/66a06f9c-1374-4f38-b2e0-bac6c72faeb2" />

<img width="1440" height="412" alt="editError" src="https://github.com/user-attachments/assets/5986df4a-47fc-43a3-aa02-3fa1ec89e392" />

<img width="1438" height="439" alt="sellError" src="https://github.com/user-attachments/assets/90d9495d-1ffa-480d-9f28-5385e6f711d3" />



