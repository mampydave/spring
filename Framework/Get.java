package mg.itu.prom16.etu2564;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Get {
    String value() default"";
} 