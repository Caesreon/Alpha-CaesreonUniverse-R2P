package caesreon.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Diese Methode wird ueberarbeitet und in Zukunft definitiv entfernt und durch eine bessere Methode ersetzt.
 *
 * @author Julian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, MODULE, PARAMETER, TYPE})
public @interface Veraltet {
    boolean forRemoval() default true;

}
