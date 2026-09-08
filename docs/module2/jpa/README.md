# Module 2: Introduction to Spring Data JPA

This module introduces [Spring Data JPA](https://spring.io/projects/spring-data-jpa) by modeling the
first table of a course catalog: a single `Course` entity backed by a Postgres database running in
Docker. Later modules build on this same catalog.

## What you'll learn

- Mapping a Java class to a database table with `@Entity`
- Defining a repository interface with Spring Data JPA (`JpaRepository`) to get CRUD operations for free
- Deriving a custom query method (`findByCourseCode`) from its method name
- Testing a repository against a real Postgres database using [Testcontainers](https://testcontainers.com/)

## Project layout

| File | Purpose |
| --- | --- |
| [`Course.java`](../../src/main/java/com/scttech/cs600/module2/model/Course.java) | The JPA entity mapped to the `courses` table |
| [`CourseRepository.java`](../../src/main/java/com/scttech/cs600/module2/repository/CourseRepository.java) | Spring Data JPA repository for `Course` |
| [`CourseRepositoryTest.java`](../../src/test/java/com/scttech/cs600/module2/CourseRepositoryTest.java) | CRUD tests run against a throwaway Postgres container |
| [`docker-compose.yml`](../../docker-compose.yml) | Starts a local Postgres instance for running the app |
| [`application.properties`](../../src/main/resources/application.properties) | Datasource and JPA configuration |

The package layout mirrors this doc structure: `com.scttech.cs600.module2.model` and
`com.scttech.cs600.module2.repository` under `module2`, documented here under `docs/module2`.

## Running it locally

### 1. Start Postgres

```bash
docker compose up -d
```

This starts a Postgres 16 container (database `cs600db`, user/password `postgres`) on
`localhost:5432`.

### 2. Run the application

```bash
./mvnw spring-boot:run
```

On startup, Hibernate creates the `courses` table automatically (`spring.jpa.hibernate.ddl-auto=update`).
This is convenient while we're iterating in class, but isn't how you'd manage schema changes against a
real production database — see the comment in `application.properties`.

### 3. Run the tests

```bash
./mvnw test -Dtest=CourseRepositoryTest
```

These tests don't touch the Postgres container started in step 1 — they spin up their own disposable
Postgres container via Testcontainers, so they'll pass on a clean checkout with nothing but Docker
running. That's what lets `CourseRepositoryTest` double as a runnable, self-contained demonstration of
each CRUD operation.

