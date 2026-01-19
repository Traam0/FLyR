import core.abstraction.SwingApp;
import core.abstraction.SwingRouter;
import core.dependencyInjection.ServiceCollection;
import core.dependencyInjection.ServiceProvider;
import core.navigation.Router;
import core.networking.HttpRestClient;
import core.networking.HttpRestClientFactory;
import core.networking.JerseyHttpRestClient;
import core.security.AuthContext;
import core.security.AuthService;
import mvvm.viewModels.FlightSearchViewModel;
import mvvm.views.LoginView;
import mvvm.views.user.FlightSearchView;
import services.AuthenticationService;

import javax.swing.*;
import java.util.Objects;
import java.util.function.Function;

public class Application extends SwingApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Application().run();
        });
    }

    @Override
    protected void registerServices(ServiceCollection services) {
        //better ur singleton Factory data.why = [Thread Safe, lightWeigh] data.whenScoped = [different request tokens]
        services.registerSingleton(HttpRestClientFactory.class, (Function<ServiceProvider, HttpRestClientFactory>) sp -> new HttpRestClientFactory("http://localhost:8080/"));
        services.registerScoped(AuthService.class, AuthenticationService.class);

        //ViewModels
        services.registerScoped(FlightSearchViewModel.class, FlightSearchViewModel.class);

        //Views
        services.registerTransient(LoginView.class, LoginView.class);
        services.registerTransient(FlightSearchView.class, FlightSearchView.class);

    }

    @Override
    protected void configureWindow() {
        super.configureWindow();

        if (this.getRouter() instanceof SwingRouter router) {
            JFrame window = router.getWindow();

            window.setTitle("FlyR");
            window.setSize(1024, 768);

            // Set application icon
            try {
                ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));
                window.setIconImage(icon.getImage());
            } catch (Exception e) {
                getLogger().warning("Could not load application icon");
            }
        }
    }

    @Override
    protected void startApplication() {
        this.showWindow(FlightSearchView.class);
    }
}
