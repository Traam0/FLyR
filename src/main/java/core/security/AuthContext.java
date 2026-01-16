package core.security;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthContext {
    public UserPrincipal currentUser;
    private String sessionId;
    private Set<String> roles;

    public AuthContext() {
        this.roles = new HashSet<>();
    }

    public void login(UserPrincipal user) throws IllegalStateException {
        if (this.isAuthenticated()) throw new IllegalStateException("user already logged in.");
        this.currentUser = user;
        this.sessionId = UUID.randomUUID().toString();
    }

    public boolean inRole(String role) {
        return roles.contains(role);
    }

    public boolean isAuthenticated() {
        return this.currentUser != null;
    }

    public void logout() {
        this.currentUser = null;
        this.sessionId = null;
    }


    public UserPrincipal getCurrentUser() {
        return this.currentUser;
    }

    public String getSessionId() {
        return this.sessionId;
    }
}
