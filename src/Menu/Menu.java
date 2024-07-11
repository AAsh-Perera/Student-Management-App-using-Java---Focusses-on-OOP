package Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings({"unused", "CallToPrintStackTrace"})

public class Menu<T> {

    /*
        this is the class for Menus. it uses an Array (list) called options to display menu options.
        this ensures this falls under the assignment brief task one.

        to use this class, create a Menu object, then once MenuObject objects are created,
        add them to the Menu object using the addOptionToMenu() method.

        to display and run the option chosen by the user, call the displayMenu() method,
        this method handles getting user input, validating it and executing the MenuOption.
    */

    //All fields will be private to follow the principle of encapsulation.
    @SuppressWarnings("FieldMayBeFinal")
    private List<MenuOption<T>> options = new ArrayList<>();
    private int numberOfOptions;
    private final String name;
    private String prompt = null;
    private Scanner scanner;

    // Constructor. Done
    public Menu (String name, Scanner scanner) {
        this.name = name;
        this.scanner = scanner;
    }

    public Menu (String name, Scanner scanner, String prompt) {
        this.name = name;
        this.scanner = scanner;
        this.prompt = prompt;
    }

    //getter methods. Done
    public String getName () {
        return this.name;
    }

    public String getOptionsList () {

        if (options.isEmpty()) {
            return "There are No Options in this Menu.";
        }

        // I'm gonna use a StringBuilder for this method cus I want to test it out.
        StringBuilder optionsListStringBuilder = new StringBuilder();
        for (MenuOption option: options) {
            optionsListStringBuilder.append(option.getOptionName()).append(", ");
        }

        // let's remove the last comma and space
        optionsListStringBuilder.setLength(optionsListStringBuilder.length() - 2);

        return optionsListStringBuilder.toString();
    }

    // Method to add MenuOption objects to the list of options. Done
    public void addOptionToMenu(MenuOption<T> option) {

        if (option != null) {
            options.add(option);
        } else {
            System.out.println("Error - MenuOption Object Not Added to Menu: Please enter a valid menu option, MenuOption Object should not be null");
        }
    }

    // method to remove all options
    public void removeAllOptions () {
        if (!options.isEmpty()) {
            for (MenuOption option: options) {
                options.remove(option);
            }
        }
    }
    //Method to display a menu. Done
    public T displayMenu() {
        // Check if there are options to display.
        if (options.isEmpty()) {
            System.out.println("There are No Options to Display in this Menu.");
            return null;
        } else {
            // the return object
            T result = null;
            // to check if an error happened
            boolean errorOccurred = false;
            String errorMessage = "";

            try {
                // Check how many options we have; this is used to get the choice from user.
                numberOfOptions = options.size();

                System.out.println();
                // Print the prompt
                if (prompt != null) {
                    System.out.println(prompt);
                } else {
                    System.out.println("Please select the Option number to select an Option");
                }

                // Print all the options
                for (int i = 0; i < numberOfOptions; i++) {
                    System.out.printf("%d %s%n", i + 1, options.get(i).getOptionName());
                }

                int userChoice = getUserChoice();
                result = executeUserChoice(userChoice);
            } catch (Exception e) {
                errorOccurred = true;
                errorMessage = "The following error occurred while executing user choice: " + e.getMessage();
            }

            // Display error-message after user choice execution but before returning
            if (errorOccurred) {
                System.out.println(errorMessage);
            }

            return result;
        }
    }

    //Method to get under choice as input. Done
    public int getUserChoice () {
        System.out.printf("%nPlease enter the number of the desired option: ");

        //input validation loop
        while (true) {
            if (scanner.hasNextInt()) {
                int userChoice = scanner.nextInt();
                scanner.nextLine();
                if (userChoice <= 0 || userChoice > numberOfOptions) {
                    System.out.printf("Invalid choice, choice must be between 1 and %d%n", numberOfOptions);
                    System.out.print("Please enter the number of the desired option: ");
                } else {
                    return userChoice;
                }
            } else {
                System.out.printf("Invalid Input, please enter a valid integer between 1 and %d : ", numberOfOptions);
                scanner.nextLine();// breaks the infinite loop of invalid input messages.
            }
        }
    }

    // Method to run user choice. Done
    public T executeUserChoice(int userChoice) {

        System.out.printf("%nYou Chose option %d - %s%n", userChoice, options.get(userChoice - 1).getOptionName());
        try {
            return options.get(userChoice - 1).call();
        } catch (Exception e) {
            System.out.printf("The following error occurred while executing user choice: %s%n", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}