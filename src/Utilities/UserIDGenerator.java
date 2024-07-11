package Utilities;

import java.util.Random;

// all the code for this utility class is referenced from Stackoverflow.com, Available at: https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
public class UserIDGenerator {

    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private Random random = new Random();

    public String generateUniqueID (int idLength) {
        if (idLength < 8) {
            throw new IllegalArgumentException("Unique ID length must be at least 8");
        }
        StringBuilder stringBuilder = new StringBuilder(idLength);
        int index;
        for (int i = 0; i < idLength; i++) {
            index = random.nextInt(ALPHANUMERIC_CHARACTERS.length());
            stringBuilder.append(ALPHANUMERIC_CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }
}

