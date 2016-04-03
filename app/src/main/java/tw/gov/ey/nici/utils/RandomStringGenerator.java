package tw.gov.ey.nici.utils;

import java.util.Random;

public class RandomStringGenerator {
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    public static String getString(final int strLength) {
        if (strLength < 0) {
            throw new IllegalArgumentException();
        }
        Random random = new Random();
        StringBuilder builder = new StringBuilder(strLength);
        for (int i = 0; i < strLength; i++) {
            builder.append(ALLOWED_CHARACTERS.charAt(
                    random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return builder.toString();
    }
}
