package University;

import Utilities.UserIDGenerator;

import java.io.Serial;
import java.io.Serializable; // this will help me serialize the objects to a txt file using byte code
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Student implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String fName;
    private String lName;
    private final String studentID;
    // and i will use a map, which just like a dictionary in python, to manage the marks
    private Map<Module, Float> moduleMarks = new HashMap<>();
    private Map<Module, String> moduleGrades = new HashMap<>();
    // and for the grades i will use an enum
    public enum Grades {
        Distinction, Merit, Pass, Fail
    }

    // i will use atomic integers to keep track of the student count
    public Student (String fName,
                    String lName,
                    Module[] allModules,
                    Integer MaxNumOfStudents,
                    AtomicInteger enrolledStudentCount,
                    Student[] enrolledStudents) {

        this.fName = fName;
        this.lName = lName;
        this.studentID = generateStudentID();

        // add to the list of students' array
        if (enrolledStudentCount.get() < MaxNumOfStudents) {
            enrolledStudents[enrolledStudentCount.get()] = this;
            enrolledStudentCount.incrementAndGet(); // this should increment the integer in the main.java class
            // add to the modules.
            this.addToModules(allModules);
            this.displayStudentInfo();
            System.out.println("Student was Successfully Enrolled");
        } else {
            System.out.println("Error - Cannot register Student: There are no open seats available for this semester");
        }
    }

    // setter methods
    public void setFName(String fName) {
        this.fName = fName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    // getter methods

    // get student's first name => string
    public String getFName() {
        return fName;
    }

    // get student last name => string
    public String getLName() {
        return lName;
    }

    // get full name
    public String getFullName() {
        return fName + " " + lName;
    }

    // get student id => string
    public String getStudentID() {
        return studentID;
    }

    // get the map of marks as a map. returns a clone
    public Map<Module, Float> getModuleMarksMap() {
        return new HashMap<>(moduleMarks); // returns a clone of the map to protect encapsulation.
    }

    // get the map of grades as a map, returns a clone
    public Map<Module, String> getModuleGradesMap() {
        return new HashMap<>(moduleGrades);
    }

    // get module marks
    public float getModuleMark (Module module) {
        return moduleMarks.get(module);
    }

    // get module grade
    public String getModuleGrade (Module module) {
        return moduleGrades.get(module);
    }

    // method to generate a student ID
    private String generateStudentID () {
        // creates a pseudo random and unique ID.
        UserIDGenerator idGenerator = new UserIDGenerator();
        return idGenerator.generateUniqueID(8);
    }

    // enroll the student to all modules
    public void addToModules (Module[] allModules) {
        for (Module module: allModules) {
            // add the modules as keys to the maps.
            moduleMarks.put(module, 0.0f);
            moduleGrades.put(module, null);
        }
    }

    // calculate grade
    private String calculateGrade (float marks) {
        if (marks >= 80) {
            return Grades.Distinction.toString();
        } else if (marks >= 70) {
            return Grades.Merit.toString();
        } else if (marks >= 40) {
            return Grades.Pass.toString();
        } else {
            return Grades.Fail.toString();
        }
    }

    // Method to set Student marks and grade
    public void setModuleMarks (Module module, float marks) {
        if (marks > 0 && marks <= 100 && moduleMarks.containsKey(module)) {
            // add the marks
            moduleMarks.put(module, marks);
            // add the grades
            moduleGrades.put(module, calculateGrade(marks));
            System.out.printf("Student grade: %s%n", calculateGrade(marks));
            System.out.println("Student Marks were Successfully recorded.");
        }
        else {
            System.out.println("Error - Cannot enter marks: Marks Must be between 0 - 100.");
        }
    }

    // method to calculate total marks
    public float getTotalMarks () {
        Float totalMarks = 0.0f;
        for (Float marks: moduleMarks.values()) {
            totalMarks += marks;
        }
        return totalMarks;
    }

    // get the average marks
    public float getAverageMarks () {
        // lets avoid the zero division exception.
        return !moduleMarks.isEmpty() ? getTotalMarks() / moduleMarks.size(): 0.0f;
    }

    // method to delete students
    public void deleteStudent(Student[] allStudents,
                              AtomicInteger enrolledStudentCount) {

        // Remove from allStudents array
        for (int i = 0; i < enrolledStudentCount.get(); i++) {
            if (allStudents[i].equals(this)) {
                // i'll shift remaining elements
                for (int j = i; j < enrolledStudentCount.get() - 1; j++) {
                    allStudents[j] = allStudents[j + 1];
                }
                // remove the copy of the last student from the list.
                allStudents[enrolledStudentCount.get() - 1] = null;
                enrolledStudentCount.decrementAndGet();
                break;
            }
        }
        System.out.printf("Student with ID '%s' Was Successfully deleted%n", this.getStudentID());
    }

    // static method to find a student using ID
    public static Student findStudent(Student[] allStudents,
                                      AtomicInteger enrolledStudentCount,
                                      Scanner scanner) {

        try {
            // Check if there are any students registered
            if (allStudents == null || enrolledStudentCount.get() == 0) {
                System.out.println("Error - Cannot Find Student: The student list is empty or null.");
                return null;
            }

            // Get the ID
            System.out.print("Enter the Student ID to find the Student: ");
            String enteredID = scanner.nextLine().trim();

            for (Student student : allStudents) {
                if (student != null && student.getStudentID().equalsIgnoreCase(enteredID)) {
                    System.out.println("Selected Student Details");
                    student.displayStudentInfo();
                    return student;
                }
            }

            // The student was not found, so return null
            System.out.println("Error - Unable to find Student: There was No student Matching the Provided Student ID in the Database.");
            return null;
        }
        catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // sorting methods - uses bubble sort

    // method to sort the students based on names
    public static List<Student> getSortedStudentsByName(Student[] allStudents,
                                                        AtomicInteger enrolledStudentCount) {
        /*
            using a bubble sort algorithm is highly inefficient because of its time complexity.
            no real-world application would use this sorting algorithm
         */

        // check if there are any students registered
        if (allStudents == null || enrolledStudentCount.get() == 0) {
            System.out.println("Error: The student list is empty or null.");
            return Collections.emptyList(); // ill return an empty list if the list is null
        }

        // Create a local copy and filter out nulls
        List<Student> students = new ArrayList<>();
        for (Student student : allStudents) {
            if (student != null) {
                students.add(student);
            }
        }

        // Bubble sort based on names
        int size = students.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                String student01Name = students.get(j).getFullName();
                String student02Name = students.get(j + 1).getFullName();

                // Compare and swap if necessary
                if (student01Name.compareTo(student02Name) > 0) {
                    Student temp = students.get(j);
                    students.set(j, students.get(j + 1));
                    students.set(j + 1, temp);
                }
            }
        }

        return students;
    }

    // method to sort students based on their average marks highest to lowest
    public static List<Student> getSortedStudentsByAverage(Student[] allStudents,
                                                           AtomicInteger enrolledStudentCount) {
        // pretty much the same as the above sorting method

        if (allStudents == null || enrolledStudentCount.get() == 0) {
            System.out.println("Error: The student list is empty or null.");
            return null;
        }
        // Create a local copy and filter out nulls
        List<Student> students = new ArrayList<>();
        for (Student student : allStudents) {
            if (student != null) {
                students.add(student);
            }
        }

        // Bubble sort based on average marks
        int size = students.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (students.get(j).getAverageMarks() < students.get(j + 1).getAverageMarks()) {
                    // Swap the two
                    Student temp = students.get(j);
                    students.set(j, students.get(j + 1));
                    students.set(j + 1, temp);
                }
            }
        }

        return students;
    }

    // display methods

    // method to display student data.
    public void displayStudentInfo () {
        System.out.println();
        System.out.println("===== Student Information =====");
        System.out.printf("Student Name: %s %s%n", this.fName, this.lName);
        System.out.printf("Student ID: %s%n", this.getStudentID());
    }

    // display student info with marks and grade.
    public void displayStudentSummary () {
        this.displayStudentInfo();
        System.out.println("----- Module Results -----");

        // let's iterate over the entries in the module marks map
        // i will get each entry from the map as an array and then iterate over it.
        for (Map.Entry<Module, Float> moduleEntry: moduleMarks.entrySet()) {
            System.out.printf("Module: %s%n", moduleEntry.getKey().name());
            System.out.printf("Marks: %.2f%n", moduleEntry.getValue());
            System.out.printf("Grade: %s%n", calculateGrade(moduleEntry.getValue()));
            System.out.println();
        }

        //print the summary
        System.out.println("----- Summary -----");
        System.out.printf("Total Marks: %.2f%n", this.getTotalMarks());
        System.out.printf("Average Marks: %.2f%n", this.getAverageMarks());
        System.out.printf("Overall Grade: %s%n", calculateGrade(this.getAverageMarks()));
        System.out.println("---------------- **** ----------------");
        System.out.println();
    }

    // method to display the sorted students by name
    public static void displaySortedStudent(Student[] allStudents,
                                            AtomicInteger enrolledStudentCount) {
        if (allStudents == null || enrolledStudentCount.get() == 0) {
            System.out.println("Error - Cannot Display Students: The student list is empty or null.");
            return;
        }

        List<Student> sortedStudents = getSortedStudentsByName(allStudents, enrolledStudentCount);
        for (Student student: sortedStudents) {
            student.displayStudentInfo();
        }
    }

    // display the full report, sorted by average highest to lowest.
    public static void displayCompleteStudentReport(Student[] allStudents,
                                             AtomicInteger enrolledStudentCount) {
        if (allStudents == null || enrolledStudentCount.get() == 0) {
            System.out.println("No students to display.");
            return;
        }
        // sort the students by average
        List<Student> sortedStudents = getSortedStudentsByAverage(allStudents, enrolledStudentCount);

        System.out.println("====== Complete Report of All Students ======");
        System.out.println();

        // print their details.
        for (Student student : sortedStudents) {
            student.displayStudentSummary();
        }
    }
}