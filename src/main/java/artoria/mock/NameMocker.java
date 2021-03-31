package artoria.mock;

import artoria.util.RandomUtils;
import artoria.util.StringUtils;

import static artoria.common.Constants.*;

public class NameMocker implements Mocker<String> {
    private static final char[] NAME_CHAR_ARRAY = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String ENGLISH_1 = "en";
    private static final String ENGLISH_2 = "eng";

    private String randomName(String language) {
        int length = RandomUtils.nextInt(SIX) + THREE;
        String randomName = RandomUtils.nextString(NAME_CHAR_ARRAY, length);
        return StringUtils.capitalize(randomName);
    }

    /**
     * @param language An ISO 639 language code
     */
    protected boolean isEnglish(String language) {

        return ENGLISH_1.equalsIgnoreCase(language) || ENGLISH_2.equalsIgnoreCase(language);
    }

    protected String firstName(String language) {

        return randomName(language);
    }

    protected String middleName(String language) {
        boolean nextBool = RandomUtils.nextBoolean();
        return nextBool ? randomName(language) : EMPTY_STRING;
    }

    protected String lastName(String language) {

        return randomName(language);
    }

    protected String fullName(String language) {
        String middleName = middleName(language);
        if (StringUtils.isNotBlank(middleName)) {
            middleName = BLANK_SPACE + middleName + BLANK_SPACE;
            return lastName(language) + middleName + firstName(language);
        }
        else {
            return lastName(language) + BLANK_SPACE + firstName(language);
        }
    }

    @Override
    public String mock() {

        return fullName(ENGLISH_1);
    }

}
