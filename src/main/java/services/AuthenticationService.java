package services;


import core.security.AuthContext;
import core.security.AuthService;
import core.security.UserPrincipal;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthenticationService implements AuthService {

    private final AuthContext context;
    private final Logger logger;

    public AuthenticationService(AuthContext context, Logger logger) {
        this.context = context;
        this.logger = logger;
        this.logger.log(Level.INFO, "AuthenticationService created");
    }

    @Override
    public UserPrincipal authenticate(String username, String password) {
//        Optional<User> user = this.repository.selectWhereUsername(username);
//        if (user.isEmpty()) {
//            return null;
//        }
//        User u = user.get();
//        if (!Objects.equals(u.getPassword(), password)) return null;
//
//        return new UserPrincipal(u.getId(), u.getUsername(), u.getRole());
        return null;
    }

    @Override
    public boolean signIn(UserPrincipal principal) {
        try {
            context.login(principal);
            return true;
        } catch (IllegalStateException e) {
            this.logger.severe(() -> String.format("@AuthService.signIn: %s", e.getMessage()));
            return false;
        }
    }
}
