package artoria.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EventRecord.
 * @author Kahle
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventRecord {

    String name();

    String type() default "";

    boolean input() default false;

    boolean output() default false;

}
