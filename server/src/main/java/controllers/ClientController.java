package controllers;

import contracts.UserDto;
import core.abstractions.ControllerBase;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import repositories.ClientRepository;
import repositories.UserRepository;

import java.util.logging.Logger;

@Path("/clients")
public class ClientController extends ControllerBase {

    private final Logger logger;
    private final ClientRepository repository;

    public ClientController(Logger logger, ClientRepository repository) {
        super();
        this.logger = logger;
        this.repository = repository;
    }

    @GET
    @Path("/{id}")
    public Response getClient(@PathParam("id") int id) {
        try {
            var clientOpt = this.repository.findClient(id);
            if (clientOpt.isEmpty()) return NotFound("Client does not exist.");
            return Response.ok(clientOpt.get()).build();
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
            return InternalServerError(e.getMessage());
        }
    }
}
