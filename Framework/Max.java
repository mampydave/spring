package mg.itu.prom16.etu2564;

import java.lang.annotation.*;

@Target(ElementType.FIELD) 
@Retention(RetentionPolicy.RUNTIME)
public @interface Max {
    int value() default Integer.MAX_VALUE; 
    
}
