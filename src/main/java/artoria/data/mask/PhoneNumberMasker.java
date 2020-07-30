package artoria.data.mask;

import artoria.util.StringUtils;

import static artoria.common.Constants.*;

public class PhoneNumberMasker implements DataMasker {
    private static final Integer PHONE_NUMBER_LENGTH = 11;
    private static final Integer COVER_LENGTH = 5;
    private String cover;

    public PhoneNumberMasker() {

        this(null);
    }

    public PhoneNumberMasker(String cover) {

        this.cover = StringUtils.isNotBlank(cover) ? cover : ASTERISK;
    }

    public String getCover() {

        return cover;
    }

    @Override
    public String mask(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }
        int length = data.length();
        if (length == PHONE_NUMBER_LENGTH) {
            String prefix = data.substring(ZERO, THREE);
            String suffix = data.substring(EIGHT, 11);
            StringBuilder builder = new StringBuilder();
            builder.append(prefix);
            for (int i = ZERO; i < COVER_LENGTH; i++) {
                builder.append(cover);
            }
            builder.append(suffix);
            return builder.toString();
        }
        return data;
    }

}
