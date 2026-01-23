import controllers.AuthController;
import controllers.ClientController;
import controllers.FlightController;
import core.abstractions.JaxRsServer;
import core.dependencyInjection.ServiceCollection;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import repositories.ClientRepository;
import repositories.FlightsRepository;
import repositories.SeatsRepository;
import repositories.UserRepository;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class Server extends JaxRsServer {
    private static final String BASE_URI = "http://localhost:8085/";
    private static final Logger logger = Logger.getLogger(Server.class.getName());


    public static void main(String[] args) {
        var server = new Server();
        try {
            server.run();
        } catch (Exception e) {
            server.exit();
        }
    }

    @Override
    protected void registerServices(ServiceCollection services) {
        //controllers
        services.registerSingleton(AuthController.class, AuthController.class);
        services.registerSingleton(ClientController.class, ClientController.class);
        services.registerSingleton(FlightController.class, FlightController.class);
        //repositories
        services.registerScoped(UserRepository.class, UserRepository.class);
        services.registerScoped(SeatsRepository.class, SeatsRepository.class);
        services.registerScoped(ClientRepository.class, ClientRepository.class);
        services.registerScoped(FlightsRepository.class, FlightsRepository.class);
    }

    @Override
    protected void startApplication() throws IOException {
        final ResourceConfig config = new ResourceConfig()
                .register(getServiceProvider().getRequiredService(AuthController.class))
                .register(getServiceProvider().getRequiredService(FlightController.class))
                .register(getServiceProvider().getRequiredService(ClientController.class))
                .register(WadlResource.class);

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
        System.out.printf("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...%n", BASE_URI);
        System.in.read();
        server.shutdownNow();
    }
}
