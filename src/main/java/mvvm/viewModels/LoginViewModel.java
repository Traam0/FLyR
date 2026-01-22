package mvvm.viewModels;


import contracts.ErrorCode;
import contracts.ErrorState;
import core.mvvm.Command;
import core.mvvm.Property;
import core.mvvm.RelayCommand;
import core.navigation.Router;
import core.security.AuthService;
import core.security.UserPrincipal;
import mvvm.views.flight.FlightSearchView;

import java.util.logging.Logger;

public class LoginViewModel {
    private final Logger logger;
    private final AuthService authService;
    private final Router router;

    private final Property<String> username = new Property<>(null);
    private final Property<String> password = new Property<>(null);
    private final Property<ErrorState> error = new Property<>(null);
    private final Property<String> usernameError = new Property<>(null);
    private final Property<String> passwordError = new Property<>(null);
    private final Command loginCommand;

    public LoginViewModel(Logger logger, AuthService authService, Router router) {
        this.logger = logger;
        this.authService = authService;
        this.router = router;

        this.loginCommand = new RelayCommand((param) -> {
            this.logger.info("loggin in user " + username.get());
            UserPrincipal user = this.authService.authenticate(this.username.get(), this.password.get());
            if (user == null) {
                this.error.set(new ErrorState(ErrorCode.INVALID_CREDENTIALS, "Invalid username or password"));
                this.logger.severe(String.format("failed to authenticate: %s", error.get().message()));
                return;
            }
            if (this.authService.signIn(user)) {
                this.logger.info("login success");
                //redirect
                this.router.navigateTo(FlightSearchView.class);

            } else {
                this.error.set(new ErrorState(ErrorCode.INTERNAL_ERROR, "Oops, something went wrong."));
                this.logger.severe(String.format("failed to signIn: %s", error.get().message()));

            }

        }, (param) -> {
            return this.usernameIsValid(username.get()) && this.passwordIsValid(this.password.get());
        });


        this.username.subscribe((oldValue, newValue) -> {
            this.getLoginCommand().canExecuteChanged();
            this.usernameIsValid(newValue);

        });
        this.password.subscribe((oldValue, newValue) -> {
            this.getLoginCommand().canExecuteChanged();
            boolean a = this.passwordIsValid(newValue);
            this.logger.info(String.format("password: %s, %s", newValue, a ? "valid" : "invalid"));
        });
    }

    private boolean usernameIsValid(Object newValue) {
        if ((!(newValue instanceof String str))) {
            this.usernameError.set("username cannot be empty.");
            return false;
        }
        if (str.isBlank() || str.length() < 3) {
            this.usernameError.set("username cannot be empty.  must be at least 3 characters long.");
            return false;
        }

        return true;
    }

    private boolean passwordIsValid(Object newValue) {
        if (!(newValue instanceof String str)) {
            this.passwordError.set("password cannot be empty.");
            return false;
        }
        if (str.isBlank() || str.length() < 6) {
            this.passwordError.set("password cannot be empty. must be at least 6 characters long.");
            return false;
        }
        return true;
    }

    public Property<String> username() {
        return this.username;
    }

    public Property<String> usernameError() {
        return this.usernameError;
    }

    public Property<String> password() {
        return this.password;
    }

    public Property<String> passwordError() {
        return this.passwordError;
    }

    public Property<ErrorState> error() {
        return this.error;
    }

    public Command getLoginCommand() {
        return this.loginCommand;
    }


}
