package sparkle.cms.registration.ui.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * UserRegistration
 * Created by thebaz on 07/04/15.
 */
@FieldMatch.List({
        @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
})
public class UserRegistration {
    @NotNull
    @Size(min = 8, max = 25)
    private String password;

    @NotNull
    @Size(min = 8, max = 25)
    private String confirmPassword;

    @NotNull
    private String email;

    @NotNull
    private String name;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
