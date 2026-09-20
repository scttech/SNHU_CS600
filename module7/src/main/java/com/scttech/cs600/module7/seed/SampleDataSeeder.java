package com.scttech.cs600.module7.seed;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.scttech.cs600.module7.model.course.Course;
import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.model.employees.Employee;
import com.scttech.cs600.module7.model.employees.EmployeeType;
import com.scttech.cs600.module7.model.employees.faculty.AcademicRank;
import com.scttech.cs600.module7.model.employees.faculty.TenureStatus;
import com.scttech.cs600.module7.repository.course.CourseRepository;
import com.scttech.cs600.module7.repository.department.DepartmentRepository;
import com.scttech.cs600.module7.repository.employees.EmployeeRepository;
import com.scttech.cs600.module7.service.course.CourseService;
import com.scttech.cs600.module7.service.employees.EmployeeService;

/**
 * Fills an empty catalog with sample data so the app is worth clicking through right after
 * {@code docker compose up -d}, without entering everything by hand. Each table is seeded only
 * while it is empty, so restarting the app never duplicates rows and never brings back rows
 * deleted through the UI; to start over, {@code docker compose down -v} drops the Postgres volume.
 *
 * <p>This is an {@link ApplicationRunner} rather than a {@code data.sql} script because Hibernate
 * ({@code ddl-auto=update}) creates the tables, so the rows can't be loaded until after it has
 * run, and because the {@code @DataJpaTest} slices never load {@code @Component}s, so their empty
 * tables stay empty. Add a {@code seedXxx} method here as each new table is added.
 */
@Component
public class SampleDataSeeder implements ApplicationRunner {

    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    public SampleDataSeeder(DepartmentRepository departmentRepository, CourseRepository courseRepository,
            CourseService courseService, EmployeeRepository employeeRepository,
            EmployeeService employeeService) {
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.courseService = courseService;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    @Override
    public void run(ApplicationArguments args) {
        Map<String, Department> departments = seedDepartments();
        seedCourses(departments);
        seedEmployees(departments);
    }

    private Map<String, Department> seedDepartments() {
        Map<String, Department> byCode = new HashMap<>();
        if (departmentRepository.count() == 0) {
            departmentRepository.save(new Department("CS", "Computer Science",
                    "Programming, software design, and computing systems"));
            departmentRepository.save(new Department("MATH", "Mathematics",
                    "Calculus, discrete mathematics, and statistics"));
            departmentRepository.save(new Department("ENG", "English",
                    "Composition and technical writing"));
        }
        departmentRepository.findAll().forEach(department -> byCode.put(department.getCode(), department));
        return byCode;
    }

    private void seedCourses(Map<String, Department> departments) {
        if (courseRepository.count() > 0) {
            return;
        }
        // Saved in prerequisite order: a course's prerequisites must already exist when it is saved.
        Course cs101 = saveCourse("CS-101", "Introduction to Programming", 3, departments.get("CS"), Set.of());
        Course cs200 = saveCourse("CS-200", "Data Structures", 3, departments.get("CS"), Set.of(cs101));
        Course math150 = saveCourse("MATH-150", "Discrete Mathematics", 3, departments.get("MATH"), Set.of());
        Course cs300 = saveCourse("CS-300", "Algorithms", 3, departments.get("CS"), Set.of(cs200, math150));
        saveCourse("CS-600", "Software Design and Development", 3, departments.get("CS"), Set.of(cs300));
        saveCourse("ENG-101", "College Composition", 3, departments.get("ENG"), Set.of());
    }

    private Course saveCourse(String courseCode, String title, int credits, Department department,
            Set<Course> prerequisites) {
        return courseService.save(new Course(courseCode, title, credits, department), prerequisites);
    }

    private void seedEmployees(Map<String, Department> departments) {
        if (employeeRepository.count() > 0) {
            return;
        }
        Employee alvarez = employee("F-1001", "Maria", "Alvarez", EmployeeType.FACULTY, departments.get("CS"), 2009);
        employeeService.saveFaculty(alvarez, AcademicRank.PROFESSOR, TenureStatus.TENURED, "Tech Hall 310");
        Employee okafor = employee("F-1002", "James", "Okafor", EmployeeType.FACULTY, departments.get("MATH"), 2019);
        employeeService.saveFaculty(okafor, AcademicRank.ASSISTANT_PROFESSOR, TenureStatus.TENURE_TRACK, "Science 204");
        Employee raman = employee("F-1003", "Priya", "Raman", EmployeeType.FACULTY, departments.get("CS"), 2023);
        employeeService.saveFaculty(raman, AcademicRank.ADJUNCT, TenureStatus.NON_TENURE, null);

        Employee kim = employee("S-2001", "Daniel", "Kim", EmployeeType.STAFF, departments.get("CS"), 2016);
        employeeService.saveStaff(kim, "Department Administrator", "Tech Hall 101");
        Employee bennett = employee("S-2002", "Laura", "Bennett", EmployeeType.STAFF, departments.get("ENG"), 2021);
        employeeService.saveStaff(bennett, "Program Coordinator", null);
    }

    private Employee employee(String number, String firstName, String lastName, EmployeeType type,
            Department department, int hireYear) {
        return new Employee(number, firstName, lastName, (firstName + "." + lastName).toLowerCase() + "@example.edu",
                department, type, LocalDate.of(hireYear, 8, 15));
    }
}
