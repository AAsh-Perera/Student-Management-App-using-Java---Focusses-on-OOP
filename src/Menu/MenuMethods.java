package Menu;

import University.Student;
import University.Module;
import java.io.*;
import java.util.Scanner;
import java.util.Stack;
import static Utilities.FileUtilities.isFileEmpty;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuMethods {

    private static Scanner menuMethodScanner = new Scanner(System.in);
    /*
        the methods that the main menu will use will be in this class.
        this is because i want to keep the Main.java class as small and consise as possible.
        this class will only have static methods
     */

    // method to select a student using all students.
    public static Student selectAStudent(Student[] allStudents, AtomicInteger enrolledStudentCount) {
        try {
            // Check if there are any students registered
            if (allStudents == null || enrolledStudentCount.get() == 0) {
                throw new IllegalStateException("No students are currently enrolled.");
            }

            // Create a menu of students
            Menu<Student> selectStudent = new Menu<Student>("Select a Student Menu.", menuMethodScanner, "To Select a Student, Please Enter the Number in front of the Student");

            // Add the students to the menu
            for (Student student : Student.getSortedStudentsByName(allStudents, enrolledStudentCount)) {
                String studentInfoString = String.format(". Name: %s %s%n   Student ID: %s", student.getFName(), student.getLName(), student.getStudentID());
                selectStudent.addOptionToMenu(new MenuOption<>(studentInfoString, () -> student));
            }

            // Return the selected student
            return selectStudent.displayMenu();

        }
        catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }

        return null;
    }

    // method to find or select a student
    public static Student selectOrFindAStudent (Student[] allStudents, AtomicInteger enrolledStudentCount) {
        Menu<Student> studentSelectorMenu = new Menu<Student>("Student Selector menu", menuMethodScanner, "Please select how you would like to find a student: ");
        studentSelectorMenu.addOptionToMenu(new MenuOption<>("Select a Student Using All Students List", () -> selectAStudent(allStudents, enrolledStudentCount)));
        studentSelectorMenu.addOptionToMenu(new MenuOption<Student>("Select a Student Using Student ID.", () -> Student.findStudent(allStudents, enrolledStudentCount, menuMethodScanner)));
        return studentSelectorMenu.displayMenu();
    }

    // 1. Method for checking available seats.
    public static MenuOption<Void> checkAvailableSeats(Integer MAX_NUMBER_OF_SEATS,
                                                       AtomicInteger enrolledStudentCount) {

        return new MenuOption<Void>("Check Available Seats",
                () -> System.out.printf("There are Currently %d Available%n", MAX_NUMBER_OF_SEATS - enrolledStudentCount.get()));
    }

    // 2. Method to register a student
    public static MenuOption<Void> registerStudent (Scanner scanner,
                                                    Integer MAX_NUMBER_OF_SEATS,
                                                    AtomicInteger enrolledStudentCount,
                                                    Module[] allModules,
                                                    Student[] allStudents) {
        return new MenuOption<Void>("Rejister a New Student.", () -> {
            try {
                // Ensure there are available seats before creating a new student
                if (enrolledStudentCount.get() >= MAX_NUMBER_OF_SEATS) {
                    throw new IllegalStateException("There are no open seats available for this semester.");
                }

                System.out.println("Enter the First Name of The Student: ");
                String fName = scanner.nextLine().trim();
                if (fName.isEmpty()) {
                    throw new IllegalArgumentException("First name cannot be empty.");
                }

                System.out.println("Enter the Last Name of The Student: ");
                String lName = scanner.nextLine().trim();
                if (lName.isEmpty()) {
                    throw new IllegalArgumentException("Last name cannot be empty.");
                }

                // Construct the student
                new Student(fName, lName, allModules, MAX_NUMBER_OF_SEATS, enrolledStudentCount, allStudents);
            }
            catch (IllegalArgumentException e) {
                System.out.println("Input Error: " + e.getMessage());
            }
            catch (IllegalStateException e) {
                System.out.println("Registration Error: " + e.getMessage());
            }
            catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // 3. Method to delete a student
    public static MenuOption<Void> deleteStudent (Student[] allStudents,
                                                  AtomicInteger enrolledStudentCount) {
        return new MenuOption<Void>("Delete a Registered Student.", () -> {
            if (enrolledStudentCount.get() > 0) {
                Student student = selectOrFindAStudent(allStudents, enrolledStudentCount);
                if (student == null) {
                    return;
                }
                student.deleteStudent(allStudents, enrolledStudentCount);
            }
            else {
                System.out.println("Error - Cannot delete Student: There are no Students to Delete.");
            }
        });
    }

    // 4. Method to find a student: using ID
    public static MenuOption<Void> findStudent (Student[] allStudents, AtomicInteger enrolledStudentCount, Scanner scanner) {
        return new MenuOption<Void>("Find a Registered Student.", () -> {
            Student selectedStudent = Student.findStudent(allStudents, enrolledStudentCount, scanner);
            if (selectedStudent != null) {
                selectedStudent.displayStudentSummary();
            }
        });
    }


    // 5. Method to write data to txt file
    public static MenuOption<Void> writeDataToFile (Module[] allModules,
                                                    Student[] allStudents,
                                                    AtomicInteger enrolledStudentCount,
                                                    String DATA_FILE_PATH) {
        return new MenuOption<Void>("Save Student and Module Data.", () -> {
            try {
                // the following code is directly referenced from SimpliLearn.com, available at: https://www.simplilearn.com/tutorials/java-tutorial/serialization-in-java#:~:text=Serialization%20in%20Java%20is%20the,then%20de%2Dserialize%20it%20there.

                FileOutputStream file = new FileOutputStream(DATA_FILE_PATH);
                ObjectOutputStream oos = new ObjectOutputStream(file);
                // save the modules
                oos.writeObject(allModules);
                // save the students
                oos.writeObject(allStudents);
                // save the student count
                oos.writeObject(enrolledStudentCount);
                System.out.println("Program data saved successfully.");
            }
            catch (IOException e) {
                System.out.println("Error saving program data: " + e.getMessage());
            }
            catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        });
    }

    // 6. Method to load data from file
    public static MenuOption<Void> loadDataFromFile (Module[] allModules,
                                                     Student[] allStudents,
                                                     AtomicInteger enrolledStudentCount,
                                                     String DATA_FILE_PATH) {
        return new MenuOption<Void>("Load Student and Module Data from File.", () -> {

            // the following code is directly referenced from SimpliLearn.com, available at: https://www.simplilearn.com/tutorials/java-tutorial/serialization-in-java#:~:text=Serialization%20in%20Java%20is%20the,then%20de%2Dserialize%20it%20there.

            File file = new File(DATA_FILE_PATH);

            // check if the file is empty.
            if (isFileEmpty(file)) {
                System.out.println("Error - Cannot Load Data: File is Empty.");
                return;
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {

                // the order is important, the test done for this failed because of this order being wrong.
                Module[] modulesTemp = (Module[]) ois.readObject();
                Student[] studentsTemp = (Student[]) ois.readObject();
                AtomicInteger tempInt = (AtomicInteger) ois.readObject();


                // Copy loaded data to the provided arrays
                System.arraycopy(modulesTemp, 0, allModules, 0, modulesTemp.length);
                System.arraycopy(studentsTemp, 0, allStudents, 0, studentsTemp.length);
                // restore the student count.
                enrolledStudentCount.set(tempInt.get());

                System.out.println("Program data loaded successfully.");
            }
            catch (FileNotFoundException e) {
                System.out.println("Error - Cannot Load Data: The file was not found. Please make sure the file path is correct");
            }
            catch (IOException e) {
                System.out.println("Error loading program data: " + e.getMessage());
            }
            catch (ClassNotFoundException e) {
                System.out.println("Error - Cannot Load Data: Class not found while loading program data");
            }
            catch (ClassCastException e) {
                System.out.println("Error - Cannot Load Data: Data in the file is corrupted or does not match expected types");
                e.printStackTrace();
            }
        });
    }

    // 7. Method to view the list of students based on their names
    public static MenuOption<Void> displayRegisteredStudents (Student[] allStudents,
                                                              AtomicInteger enrolledStudentCount) {
        return new MenuOption<Void>("Display All Registered Students", new Runnable() {
            @Override
            public void run() {
                Student.displaySortedStudent(allStudents, enrolledStudentCount);
            }
        });
    }

    // a. Method to add or edit student name
    public static MenuOption<Void> editStudentName (Student[] allStudents, AtomicInteger enrolledStudentCount, Scanner scanner) {
        return new MenuOption<Void>("Edit or Add a Student's Name.", () -> {

            if (allStudents == null || enrolledStudentCount.get() == 0) {
                System.out.println("Error - Cannot choose this option: There are no Students Registered to edit.");
                return;
            }

            try {
                // Inform the user to select a student
                System.out.println("To edit or add a name, select a student");

                // Select or find a student
                Student selectedStudent = selectOrFindAStudent(allStudents, enrolledStudentCount);
                if (selectedStudent == null) {
                    System.out.println("Error: No student was selected or found.");
                    return;
                }

                // Get the first name
                System.out.println("Enter the First Name of the Student: ");
                String fName = scanner.nextLine().trim();
                if (fName.isEmpty()) {
                    throw new IllegalArgumentException("First name cannot be empty.");
                }
                selectedStudent.setFName(fName);

                // Get the last name
                System.out.println("Enter the Last Name of the Student: ");
                String lName = scanner.nextLine().trim();
                if (lName.isEmpty()) {
                    throw new IllegalArgumentException("Last name cannot be empty.");
                }
                selectedStudent.setLName(lName);

                // Display updated student information
                System.out.println("Updated Student Information");
                selectedStudent.displayStudentInfo();

            }
            catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
            catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        });
    }

    // b. Method to set module marks for all modules.
    public static MenuOption<Void> setModuleMarks (Student[] allStudents,
                                                   AtomicInteger enrolledStudentCount,
                                                   Scanner scanner,
                                                   Module[] allModules) {
        return new MenuOption<Void>("Set Module Marks for Modules 1 - 3.", () -> {

            // check if there are any students
            if (allStudents == null || enrolledStudentCount.get() == 0) {
                System.out.println("Error - Cannot choose this option: There are no Students Registered to edit.");
                return;
            }

            // select the student.
            System.out.println("Select a Student to Set Their Module Marks.");
            Student selectedStudent = selectOrFindAStudent(allStudents, enrolledStudentCount);
            if (selectedStudent == null) {
                return;
            }

            // get the module
            for (int i = 1; i <= allModules.length; i ++) {
                Module selectedModule = selectAModule(allModules, scanner);

                // set the marks
                System.out.printf("Enter the marks for module '%s': %n", selectedModule.name());
                Float marks = scanner.nextFloat();
                scanner.nextLine();
                while (marks < 0 || marks > 100) {
                    System.out.println("Error: Marks Must be between 0 - 100.");
                    System.out.printf("Enter the marks for module '%s': %n", selectedModule.name());
                    marks = scanner.nextFloat();
                    scanner.nextLine();
                }
                // exception handling is done by the setModuleMarks method itself.
                selectedStudent.setModuleMarks(selectedModule, marks);
            }
        });
    }
    // method to select a module.
    public static Module selectAModule (Module[] allModules, Scanner scanner) {
        Menu<Module> moduleSelectorMenu = new Menu<Module>("Module Selector Menu", scanner, "Please Select a Module to Set Marks For");
        // add the modules as options.
        for (Module module: allModules) {
            moduleSelectorMenu.addOptionToMenu(new MenuOption<Module>(module.name(), () -> module));
        }
        return moduleSelectorMenu.displayMenu();
    }

    // c. Generate Summary of Student and module data
    public static MenuOption<Void> getSystemSummary (Module[] allModules, Student[] allStudents, AtomicInteger enrolledStudentCount) {
        return new MenuOption<Void>("Generate Summary of Students and Modules.", ()-> {

            if (allStudents == null || enrolledStudentCount.get() == 0) {
                System.out.println("There are 0 Students Registered");
                System.out.println("To get module data Register at least one student");
                return;
            }

            int totalStudents = enrolledStudentCount.get();
            System.out.printf("Total Number of Students Registered: %d%n", totalStudents);
            System.out.println("===== Module Summary =====");
            for (Module module: allModules) {
                System.out.printf("Module Name: %s%n Number of Students Who Passes: %d%n", module.name(), module.getStudentPassedCount(allStudents));
            }
        });
    }

    // d. Generate a complete Student report with list of students
    public static MenuOption<Void> displayCompleteReport (Student[] allStudents, AtomicInteger enrolledStudentCount) {
        return new MenuOption<Void>("Display Complete Report of All Students", () -> {
            Student.displayCompleteStudentReport(allStudents, enrolledStudentCount);
        });
    }

    // 9. Method to safely exist program
    public static MenuOption<Void> exitMenu (Stack<Menu> menuStack,
                                             Module[] allModules,
                                             Student[] allStudents,
                                             AtomicInteger enrolledStudentCount,
                                             String DATA_FILE_PATH) {
        return new MenuOption<Void>("Save and Exit Program.", new Runnable() {
            @Override
            public void run() {
                MenuOption<Void> temp = writeDataToFile(allModules, allStudents, enrolledStudentCount, DATA_FILE_PATH);
                try {
                    temp.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //close the local scanner
                menuMethodScanner.close();
                // remove the menu from the stack
                menuStack.pop();
            }
        });
    }
}