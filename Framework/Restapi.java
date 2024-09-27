package mg.itu.prom16.etu2564;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)


@Target(ElementType.METHOD)
public @interface Restapi {
    String value() default"";
} 