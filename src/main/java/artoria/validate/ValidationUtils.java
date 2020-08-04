package artoria.validate;

import artoria.common.ErrorCode;
import artoria.exception.BusinessException;
import artoria.util.ArrayUtils;
import artoria.util.CollectionUtils;
import artoria.util.MapUtils;
import artoria.util.StringUtils;

import java.util.Collection;
import java.util.Map;

public class ValidationUtils {

    public static void isFalse(boolean expression, ErrorCode errorCode) {
        if (expression) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isFalse(boolean expression, String code, String description) {
        if (expression) {
            throw new BusinessException(code, description);
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isTrue(boolean expression, String code, String description) {
        if (!expression) {
            throw new BusinessException(code, description);
        }
    }

    public static void isNull(Object object, ErrorCode errorCode) {
        if (object != null) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isNull(Object object, String code, String description) {
        if (object != null) {
            throw new BusinessException(code, description);
        }
    }

    public static void notNull(Object object, ErrorCode errorCode) {
        if (object == null) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notNull(Object object, String code, String description) {
        if (object == null) {
            throw new BusinessException(code, description);
        }
    }

    public static void isEmpty(byte[] array, ErrorCode errorCode) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isEmpty(byte[] array, String code, String description) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(code, description);
        }
    }

    public static void notEmpty(byte[] array, ErrorCode errorCode) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(byte[] array, String code, String description) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(code, description);
        }
    }

    public static void isEmpty(Object[] array, ErrorCode errorCode) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isEmpty(Object[] array, String code, String description) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(code, description);
        }
    }

    public static void notEmpty(Object[] array, ErrorCode errorCode) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(Object[] array, String code, String description) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(code, description);
        }
    }

    public static void isEmpty(Collection<?> collection, ErrorCode errorCode) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isEmpty(Collection<?> collection, String code, String description) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw new BusinessException(code, description);
        }
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(Collection<?> collection, String code, String description) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(code, description);
        }
    }

    public static void isEmpty(Map<?, ?> map, ErrorCode errorCode) {
        if (MapUtils.isNotEmpty(map)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isEmpty(Map<?, ?> map, String code, String description) {
        if (MapUtils.isNotEmpty(map)) {
            throw new BusinessException(code, description);
        }
    }

    public static void notEmpty(Map<?, ?> map, ErrorCode errorCode) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(Map<?, ?> map, String code, String description) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(code, description);
        }
    }

    public static void isEmpty(String text, ErrorCode errorCode) {
        if (StringUtils.isNotEmpty(text)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isEmpty(String text, String code, String description) {
        if (StringUtils.isNotEmpty(text)) {
            throw new BusinessException(code, description);
        }
    }

    public static void notEmpty(String text, ErrorCode errorCode) {
        if (StringUtils.isEmpty(text)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(String text, String code, String description) {
        if (StringUtils.isEmpty(text)) {
            throw new BusinessException(code, description);
        }
    }

    public static void isBlank(String text, ErrorCode errorCode) {
        if (StringUtils.isNotBlank(text)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isBlank(String text, String code, String description) {
        if (StringUtils.isNotBlank(text)) {
            throw new BusinessException(code, description);
        }
    }

    public static void notBlank(String text, ErrorCode errorCode) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notBlank(String text, String code, String description) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(code, description);
        }
    }

    public static void isContain(String textToSearch, String substring, ErrorCode errorCode) {
        if (!textToSearch.contains(substring)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isContain(String textToSearch, String substring, String code, String description) {
        if (!textToSearch.contains(substring)) {
            throw new BusinessException(code, description);
        }
    }

    public static void notContain(String textToSearch, String substring, ErrorCode errorCode) {
        if (textToSearch.contains(substring)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notContain(String textToSearch, String substring, String code, String description) {
        if (textToSearch.contains(substring)) {
            throw new BusinessException(code, description);
        }
    }

    public static void isAssignable(Class<?> superType, Class<?> subType, ErrorCode errorCode) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isAssignable(Class<?> superType, Class<?> subType, String code, String description) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new BusinessException(code, description);
        }
    }

    public static void isInstanceOf(Class<?> type, Object object, ErrorCode errorCode) {
        if (!type.isInstance(object)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isInstanceOf(Class<?> type, Object object, String code, String description) {
        if (!type.isInstance(object)) {
            throw new BusinessException(code, description);
        }
    }

    public static void noNullElements(Object[] array, ErrorCode errorCode) {
        for (Object element : array) {
            if (element == null) {
                throw new BusinessException(errorCode);
            }
        }
    }

    public static void noNullElements(Object[] array, String code, String description) {
        for (Object element : array) {
            if (element == null) {
                throw new BusinessException(code, description);
            }
        }
    }

    public static void validate(String name, Object target, ErrorCode errorCode) {
        if (!ValidatorUtils.validate(name, target)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void validate(String name, Object target, String code, String description) {
        if (!ValidatorUtils.validate(name, target)) {
            throw new BusinessException(code, description);
        }
    }

}
