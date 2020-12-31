package artoria.fake;

import artoria.exception.ExceptionUtils;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.reflect.ReflectUtils;
import artoria.util.Assert;
import artoria.util.ObjectUtils;
import artoria.util.StringUtils;
import com.github.javafaker.Faker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static artoria.common.Constants.DOT;
import static artoria.common.Constants.ZERO;

public class JavaFakerFaker extends AbstractFaker {
    private static final List<String> EXCLUDE_METHOD_NAMES = Arrays.asList(
            "wait", "equals", "toString", "hashCode", "getClass", "notify", "notifyAll"
    );
    private static final List<String> EXCLUDE_FIELD_NAMES = Arrays.asList(
            "randomService", "fakeValuesService"
    );
    private static Logger log = LoggerFactory.getLogger(JavaFakerFaker.class);
    private Map<String, Method> methodMap;
    private String fakerName;
    private Object target;
    private Faker faker;

    private String defaultDirective(String fakerName) {
        if ("phone_number".equals(fakerName)) { return "phone_number"; }
        if ("address".equals(fakerName)) { return "full_address"; }
        if ("number".equals(fakerName)) { return "digit"; }
        if ("avatar".equals(fakerName)) { return "image"; }
        if ("name".equals(fakerName)) { return "name"; }
        if ("bool".equals(fakerName)) { return "bool"; }
        if ("job".equals(fakerName)) { return "title"; }
        if ("app".equals(fakerName)) { return "name"; }
        throw new IllegalArgumentException(
                "The directive in the expression are required. "
        );
    }

    public JavaFakerFaker(Faker javaFaker, String fakerName) {
        Assert.notBlank(fakerName, "Parameter \"fakerName\" must not blank. ");
        Assert.notNull(javaFaker, "Parameter \"javaFaker\" must not null ");
        // Create the field name and handle the faker name.
        String fieldName = StringUtils.underlineToCamel(fakerName);
        fieldName = StringUtils.uncapitalize(fieldName);
        if (EXCLUDE_FIELD_NAMES.contains(fieldName)) {
            throw new IllegalArgumentException("The faker name is invalid. ");
        }
        fakerName = StringUtils.camelToUnderline(fieldName);
        fakerName = fakerName.toLowerCase();
        try {
            // Find field by faker name.
            Field field = ReflectUtils.findField(Faker.class, fieldName);
            if (field != null) {
                ReflectUtils.makeAccessible(field);
                this.target = field.get(javaFaker);
            }
            if (field == null || target == null) {
                throw new IllegalArgumentException("The faker name is not mapped. ");
            }
            // Find all the corresponding methods.
            Method[] methods = ReflectUtils.findMethods(target.getClass());
            Map<String, Method> map = new HashMap<String, Method>();
            for (Method method : methods) {
                // Loop and filter.
                String methodName = method.getName();
                if (EXCLUDE_METHOD_NAMES.contains(methodName)) { continue; }
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length > ZERO) { continue; }
                Class<?> returnType = method.getReturnType();
                if (!isBasicType(returnType)) { continue; }
                // Print the display name.
                String displayName = StringUtils.camelToUnderline(methodName);
                displayName = fakerName + DOT + displayName.toLowerCase();
                log.debug("Register \"{}\". ", displayName);
                map.put(methodName, method);
            }
            Assert.notEmpty(map, "The faker has no corresponding function. ");
            this.methodMap = Collections.unmodifiableMap(map);
            this.fakerName = fakerName;
            this.faker = javaFaker;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public String name() {

        return fakerName;
    }

    @Override
    public <T> T fake(Class<T> clazz, String expression) {
        verifyParameters(clazz, expression);
        try {
            String directive = parseDirective(expression);
            if (StringUtils.isBlank(directive)) {
                directive = defaultDirective(name());
            }
            String methodName = StringUtils.underlineToCamel(directive);
            methodName = StringUtils.uncapitalize(methodName);
            Method method = methodMap.get(methodName);
            Assert.notNull(method, "The expression is wrong. ");
            Object invoke = method.invoke(target);
            return ObjectUtils.cast(invoke, clazz);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
