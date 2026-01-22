package core.abstractions;

import jakarta.ws.rs.core.Response;

import java.util.Map;

public abstract class ControllerBase {

    public static Response Ok() {
        return Response.status(Response.Status.OK).build();
    }

    public static Response Ok(Object body) {
        return Response.ok(body).build();
    }

    public static Response NotFound() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public static Response NotFound(String message) {
        return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", message)).build();
    }

    public static Response Unauthorized(String message) {
        return Response.status(Response.Status.UNAUTHORIZED).entity(Map.of("message", message)).build();
    }

    public static Response InternalServerError(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", message)).build();
    }
}
