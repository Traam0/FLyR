package controllers;

import contracts.UserDto;
import contracts.requests.LoginDto;
import core.abstractions.ControllerBase;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mvvm.models.User;
import repositories.UserRepository;

import java.util.logging.Logger;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController extends ControllerBase {

    private final UserRepository repository;
    private final Logger logger;

    public AuthController(UserRepository repository, Logger logger) {
        this.repository = repository;
        this.logger = logger;
    }

    @POST
    @Path("/login")
    public Response login(LoginDto body) {
        try {
            var userOpt = this.repository.selectWhereUsername(body.username());
            if (userOpt.isEmpty()) return NotFound("User does not or no longer exists.");
            var user = userOpt.get();
            logger.info("User: " + user.toString());
            if (!user.getPassword().equals(body.password())) return Unauthorized("Wrong password.");
            return Ok(new UserDto(user.getId(), user.getUsername(), user.getRole().name()));
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
            return InternalServerError(e.getMessage());
        }
    }
}
