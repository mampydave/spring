package mg.itu.prom16.etu2564;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)


@Target(ElementType.METHOD)

public @interface Url {
    String value() default"";
}
