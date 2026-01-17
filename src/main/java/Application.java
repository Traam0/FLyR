import core.abstraction.SwingApp;
import core.abstraction.SwingRouter;
import core.dependencyInjection.ServiceCollection;
import core.security.AuthContext;

import javax.swing.*;
import java.util.Objects;

public class Application extends SwingApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Application().run();
        });
    }

    @Override
    protected void registerServices(ServiceCollection services) {
        // Register security
        services.registerSingleton(AuthContext.class, AuthContext.class);

        // Register services

        // Register views (transient - new instance each time)

    }

    @Override
    protected void configureWindow() {
        super.configureWindow(); // Call parent if you want default config

        // Custom window configuration
        if (getRouter() instanceof SwingRouter router) {
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
    }
}
