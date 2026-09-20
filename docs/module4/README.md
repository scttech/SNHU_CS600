# Overview

Module 4 expands the database design to build out more of a full-fledged system rather than
just course listings. [Module 3](../module3/README.md)'s `courses` table (see
[`Course.java`](../../module3/src/main/java/com/scttech/cs600/module3/model/Course.java)) becomes
one piece of a normalized registrar-style schema that also covers departments, students, and
faculty/staff.

## Changes in this module

This module intentionally doesn't have its own code. Instead, we focus on the database design
before we implement it, using [PlantUML](https://plantuml.com/ie-diagram) entity-relationship
diagrams as the design artifact.

- [`erd/database-design.puml`](./erd/database-design.puml) — the full ERD

To view it: the [PlantUML VS Code extension](https://marketplace.visualstudio.com/items?itemName=jebbs.plantuml),
the `plantuml` CLI, or with the
[online PlantUML editor](https://www.plantuml.com/plantuml/uml/pLXVSzku4N_dfzZgPch9OqlfZNEJqVWyKMHbwjQ-UXmbRVgYWuWbYHW4U00OMOtotRiWHHcY53lDs3FLWnBjdz_-Th6x8FtQEcPScKlWpB4vitZCqOfKHT41N6bUIeIJ8IIblBU0sW8IRS1b20PJOPrX1kpIEipxv7g3YgEni10kguok9LFk_TKbV7W7OyqHyCwXiaAh8_Yzy4eYyJkCBo-EW70pvmex70mMYqM_qVPZdG-Q7z41HHbhzHcL8n0BfKLEaOmkqqoN3heM4RYExI2lqZWPt4n6vrUJViuhvePvFoQnDoGaW6EOV4Qp1CfH8cJC0eFtxo_EmPR6w9GvXCA8d979BIw787Yjlhwvk1hTV8I_Jpx2-UJjwFtb559KCyCKr_ciB0NlzjOHOeE4msVCmG3AWZS_fXTNatVJqTNrzD_0IYuSn5gMkRB038BEXID3I8pEmMN20XSiDIptULJ800JCsLn8uPP7C2yTiAAGb8m63BB2knWB1qL9g9-qKBNOGZVMfR4uAmmQ_ACKLZYq3Mchs2tEOd_mWljg9mADFOASkJWJAeLhRLrgq0x-TZs2xkjnHRshM8xmAtJw_NwdruDuSrHHb14EnCB44YDxArJ1V39IA7JB0a4Rb-b0ONIfUAoDGWEl0hdD6DSBpo1XqePGiMJMmfS8u0sBRzCAW6Xf0__CX4ClruQZgMK_dLGVaewCqOjjuTSe-XF7XBX1STkzwy7f_EmqzYR3dzwyUlNBYpTdTwU3Id1sEfYVRQoJohenFVdbrSzlJkvD_NWTFzs7q4OnuvYB66BccDJf4mSeg90iHUXiuephE1sgSmJWpzOjWMF1ZClfLrr_y5KJl0V3Qbeg2Q7vVsBDaSGVHZVZluvkkdzvGTKwBPNueyIpiyh2jr1eyV95hv9pjB4HXMycKayd_vgI-6l0ecxdHndG4TtpdRLirYFHOldpBakAABTD7g99m0QvS9P4xwv6bvSNlxNPxmvZc0gHNXiGuO1nempTPdijATRNf--_QzI5n25WNaYzHBzL4XQNqYq7rh4awG5J7AmhkM-Ev-hEciLMJpQC7k-7nd2codoEvk5ZJuInRjPkqURi9Nj0YJaJCaH_-JA4VwGtGwAqBtTdgFDsDAPRw6C7Lj2XQ-NjsyuQD1C6PvmvxtC-caugeNNCbNOdMOu9AwM3Wz5uUl5XSb11X6swFjqPHqVfR7TeGz0JtrTfjbuY_fQy3K8t4-vGbKHrbzfQhvD4n3YJEgvkyTqYXqIhzliXcf_qVDOQRmhmOnnA_XrBiR7xV-X0toSpdSpcmhWiR1XKHalf-pBifGUxbh1LAQL_sZW2Zh5a1Zdax9Cs5Eq7kl0P5jYxE4DUIg5Ilwc2B9zhLmK1_moJJRZjvUNGv8zsJUkON-xsYdNCx1uNAXxAzjz35cFVtu-I22-cKEyLFeksU8sxs-gqnHe_eQmpPUoq-LOp6KpGe8hHjXVIElEOKS4fdUtx5k3RSmjWwvF11sSww93lgKqpsRl5MHUYfGdW_-ULFVdjvX-NbvFpUbm0KiFuLeFqdcsARb1MPRMPAEmJeqVXq-LgTNoilsmUzOBF43ePivqzri6ZG6XTIziUkl9e5lSslh3ryWATTAAeuH1IQZ-_LMwR-UXyly_-vxdTgArf3SZgn8y81ryJFuYFkLHEpGXm48g2DOLR-wxQmxRngrxhkONk8aC5woUHNcS7e7KhmpS0giSNSe_QyLPwRme4u90HTqi-pTar-OO3kvLihR3mMR1Er8RSxqnUYP2-FlDbv-d7w19JLFofvqTMg614cha8OFKhM4RdWlv_EJBbhwALL-nvovrhoI6bh_1j5nA195w5Vngf4TflldDPOkEvyKgqGP6gnglUbeU7OwCN1vQqse7IhjedXuUaccO8TfaJ5N2wZBFwhcxsSVs-DKSBcLv0pjGI2CY2Jc0mS1bpWy7QbA2CtrD8CC304ev4R-njNOOD8dFLBqrVXcIyKKkXi7_FTxKwF0G0l6EnaqlG2YknhiN_GQD1ctlnbr1SyQnHQZcP1U8gmkIkEgKX6Nrx0rO5cb3hhmKmHwbLQi7fa1dS8q2tfdwqAIOX105w_GelzCYOtIBU18Ukpwi5LUi89lAbatL7Hw_fkynbz5y0).

## Schema

![ERD](../../images/module4/example_erd.png)

## Design overview

The schema is organized into three groups of tables:

| Group | Tables |
| --- | --- |
| Academic catalog | `departments`, `courses`, `course_prerequisites` |
| People | `employees`, `faculty_details`, `staff_details`, `students` |
| Scheduling & enrollment | `terms`, `course_sections`, `enrollments` |

**Why scheduling and enrollment tables, when only departments/students/faculty/course catalog
were asked for?** A course catalog by itself can't represent "students take courses" or "faculty
teach courses" in a way that holds up to normalization — a course can be taught by different
instructors each term, and a student's enrollment has its own lifecycle (enrolled, dropped,
graded). `terms` and `course_sections` split "the abstract course" (`courses`, e.g. *CS-600*) from
"a specific offering of it" (`course_sections`, e.g. *CS-600, Section 001, Fall 2026, taught by
Dr. X*), and `enrollments` is the join table between `students` and `course_sections`. This is how
real registrar systems are normalized, and it avoids modeling a course as only ever having one
instructor or one roster, ever.

**Why `employees` + `faculty_details` / `staff_details` instead of two flat tables?** Faculty and
staff share most attributes (name, email, department, hire date, status) and diverge on only a
few (academic rank/tenure vs. job title). Splitting the shared columns into `employees` and the
type-specific columns into a 1:1 "detail" table (keyed by the same id) avoids duplicating the
shared columns and avoids nullable columns that only make sense for one subtype. It's the standard
"class-table inheritance" pattern, and it composes with `employee_type` doing double duty: it
tells you which detail table to expect a row in, and `course_sections.instructor_id` references
`faculty_details.employee_id` directly (not `employees.id`), so the database itself enforces that
only faculty can be assigned as an instructor. That guarantee only holds if a `faculty_details` row
can't belong to a non-faculty employee, so each detail table carries its own `employee_type` column
and references `employees (id, employee_type)` as a composite foreign key — see the tables below.

## Conventions

These conventions apply to every table (they're omitted from the diagram itself for readability):

- **Primary keys**: `id UUID PRIMARY KEY DEFAULT gen_random_uuid()`, matching the existing
  `Course` entity's `GenerationType.UUID` strategy. `gen_random_uuid()` is built into Postgres 16
  (the version this project already runs via `docker-compose.yml`), so no extension is needed.
- **Audit columns**: every table except pure join tables (`course_prerequisites`) has
  `created_at TIMESTAMPTZ NOT NULL DEFAULT now()` and `updated_at TIMESTAMPTZ NOT NULL DEFAULT now()`.
- **Naming**: `snake_case` table and column names, matching the `@Column(name = "...")` convention
  already used in `Course.java`.
- **"Enum-like" columns** (`employee_type`, `status`, `academic_rank`, `tenure_status`) are native
  Postgres `ENUM` types (`CREATE TYPE ... AS ENUM`), so the database itself — not just the
  application — rejects values outside the set. Each type is backed by a Java `enum` in the entity,
  mapped with `@Enumerated(EnumType.STRING)` and `@JdbcTypeCode(SqlTypes.NAMED_ENUM)`; because the
  schema is generated by Hibernate (`ddl-auto=update`), the Java enum is the single definition of
  the allowed values and Hibernate emits the `CREATE TYPE` from it. `STRING` maps by constant name
  (`ORDINAL`, the JPA default, would break if the constants were reordered).
  Tradeoffs accepted with this choice:
  - It ties the schema to Postgres (`ENUM` types aren't portable to other databases).
  - Values can be added (`ALTER TYPE ... ADD VALUE`) or renamed (`ALTER TYPE ... RENAME VALUE`),
    but not removed without recreating the type. Add a value to an existing database with that
    `ALTER TYPE`, not by relying on `ddl-auto`.
  - Hibernate names the type after the Java enum's simple name, lowercased by Postgres
    (`EmployeeType` → `employeetype`), and that name can't be overridden.
  - Implemented so far: `EmployeeType` and `EmployeeStatus` on `Employee`, `AcademicRank` and
    `TenureStatus` on `FacultyDetails`, and `StudentStatus` on `Student`. The remaining enum-like
    column (`enrollments.status`) still shows `VARCHAR` + `CHECK` in the table below until its
    entity is built, and should follow this same convention.

## Tables

### `departments`

| Column | Type | Constraints |
| --- | --- | --- |
| `id` | `UUID` | PK |
| `code` | `VARCHAR(10)` | `NOT NULL`, `UNIQUE` — e.g. `CS` |
| `name` | `VARCHAR(150)` | `NOT NULL` |
| `description` | `TEXT` | |

### `courses`

| Column | Type | Constraints |
| --- | --- | --- |
| `id` | `UUID` | PK |
| `department_id` | `UUID` | `NOT NULL`, FK → `departments.id` |
| `course_code` | `VARCHAR(20)` | `NOT NULL`, `UNIQUE` — e.g. `CS-600` |
| `title` | `VARCHAR(200)` | `NOT NULL` |
| `description` | `TEXT` | |
| `credits` | `SMALLINT` | `NOT NULL`, `CHECK (credits > 0)` |

### `course_prerequisites`

Self-referencing many-to-many join table on `courses`.

| Column | Type | Constraints |
| --- | --- | --- |
| `course_id` | `UUID` | PK (composite), FK → `courses.id` |
| `prerequisite_course_id` | `UUID` | PK (composite), FK → `courses.id` |

`CHECK (course_id <> prerequisite_course_id)` — a course can't be its own prerequisite.

### `employees`

Shared attributes for both faculty and staff.

| Column | Type | Constraints |
| --- | --- | --- |
| `id` | `UUID` | PK |
| `employee_number` | `VARCHAR(20)` | `NOT NULL`, `UNIQUE` |
| `first_name` | `VARCHAR(100)` | `NOT NULL` |
| `last_name` | `VARCHAR(100)` | `NOT NULL` |
| `email` | `VARCHAR(255)` | `NOT NULL`, `UNIQUE` |
| `department_id` | `UUID` | `NOT NULL`, FK → `departments.id` |
| `employee_type` | `employeetype` (native `ENUM`) | `NOT NULL` — values `'FACULTY'`, `'STAFF'`; Java enum `EmployeeType` |
| `hire_date` | `DATE` | `NOT NULL` |
| `status` | `employeestatus` (native `ENUM`) | `NOT NULL DEFAULT 'ACTIVE'` — values `'ACTIVE'`, `'INACTIVE'`, `'TERMINATED'`; Java enum `EmployeeStatus` |

`UNIQUE (id, employee_type)` — redundant with the primary key on its own, but it gives
`faculty_details` and `staff_details` a key to reference so they can be tied to an employee of the
right type (see below).

Mapped by the `Employee` entity (`model/employees/`). `EmployeeType` and `EmployeeStatus` are the
Java enums behind the two native Postgres `ENUM` types above; adding a value means adding an enum
constant and running `ALTER TYPE ... ADD VALUE` on an existing database (see Conventions).

### `faculty_details`

At most one row per `employees` row where `employee_type = 'FACULTY'`.

| Column | Type | Constraints |
| --- | --- | --- |
| `employee_id` | `UUID` | PK, part of composite FK (`employee_id`, `employee_type`) → `employees (id, employee_type)` |
| `employee_type` | `employeetype` (native `ENUM`) | `NOT NULL DEFAULT 'FACULTY'`, `CHECK (employee_type = 'FACULTY')`; the other half of the composite FK |
| `academic_rank` | `academicrank` (native `ENUM`) | `NOT NULL` — values `'INSTRUCTOR'`, `'ASSISTANT_PROFESSOR'`, `'ASSOCIATE_PROFESSOR'`, `'PROFESSOR'`, `'ADJUNCT'`; Java enum `AcademicRank` |
| `tenure_status` | `tenurestatus` (native `ENUM`) | nullable — values `'TENURED'`, `'TENURE_TRACK'`, `'NON_TENURE'`; Java enum `TenureStatus` |
| `office_location` | `VARCHAR(100)` | |

Mapped by the `FacultyDetails` entity (`model/employees/faculty/`). The entity's primary key is its
`employee` association (`@OneToOne` + `@MapsId`), so the row's `employee_id` is always the employee's
own `id`. `employee_type` is pinned to `'FACULTY'` by its default and `CHECK`, so the composite
foreign key only matches employees of type `FACULTY`: Postgres rejects a row for a staff employee,
and rejects changing an employee's type while a faculty row exists. The entity maps the column only
to declare it (it is never written or read by JPA).

### `staff_details`

At most one row per `employees` row where `employee_type = 'STAFF'`.

| Column | Type | Constraints |
| --- | --- | --- |
| `employee_id` | `UUID` | PK, part of composite FK (`employee_id`, `employee_type`) → `employees (id, employee_type)` |
| `employee_type` | `employeetype` (native `ENUM`) | `NOT NULL DEFAULT 'STAFF'`, `CHECK (employee_type = 'STAFF')`; the other half of the composite FK |
| `job_title` | `VARCHAR(100)` | `NOT NULL` |
| `office_location` | `VARCHAR(100)` | |

Mapped by the `StaffDetails` entity (`model/employees/staff/`), keyed and constrained the same way
as `FacultyDetails` (`@OneToOne` + `@MapsId`, `employee_type` pinned to `'STAFF'`). Its only
enum-like column is that pinned `employee_type`.

**Not enforced by the schema:** an employee of type `FACULTY` or `STAFF` isn't required to *have* a
details row. Details are created after the employee, so requiring one would take a deferred
constraint or trigger; the application is expected to create them together.

### `students`

| Column | Type | Constraints |
| --- | --- | --- |
| `id` | `UUID` | PK |
| `student_number` | `VARCHAR(20)` | `NOT NULL`, `UNIQUE` |
| `first_name` | `VARCHAR(100)` | `NOT NULL` |
| `last_name` | `VARCHAR(100)` | `NOT NULL` |
| `email` | `VARCHAR(255)` | `NOT NULL`, `UNIQUE` |
| `date_of_birth` | `DATE` | |
| `enrollment_date` | `DATE` | `NOT NULL` |
| `department_id` | `UUID` | FK → `departments.id`, nullable (undeclared major) |
| `status` | `studentstatus` (native `ENUM`) | `NOT NULL DEFAULT 'ACTIVE'` — values `'ACTIVE'`, `'INACTIVE'`, `'GRADUATED'`, `'WITHDRAWN'`; Java enum `StudentStatus` |

Mapped by the `Student` entity (`model/students/`). `StudentStatus` is the Java enum behind the
native Postgres `ENUM` type above; adding a value means adding an enum constant and running
`ALTER TYPE ... ADD VALUE` on an existing database (see Conventions). `department_id` is the
student's declared major and is nullable, so a student can be undeclared.

### `terms`

| Column | Type | Constraints |
| --- | --- | --- |
| `id` | `UUID` | PK |
| `name` | `VARCHAR(50)` | `NOT NULL`, `UNIQUE` — e.g. `Fall 2026` |
| `start_date` | `DATE` | `NOT NULL` |
| `end_date` | `DATE` | `NOT NULL`, `CHECK (end_date > start_date)` |

### `course_sections`

A specific offering of a `course` within a `term`.

| Column | Type | Constraints |
| --- | --- | --- |
| `id` | `UUID` | PK |
| `course_id` | `UUID` | `NOT NULL`, FK → `courses.id` |
| `term_id` | `UUID` | `NOT NULL`, FK → `terms.id` |
| `section_number` | `VARCHAR(10)` | `NOT NULL` — e.g. `001` |
| `instructor_id` | `UUID` | FK → `faculty_details.employee_id`, nullable (TBD instructor) |
| `capacity` | `SMALLINT` | `NOT NULL`, `CHECK (capacity > 0)` |
| `location` | `VARCHAR(100)` | |
| `schedule` | `VARCHAR(100)` | e.g. `MWF 10:00-10:50` |

`UNIQUE (course_id, term_id, section_number)`.

### `enrollments`

Join table between `students` and `course_sections`.

| Column | Type | Constraints |
| --- | --- | --- |
| `id` | `UUID` | PK |
| `student_id` | `UUID` | `NOT NULL`, FK → `students.id` |
| `section_id` | `UUID` | `NOT NULL`, FK → `course_sections.id` |
| `enrollment_date` | `DATE` | `NOT NULL DEFAULT CURRENT_DATE` |
| `status` | `VARCHAR(20)` | `NOT NULL DEFAULT 'ENROLLED'`, `CHECK (... IN ('ENROLLED', 'DROPPED', 'COMPLETED', 'WITHDRAWN'))` |
| `grade` | `VARCHAR(5)` | nullable until the section completes |

`UNIQUE (student_id, section_id)` — a student enrolls in a given section at most once.
