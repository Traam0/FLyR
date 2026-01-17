package core.abstraction;

import core.dependencyInjection.ServiceCollection;
import core.dependencyInjection.ServiceProvider;
import core.mvvm.View;
import core.navigation.Router;

import javax.swing.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class for Swing applications.
 * Handles DI setup, application lifecycle, and coordination with SwingRouter.
 */
public abstract class SwingApp {

    private ServiceProvider serviceProvider;
    private Router router;
    private Logger logger;

    /**
     * Main entry point to run the application.
     * Call this from your main method.
     */
    public void run() {
        try {
            // Initialize logging
            initializeLogging();

            // Build service container
            serviceProvider = buildServiceProvider();

            // Get router
            router = serviceProvider.getRequiredService(Router.class);

            // Configure window settings
            configureWindow();

            // Start the application
            startApplication();

            logger.info("Application started successfully");

        } catch (Exception e) {
            handleStartupError(e);
        }
    }

    /**
     * Build the service provider with all registered services.
     */
    protected ServiceProvider buildServiceProvider() {
        ServiceCollection services = new ServiceCollection();

        // Register core services
        registerCoreServices(services);

        // Register application-specific services
        registerServices(services);

        return services.buildServiceProvider();
    }

    /**
     * Register core framework services (Router, Logger, etc.)
     */
    private void registerCoreServices(ServiceCollection services) {
        // Register logger
        services.registerSingleton(Logger.class, getLogger());

        // Register router
        services.registerSingleton(Router.class, (Function<ServiceProvider, Router>) sp -> {
            return new SwingRouter(getLogger(), sp) {
            };
        });
    }

    /**
     * Register application-specific services.
     * Override this method in your application.
     */
    protected abstract void registerServices(ServiceCollection services);

    /**
     * Configure window properties (title, size, etc.)
     * Override to customize window settings.
     */
    protected void configureWindow() {
        if (router instanceof SwingRouter) {
            SwingRouter swingRouter = (SwingRouter) router;
            JFrame window = swingRouter.getWindow();

            // Default configuration
            window.setTitle("My Swing Application");
            window.setSize(800, 600);
            window.setLocationRelativeTo(null); // Center on screen

            // You can add more window configuration here
            // window.setIconImage(...);
            // window.setExtendedState(...);
        }
    }

    /**
     * Start the application with initial navigation.
     * Override to set the initial view.
     */
    protected abstract void startApplication();

    /**
     * Initialize logging configuration.
     */
    protected void initializeLogging() {
        logger = Logger.getLogger(getClass().getName());

        // Set up uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.log(Level.SEVERE, "Uncaught exception in thread: " + thread.getName(), throwable);

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null,
                        "An unexpected error occurred: " + throwable.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            });
        });
    }

    /**
     * Handle startup errors.
     */
    protected void handleStartupError(Exception e) {
        if (logger != null) {
            logger.log(Level.SEVERE, "Failed to start application", e);
        } else {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
            );
        });

        System.exit(1);
    }

    /**
     * Get the service provider.
     */
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /**
     * Get the router.
     */
    public Router getRouter() {
        return router;
    }

    /**
     * Get the logger.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Helper method to navigate to a view.
     */
    protected void navigateTo(Class<? extends View> view) {
        router.navigateTo(view);
    }

    /**
     * Helper method to navigate to a view with parameters.
     */
    protected void navigateTo(Class<? extends View> view, java.util.Map<String, Object> params) {
        router.navigateTo(view, params);
    }

    /**
     * Helper method to go back.
     */
    protected void goBack() {
        router.goBack();
    }

    /**
     * Show the main window.
     */
    protected void showWindow() {
        if (router instanceof SwingRouter) {
            ((SwingRouter) router).getWindow().setVisible(true);
        }
    }

    /**
     * Exit the application.
     */
    public void exit() {
        logger.info("Application exiting");
        System.exit(0);
    }
}