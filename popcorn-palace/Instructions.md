
# Project Instructions

## Prerequisites

Before running the project, ensure you have the following installed on your local machine:

- **Docker**: Make sure Docker is installed and running on your system. You can download it from [Docker's official website](https://www.docker.com/get-started).
- **Docker Compose**: Docker Compose is used for defining and running multi-container Docker applications. It should come bundled with Docker, but if not, you can follow the installation instructions [here](https://docs.docker.com/compose/install/).

## Project Setup

1. **Clone the repository**:
   If you haven't cloned the repository yet, run the following command:

   ```bash
   git clone https://github.com/liron977/Popcorn-Palace.git
   cd PopcornPalace/popcorn-palace
   ```

2. **Build and Run the Application using Docker Compose**:

   The project uses Docker Compose to manage the containers for the application. Make sure your `compose.yml` file is located in the root directory of your project.

   To start the services, run the following command:

   ```bash
     docker-compose up -d

   ```
3. **Check if the containers are running**:

   Once the application is up and running, check if the containers are functioning properly:

   ```bash
   docker-compose ps
   ```

   This will list all running containers.

4.  **Access a PostgreSQL database**:

The command you provided is used to access a PostgreSQL database running inside a Docker container

   ```bash
      docker exec -it popcorn-palace-db-1 psql -U popcorn-palace popcorn-palace
   ```


## Build the project

 ```bash
     mvn clean install
 ```
     
## Testing  the Application

1. **Run the tests**:

   Once the application is running, you can test it . To run the tests for the application, use the following command:

   ```bash
   mvn test
   ```

## Run the Application

```bash
mvn spring-boot:run
```


2. **Verify API Endpoints**:
   You can now test the API endpoints using tools like [Postman](https://www.postman.com/) or `curl`. The endpoints will be available on the port specified in the `compose.yml` file (for example, `localhost:8080`).

   Sample API endpoints:

    - **GET all movies**:
      ```bash
      GET http://localhost:8080/movies/all
      ```

    - **POST add a movie**:
      ```bash
      POST http://localhost:8080/movies
      ```

    - **POST update a movie**:
      ```bash
      POST http://localhost:8080/movies/update/{movieTitle}
      ```

    - **DELETE delete a movie**:
      ```bash
      DELETE http://localhost:8080/movies/{movieTitle}
      ```

## Stopping the Application

To stop the application and remove the containers, use the following command:

```bash
docker-compose down
```

This will:
- Stop and remove the containers.
- Remove any networks that were created.

---

## Troubleshooting

- **If you're facing issues with Docker**: Ensure that Docker is running correctly and check for any errors in the `docker-compose` logs:

  ```bash
  docker-compose logs
  ```

- **Database Connection Issues**: Check that the database container is properly linked to the application container in the `compose.yml` file.

- **Test Failures**: If tests are failing, check the test output for specific error messages, and ensure the application is running correctly before re-running tests.

---

