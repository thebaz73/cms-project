package sparkle.cms.registration.ui.domain;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * RegistrationValidator
 * Created by thebaz on 07/04/15.
 */
@Component("registrationValidator")
public class RegistrationValidator implements Validator {
    /**
     * Can this {@link org.springframework.validation.Validator} {@link #validate(Object, Errors) validate}
     * instances of the supplied {@code clazz}?
     * <p>This method is <i>typically</i> implemented like so:
     * <pre class="code">return Foo.class.isAssignableFrom(clazz);</pre>
     * (Where {@code Foo} is the class (or superclass) of the actual
     * object instance that is to be {@link #validate(Object, Errors) validated}.)
     *
     * @param clazz the {@link Class} that this {@link org.springframework.validation.Validator} is
     *              being asked if it can {@link #validate(Object, Errors) validate}
     * @return {@code true} if this {@link org.springframework.validation.Validator} can indeed
     * {@link #validate(Object, Errors) validate} instances of the
     * supplied {@code clazz}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistration.class.equals(clazz);
    }

    /**
     * Validate the supplied {@code target} object, which must be
     * of a {@link Class} for which the {@link #supports(Class)} method
     * typically has (or would) return {@code true}.
     * <p>The supplied {@link org.springframework.validation.Errors errors} instance can be used to report
     * any resulting validation errors.
     *
     * @param target the object that is to be validated (can be {@code null})
     * @param errors contextual state about the validation process (never {@code null})
     * @see ValidationUtils
     */
    @Override
    public void validate(Object target, Errors errors) {
        UserRegistration registration = (UserRegistration) target;
        if (registration.getPassword().length() < 8) {
            errors.rejectValue("password", "password.wrong");
        }
        if (!registration.getPassword().equalsIgnoreCase(registration.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "password.match");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.wrong");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "password.match");
    }
}
