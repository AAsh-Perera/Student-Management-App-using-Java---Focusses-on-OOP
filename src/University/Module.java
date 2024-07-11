package University;

import java.io.Serial;
import java.io.Serializable;

public record Module(String name) implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    /*
        the Module class for this assignment will be a record class, because it does not have any robust methods.
        the rest of the functionalities logically come under other classes and packages like the Menu or Student classes, etc.
     */

    // method for getting the total number of students who passed the module.
    public int getStudentPassedCount(Student[] allStudents) {
        int count = 0;
        for (Student student : allStudents) {
            // check to see if the student is null or not.
            if (student != null && student.getModuleMark(this) >= 40) {
                count++;
            }
        }
        return count;
    }
}