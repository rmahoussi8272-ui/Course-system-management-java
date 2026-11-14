import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class StudentCourseManagementSystem {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final String FILE_NAME = "courses.txt";
    private final ArrayList<Course> courses = new ArrayList<Course>();

    public static void main(String[] args) {
        new StudentCourseManagementSystem().run();
    }

    private void run() {
        // Initial courses supplied for this project.
        courses.add(new Course("MTH201", "Mathematics II", 2,
                "Computer Science"));
        courses.add(new Course("COS201", "Computer Programming I", 3,
                "Computer Science"));

        System.out.println("==============================================");
        System.out.println("        STUDENT COURSE MANAGEMENT SYSTEM");
        System.out.println("==============================================");

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = readInteger("Enter your choice: ");

            switch (choice) {
                case 1: addCourse(); break;
                case 2: displayCourses(); break;
                case 3: searchCourse(); break;
                case 4: displayTotalUnits(); break;
                case 5: saveCourses(); break;
                case 6: loadCourses(); break;
                case 7:
                    running = false;
                    System.out.println("Thank you for using the system.");
                    break;
                default:
                    System.out.println("Invalid choice. Enter a number from 1 to 7.");
            }
        }
        INPUT.close();
    }

    private void displayMenu() {
        System.out.println("\\n--------------- MAIN MENU ----------------");
        System.out.println("1. Add Course");
        System.out.println("2. View All Courses");
        System.out.println("3. Search Course by Code");
        System.out.println("4. Compute Total Units");
        System.out.println("5. Save Courses to File");
        System.out.println("6. Load Courses from File");
        System.out.println("7. Exit Program");
        System.out.println("-------------------------------------------");
    }

    private void addCourse() {
        System.out.println("\\nADD NEW COURSE");
        String code = readText("Enter course code: ").toUpperCase();

        if (findCourseRecursive(code, 0) != null) {
            System.out.println("A course with that code already exists.");
            return;
        }

        String title = readText("Enter course title: ");
        int unit = readPositiveInteger("Enter course unit: ");
        String department = readText("Enter course department: ");
        courses.add(new Course(code, title, unit, department));
        System.out.println("Course added successfully.");
    }

    private void displayCourses() {
        System.out.println("\\nREGISTERED COURSES");
        if (courses.isEmpty()) {
            System.out.println("No courses have been recorded.");
            return;
        }

        System.out.printf("%-12s %-32s %-8s %s%n",
                "COURSE CODE", "COURSE TITLE", "UNIT", "DEPARTMENT");
        System.out.println("--------------------------------------------------------------------------");
        for (Course course : courses) {
            course.display();
        }
    }

    private void searchCourse() {
        System.out.println("\\nSEARCH COURSE");
        String code = readText("Enter course code: ").toUpperCase();
        Course result = findCourseRecursive(code, 0);

        if (result == null) {
            System.out.println("Course not found.");
        } else {
            System.out.println("\\nCourse found:");
            System.out.printf("%-12s %-32s %-8s %s%n",
                    "COURSE CODE", "COURSE TITLE", "UNIT", "DEPARTMENT");
            System.out.println("--------------------------------------------------------------------------");
            result.display();
        }
    }

    // Recursive search: checks one course at a time until a match is found
    // or the end of the ArrayList is reached.
    private Course findCourseRecursive(String code, int index) {
        if (index >= courses.size()) {
            return null;
        }
        Course current = courses.get(index);
        if (current.getCourseCode().equalsIgnoreCase(code)) {
            return current;
        }
        return findCourseRecursive(code, index + 1);
    }

    private void displayTotalUnits() {
        int total = 0;
        for (Course course : courses) {
            total += course.getCourseUnit();
        }
        System.out.println("Total course units: " + total);
    }

    private void saveCourses() {
        Path file = Paths.get(FILE_NAME);
        try (BufferedWriter writer = Files.newBufferedWriter(
                file, StandardCharsets.UTF_8)) {
            for (Course course : courses) {
                writer.write(course.toFileRecord());
                writer.newLine();
            }
            System.out.println("Courses saved successfully in " + FILE_NAME + ".");
        } catch (IOException exception) {
            System.out.println("Error saving courses: " + exception.getMessage());
        }
    }

    private void loadCourses() {
        Path file = Paths.get(FILE_NAME);
        if (!Files.exists(file)) {
            System.out.println("The file " + FILE_NAME
                    + " does not exist. Save courses first.");
            return;
        }

        int addedCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(
                file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\t", -1);
                if (fields.length != 4) {
                    System.out.println("Skipping an invalid record in the file.");
                    continue;
                }

                try {
                    String code = fields[0].trim().toUpperCase();
                    String title = fields[1].trim();
                    int unit = Integer.parseInt(fields[2].trim());
                    String department = fields[3].trim();

                    if (code.isEmpty() || title.isEmpty()
                            || department.isEmpty() || unit <= 0) {
                        System.out.println("Skipping an incomplete course record.");
                        continue;
                    }

                    if (findCourseRecursive(code, 0) == null) {
                        courses.add(new Course(code, title, unit, department));
                        addedCount++;
                    }
                } catch (NumberFormatException exception) {
                    System.out.println("Skipping a record with an invalid unit.");
                }
            }
            System.out.println("Loading completed. " + addedCount
                    + " new course(s) added.");
        } catch (IOException exception) {
            System.out.println("Error loading courses: " + exception.getMessage());
        }
    }

    private int readInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = INPUT.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    private int readPositiveInteger(String prompt) {
        while (true) {
            int value = readInteger(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("The course unit must be greater than zero.");
        }
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = INPUT.nextLine().trim();
            if (!value.isEmpty() && !value.contains("\\t")) {
                return value;
            }
            System.out.println("This field cannot be empty or contain a tab.");
        }
    }
}

class Course {
    private final String courseCode;
    private final String courseTitle;
    private final int courseUnit;
    private final String courseDepartment;

    public Course(String courseCode, String courseTitle,
                  int courseUnit, String courseDepartment) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.courseUnit = courseUnit;
        this.courseDepartment = courseDepartment;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getCourseUnit() { return courseUnit; }
    public String getCourseDepartment() { return courseDepartment; }

    public void display() {
        System.out.printf("%-12s %-32s %-8d %s%n",
                courseCode, courseTitle, courseUnit, courseDepartment);
    }

    public String toFileRecord() {
        return courseCode + "\\t" + courseTitle + "\\t"
                + courseUnit + "\\t" + courseDepartment;
    }
}
