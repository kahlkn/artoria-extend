package artoria.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Event record.
 * If you want to desensitize the input or output parameters.
 * You can desensitize the data before saving it to the database.
 * Because data desensitization is complex and biased towards business logic.
 * So there will be no data desensitization support here.
 * @author Kahle
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventRecord {

    /**
     * EventRecord name.
     */
    String name();

    /**
     * EventRecord type.
     */
    String type() default "";

    /**
     * EventRecord input.
     */
    boolean input() default false;

    /**
     * EventRecord output.
     */
    boolean output() default false;

}
