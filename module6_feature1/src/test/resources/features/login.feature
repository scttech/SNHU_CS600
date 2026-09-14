Feature: Login screen for the Courses catalog

  As a registrar staff member, I want to sign in with my own username and password before I
  can see or change the course catalog, so that access to the system is tied to me
  individually, not a credential everyone shares.

  These scenarios exercise the acceptance criteria from docs/module6_feature1/README.md
  directly against the running app's HTTP endpoints (real Spring Security filter chains, a
  real Testcontainers Postgres `users` table) rather than through a browser.

  Background:
    Given a user "student" with password "changeit" exists

  Scenario: An unauthenticated visitor is sent to the login screen
    When I request the Courses view without signing in
    Then I am redirected to the login screen

  Scenario: Signing in with valid credentials reaches the Courses view
    When I sign in as "student" with password "changeit"
    Then my sign-in succeeds
    And I can request the Courses view without being redirected

  Scenario: Signing in with an invalid password stays on the login screen
    When I sign in as "student" with password "the-wrong-password"
    Then I am sent back to the login screen with an error

  Scenario: A successful sign-in updates the audit trail
    When I sign in as "student" with password "changeit"
    Then "student"'s last sign-in time is recorded

  Scenario: The REST API is guarded by the same users table as the login screen
    When I call the courses API without credentials
    Then the API responds with status 401
    When I call the courses API as "student" with password "changeit"
    Then the API responds with status 200
    When I call the courses API as "student" with password "the-wrong-password"
    Then the API responds with status 401
