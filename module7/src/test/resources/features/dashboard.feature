Feature: Registrar dashboard as the post-login landing page

  As a registrar staff member, I want to land on a dashboard after I sign in, with a way to
  get to the Course Catalog, so that I have one home to work from as more tools are added to
  this system, instead of being dropped into one hardcoded screen.

  Background:
    Given a user "student" with password "changeit" exists

  Scenario: Signing in lands on the Dashboard
    When I sign in as "student" with password "changeit"
    Then I am redirected to the Dashboard

  Scenario: The Course Catalog is reachable as its own screen once signed in
    When I sign in as "student" with password "changeit"
    Then I can request the Course Catalog without being redirected

  Scenario: The Course Catalog is protected the same as the Dashboard
    When I request the Course Catalog without signing in
    Then I am redirected to the login screen
