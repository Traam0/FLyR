package controllers;

import core.abstractions.ControllerBase;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mvvm.models.Flight;
import repositories.FlightsRepository;
import repositories.SeatsRepository;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/flights")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlightController extends ControllerBase {
    private static final Logger logger = Logger.getLogger(FlightController.class.getName());
    private final FlightsRepository repository;
    private final SeatsRepository seatsRepository;

    public FlightController(FlightsRepository repository, SeatsRepository seatsRepository) {
        this.repository = repository;
        this.seatsRepository = seatsRepository;
    }

    @GET
    public Response getFlights() {
        try {
            var result = this.repository.selectAll();
            if (result.isEmpty()) return Response.status(Response.Status.NO_CONTENT).build();
            return Response.ok(result).build();
        } catch (Exception e) {
            logger.severe("Error getting flights: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve flights"))
                    .build();
        }
    }

    @GET
    @Path("/search")
    public Response searchFlights(
            @QueryParam("departure") String departureCity,
            @QueryParam("arrival") String arrivalCity,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("passengers") int passengerCount,
            @QueryParam("roundTrip") @DefaultValue("false") boolean roundTrip) {

        try {
            if (departureCity == null || arrivalCity == null || from == null)
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "departure, arrival, and from date are required"))
                        .build();

            List<Flight> outboundFlights = repository.searchFlights(
                    departureCity, arrivalCity, from, passengerCount);

            if (!roundTrip) {
                return Response.ok(outboundFlights).build();
            }

            if (to == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Return date is required for round trip"))
                        .build();
            }

            // Find return flights (swap departure and arrival)
            List<Flight> returnFlights = repository.searchFlights(
                    arrivalCity, departureCity, to, passengerCount);

            Map<String, List<Flight>> result = Map.of(
                    "outbound", outboundFlights,
                    "return", returnFlights
            );

            return Response.ok(result).build();

        } catch (Exception e) {
            logger.severe("Error searching flights: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to search flights"))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getFlight(@PathParam("id") int id) {
        try {
            var flight = this.repository.selectWhereId(id);
            if (flight.isEmpty())
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", String.format("Flight %d does not or no longer exists", id)))
                        .build();
            return Response.ok(flight).build();
        } catch (Exception e) {
            logger.severe("Error getting flights: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve flights"))
                    .build();
        }
    }

    @GET
    @Path("/{id}/details")
    public Response getFlightDetails(@PathParam("id") int id) {
        try {
            var flightOpt = this.repository.selectWhereId(id);
            if (flightOpt.isEmpty()) return NotFound(String.format("Flight %d does not or no longer exists", id));
            var flight = flightOpt.get();
            if (flight.getAirCraft() == null)
                return NotFound(String.format("No aircraft assigned to flight number %d", id));
            var seats = this.seatsRepository.selectFlightSeats(flight.getId());
            flight.getAirCraft().setSeats(seats);
            return Response.ok(flight).build();
        } catch (Exception e) {
            logger.severe("Error getting flights: " + e.getMessage());
            return InternalServerError(e.getMessage());
        }
    }
}
