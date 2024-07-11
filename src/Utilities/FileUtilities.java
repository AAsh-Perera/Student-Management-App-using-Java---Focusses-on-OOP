package Utilities;

import java.io.File;

public class FileUtilities {

    public static boolean isFileEmpty (File file) {
        if (file.exists() && file.isFile()) {
            // next check if the file is empty or not
            return file.length() == 0;
        }
        else {
            return true;
        }
    }
}
