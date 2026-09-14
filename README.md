![Southern New Hampshire University](./images/snhu.png)

# Overview

This repo contains sample code to help illustrate topics for [SNHU CS-600](https://www.snhu.edu/admission/academic-catalogs#/courses/ryvmyfvb1e)

It also contains supporting material for projects and assignments.

## Project structure

This is a Maven **multi-module** project: each module of the course is its own independent, runnable
Spring Boot application, in its own `moduleN/` folder with its own `pom.xml`. Later modules start as a
copy of the previous one and then diverge, so every module stays exactly as it was when it was covered
in class. The root [`pom.xml`](./pom.xml) just aggregates them so `./mvnw install` can build everything in one pass;
it isn't an app itself.

Build everything from the repo root:

```bash
./mvnw install
```

Run one module at a time, for instance to run the code for module 2

```bash
cd module2
docker compose up -d
../mvnw spring-boot:run
```

Or module 3

```bash
cd module3
docker compose up -d
../mvnw spring-boot:run
```

Each module's own `docs/moduleN/` walkthrough has the exact commands and ports for that module.

## Course Progression

Each module of the course has a walkthrough under [`docs/`](./docs), alongside the corresponding code
in `moduleN/src/main/java/com/scttech/cs600/moduleN`:

- [Module 2: Introduction to Spring Data JPA](./docs/module2/README.md)
- [Module 3: TLS and HTTP Basic Auth](./docs/module3/README.md)
- [Module 4: Database Design (PlantUML ERD)](./docs/module4/README.md)
- [Module 5: Introduction to Vaadin](./docs/module5/README.md)
- [Module 6, Feature 1: Login Screen (Spring Security + Vaadin, Agile/DDD design)](./docs/module6_feature1/README.md)
- [Module 6, Feature 2: Registrar Dashboard (post-login landing page)](./docs/module6_feature2/README.md)
- [Module 7: Build out functionality](./docs/module7/README.md)

- [Troubleshooting: A problem/solution guide for common errors](./docs/troubleshooting/README.md)

- [`Vaadin Examples`](./docs/vaadin_example/README.md): a UI-only reference catalog of Vaadin components and layouts
