# Overview

This repo builds out functionality from [Module 6: Feature 2](../module6_feature2/README.md)
## Faculty & Staff

The Dashboard's **Faculty & Staff** tile opens [`EmployeeView`](../../module7/src/main/java/com/scttech/cs600/module7/ui/EmployeeView.java),
a single screen for adding, editing, and removing the two kinds of `employees` row
(see the [Module 4 schema](../module4/README.md#employees)).

- **Add faculty / Add staff** start the form for that type. The type is fixed once the employee is
  saved, because the database ties each details table to employees of one type.
- The form shows the shared employee fields (number, name, email, department, hire date, status)
  plus the type's own: academic rank (required) and tenure status for faculty, job title (required)
  for staff, and an office location for both. Ranks, tenure, and status are the native Postgres
  enums from Module 4, offered as drop-downs.
- An employee and their faculty or staff details are written in one transaction by
  [`EmployeeService`](../../module7/src/main/java/com/scttech/cs600/module7/service/employees/EmployeeService.java),
  and deleted together. An employee number or email already used by someone else is rejected with a
  message, and the form stays open so it can be corrected.
- A few sample faculty and staff are seeded on first startup, like the courses and departments.

## Students

The Dashboard's **Students** tile opens [`StudentView`](../../module7/src/main/java/com/scttech/cs600/module7/ui/StudentView.java),
a single screen for adding, editing, and removing rows of the `students` table
(see the [Module 4 schema](../module4/README.md#students)).

- The form has the student's number, name, email, date of birth (optional, must be in the past),
  enrollment date (defaults to today), major, and status. The status is the native Postgres enum
  from Module 4, offered as a drop-down: Active, Inactive, Graduated, or Withdrawn.
- The major is optional and can be cleared; a student without one shows as **Undeclared**.
- A student number or email already used by someone else is rejected with a message by
  [`StudentService`](../../module7/src/main/java/com/scttech/cs600/module7/service/students/StudentService.java),
  and the form stays open so it can be corrected.
- A few sample students are seeded on first startup, including one who hasn't declared a major.
