package mg.itu.prom16.etu2564;

import java.lang.annotation.*;

@Target(ElementType.FIELD) 
@Retention(RetentionPolicy.RUNTIME)
public @interface Min {
    int value() default Integer.MIN_VALUE; 
}
