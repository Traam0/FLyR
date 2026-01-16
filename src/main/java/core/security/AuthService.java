package core.security;

public interface AuthService {
    UserPrincipal authenticate(String username, String password);
    boolean signIn(UserPrincipal principal);
}
