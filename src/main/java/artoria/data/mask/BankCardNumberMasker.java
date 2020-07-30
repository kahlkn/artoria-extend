package artoria.data.mask;

import artoria.util.StringUtils;

import static artoria.common.Constants.*;

public class BankCardNumberMasker implements DataMasker {
    private String cover;

    public BankCardNumberMasker() {

        this(null);
    }

    public BankCardNumberMasker(String cover) {

        this.cover = StringUtils.isNotBlank(cover) ? cover : ASTERISK;
    }

    public String getCover() {

        return cover;
    }

    @Override
    public String mask(String bankCardNumber) {
        if (StringUtils.isBlank(bankCardNumber)) { return bankCardNumber; }
        int dataLength = bankCardNumber.length();
        if (dataLength <= FOUR) { return bankCardNumber; }
        int beginIndex = dataLength - FOUR;
        String back =
                bankCardNumber.substring(beginIndex, dataLength);
        StringBuilder builder = new StringBuilder();
        for (int i = ZERO; i < 12; i++) {
            if (i % FOUR == ZERO) {
                builder.append(BLANK_SPACE);
            }
            builder.append(cover);
        }
        builder.append(BLANK_SPACE);
        builder.append(back);
        return builder.toString();
    }

}
