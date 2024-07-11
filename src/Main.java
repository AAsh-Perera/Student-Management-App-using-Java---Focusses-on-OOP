import Menu.*;// import all classes from the menu package
import University.*;
import University.Module;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static final int MAX_NUMBER_OF_SEATS = 100;
    public static final int NUMBER_OF_MODULES = 3;
    // i will use an atomic integer to keep track of the student count.
    public static AtomicInteger enrolledStudentCount = new AtomicInteger(0);
    private static final String DATA_FILE_PATH = "StudentData/studentData.txt";
    public static Scanner scanner = new Scanner(System.in);
    // arrays for the students and modules
    public static Module[] allModules = new Module[NUMBER_OF_MODULES];
    public static Student[] allStudents = new Student[MAX_NUMBER_OF_SEATS];

    public static void main (String [] args) {

        // create and add the modules
        for (int i = 0; i < allModules.length; i++) {
            allModules[i] = new Module(String.format("Module 0%d", i + 1));
        }

        /*
                we will be using a simple Stack data structure to keep track of all the menus.
                this data type is a FILO type structure, so the last in item will be displayed first.
            */
        // create the menu stack
        Stack<Menu> menuStack = new Stack<>();

        // create the main menu
        Menu<Void> mainMenu = new Menu<Void>("Main Menu", scanner);

        // option 01 - check available seats
        mainMenu.addOptionToMenu(MenuMethods.checkAvailableSeats(MAX_NUMBER_OF_SEATS, enrolledStudentCount));

        // option 02 - register a student.
        mainMenu.addOptionToMenu(MenuMethods.registerStudent(scanner, MAX_NUMBER_OF_SEATS, enrolledStudentCount, allModules, allStudents));

        // option 03 - delete a student.
        mainMenu.addOptionToMenu(MenuMethods.deleteStudent(allStudents, enrolledStudentCount));

        // option 04 - find a student.
        mainMenu.addOptionToMenu(MenuMethods.findStudent(allStudents, enrolledStudentCount, scanner));

        // option 05 - write data to file
        mainMenu.addOptionToMenu(MenuMethods.writeDataToFile(allModules, allStudents, enrolledStudentCount, DATA_FILE_PATH));

        // option 06 - load data from file
        mainMenu.addOptionToMenu(MenuMethods.loadDataFromFile(allModules, allStudents, enrolledStudentCount, DATA_FILE_PATH));

        // option 07 - display all registered students.
        mainMenu.addOptionToMenu(MenuMethods.displayRegisteredStudents(allStudents, enrolledStudentCount));

        //create submenu
        Menu<Void> subMenu = new Menu<Void>("Sub-Menu", scanner);

        // option 08 - more options.
        mainMenu.addOptionToMenu(new MenuOption<Void>("More Options.", ()-> {
            //add the submenu to the top of the stack.
            menuStack.push(subMenu);
        }));

        // option 8.1 - edit or add student name
        subMenu.addOptionToMenu(MenuMethods.editStudentName(allStudents, enrolledStudentCount, scanner));

        // option 8.2 - set module marks
        subMenu.addOptionToMenu(MenuMethods.setModuleMarks(allStudents, enrolledStudentCount, scanner, allModules));

        // option 8.3 - get system summary
        subMenu.addOptionToMenu(MenuMethods.getSystemSummary(allModules, allStudents,enrolledStudentCount));

        // option 8.4 - generate a full report.
        subMenu.addOptionToMenu(MenuMethods.displayCompleteReport(allStudents, enrolledStudentCount));

        // option to exit submenu
        subMenu.addOptionToMenu( new MenuOption<Void>("Back To Main Menu.", () -> {
            // remove the submenu from the stack.
            menuStack.pop();
        }));

        // option 09 - exit program
        mainMenu.addOptionToMenu(MenuMethods.exitMenu(menuStack, allModules, allStudents, enrolledStudentCount, DATA_FILE_PATH));

        // add the main-menu to the stack
        menuStack.push(mainMenu);

        // the main loop
        while (!menuStack.empty()) {
            try {
                menuStack.peek().displayMenu();
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println(); // Add a blank line for readability
            }
        }
        // close the scanner
        scanner.close();
    }
}