# Overview

Module 3 continues from [Module 2](../module2/README.md)'s course catalog. 

## Why module 3 is its own Spring Boot application

Module 3 isn't just a different package — it's a separate Maven module with its own `pom.xml`, its own
`Module3Application` main class, its own port, and its own database. See the
[repo root README](../../README.md#project-structure) for the overall structure.  This will be true for subsequent modules as well.

## Changes in this module

In this module, we use Spring Security to begin to secure our project.  Take a look at the changes that were implemented for the REST contoller in the [README](./rest/README.md)