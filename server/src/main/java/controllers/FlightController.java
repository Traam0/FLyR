package controllers;

import core.abstractions.JaxRsServer;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mvvm.models.Flight;
import repositories.FlightsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/flights")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlightController {
    private static final Logger logger = Logger.getLogger(FlightController.class.getName());
    private final FlightsRepository repository;

    public FlightController(FlightsRepository repository) {
        this.repository = repository;
    }

    @GET
    public Response getFlights() {
        try {
            var result = this.repository.getAll();
            return Response.ok(new ArrayList<>()).build();
        } catch (Exception e) {
            logger.severe("Error getting flights: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve flights"))
                    .build();
        }
    }
}
