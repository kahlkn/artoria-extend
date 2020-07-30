package artoria.data.mask;

import artoria.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WithPhoneNumberMasker implements DataMasker {
    private DataMasker phoneNumberMasker;
    private Pattern pattern;

    public WithPhoneNumberMasker() {

        this(null, null);
    }

    public WithPhoneNumberMasker(String regex, String cover) {
        this.phoneNumberMasker = new PhoneNumberMasker(cover);
        if (StringUtils.isBlank(regex)) {
            regex = "1[3|4|5|6|7|8|9]\\d{9}";
        }
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public String mask(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            String phoneNumber = matcher.group();
            String mask = phoneNumberMasker.mask(phoneNumber);
            data = StringUtils.replace(data, phoneNumber, mask);
        }
        return data;
    }

}
