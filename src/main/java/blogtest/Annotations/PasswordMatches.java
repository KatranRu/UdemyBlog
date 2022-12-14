package blogtest.Annotations;

import blogtest.Validations.EmailValidator;
import blogtest.Validations.PasswordMatchesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {

    String message() default "Password do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
