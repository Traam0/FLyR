package services;


import contracts.UserDto;
import contracts.requests.LoginDto;
import core.networking.HttpRestClient;
import core.networking.HttpRestClientFactory;
import core.networking.exceptions.RestClientException;
import core.security.AuthContext;
import core.security.AuthService;
import core.security.UserPrincipal;
import mvvm.models.User;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthenticationService implements AuthService {
    private final HttpRestClient httpClient;
    private final AuthContext context;
    private final Logger logger;

    public AuthenticationService(AuthContext context, HttpRestClientFactory httpRestClientFactory, Logger logger) {
        this.context = context;
        this.logger = logger;
        this.httpClient = httpRestClientFactory.createClient();
        this.logger.log(Level.INFO, "AuthenticationService created");
    }

    @Override
    public UserPrincipal authenticate(String username, String password) {
        try {
            var response = this.httpClient.post("/auth/login", new LoginDto(username, password), UserDto.class);
            return new UserPrincipal(response.getId(), response.getUsername(), response.getRole());
        } catch (RestClientException e) {
            logger.severe(e.getMessage());
        }
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
