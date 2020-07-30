package artoria.util;

import artoria.exception.ExceptionUtils;
import artoria.validate.RegexValidator;
import artoria.validate.Validator;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import static artoria.common.Constants.ZERO;

/**
 * Pinyin tools.
 * @author Kahle
 */
public class PinyinUtils {
    private static final Validator CHINESE_VALIDATOR = new RegexValidator("[\\u4E00-\\u9FA5]+");
    private static final HanyuPinyinOutputFormat DEFAULT_OUTPUT_FORMAT;

    static {
        DEFAULT_OUTPUT_FORMAT = new HanyuPinyinOutputFormat();
        DEFAULT_OUTPUT_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);
        DEFAULT_OUTPUT_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        DEFAULT_OUTPUT_FORMAT.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
    }

    public static String[] convertSingleChineseToPinyin(char singleChinese) {

        return convertSingleChineseToPinyin(singleChinese, null);
    }

    public static String[] convertSingleChineseToPinyin(char singleChinese, HanyuPinyinOutputFormat outputFormat) {
        try {
            return PinyinHelper.toHanyuPinyinStringArray(
                    singleChinese,
                    outputFormat != null ? outputFormat : DEFAULT_OUTPUT_FORMAT
            );
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static String convertChineseToPinyin(String inputChinese) {

        return convertChineseToPinyin(inputChinese, true, DEFAULT_OUTPUT_FORMAT);
    }

    public static String convertChineseToPinyin(String inputChinese, boolean capitalize) {

        return convertChineseToPinyin(inputChinese, capitalize, DEFAULT_OUTPUT_FORMAT);
    }

    public static String convertChineseToPinyin(String inputChinese, boolean capitalize, HanyuPinyinOutputFormat outputFormat) {
        if (StringUtils.isBlank(inputChinese)) { return inputChinese; }
        StringBuilder result = new StringBuilder();
        char[] chineseCharArray = inputChinese.toCharArray();
        for (char singleChinese : chineseCharArray) {
            String[] pinyinArray;
            if (!CHINESE_VALIDATOR.validate(String.valueOf(singleChinese))
                    || ArrayUtils.isEmpty(
                    pinyinArray = convertSingleChineseToPinyin(singleChinese, outputFormat)
            )) {
                result.append(singleChinese);
                continue;
            }
            String pinyin = pinyinArray[ZERO];
            if (capitalize) {
                pinyin = StringUtils.capitalize(pinyin);
            }
            result.append(pinyin);
        }
        return result.toString();
    }

}
