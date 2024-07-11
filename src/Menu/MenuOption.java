package Menu;

import java.util.concurrent.Callable;

// using the generic <T> here.

public class MenuOption<T> implements Callable<T> {

    /*
        This is the class that will create MenuOption objects for the Menu class objects,
        and this class implements the runnable interface.

        When constructing a Menu.MenuOption obj, pass one runnable object as the second argument.
        when the run() the Menu.MenuOption obj is called it will run the runnable code block.
        note this works with a lambda function as well.

        Sometimes, we will need to make an option that can return objects or stuff,
        so for that we will also implement the Callable interface.

        So depending on the use case, the user can either parse a callable or a runnable object

        These objects should be passed to Menu objects using the addMenuOption() method of Menu class objects.
     */

    private final String optionName;
    private final Callable<T> callableAction;
    private final Runnable runnableAction;

    // Constructor for option with Callable action
    public MenuOption(String optionName, Callable<T> action) {
        this.optionName = optionName;
        this.callableAction = action;
        this.runnableAction = null;
    }

    // Constructor for menu options with Runnable action
    public MenuOption(String optionName, Runnable action) {
        this.optionName = optionName;
        this.callableAction = null;
        this.runnableAction = action;
    }

    // the method that either calls the run() method or call() method
    @Override
    public T call() throws Exception {
        if (callableAction != null) {
            return callableAction.call();
        } else if (runnableAction != null) {
            runnableAction.run();
            return null; // if not a callable still return cus that how i made the menu class work.
        } else {
            System.out.println("Error - Cannot execute MenuOption callable or runnable: No callable or runnable action was provided.");
            return null;
        }
    }

    // now for the getter methods
    public String getOptionName() {
        return optionName;
    }
    public Callable<T> getCallableAction() {
        return callableAction;
    }
}