package core.abstraction;

import core.dependencyInjection.ServiceCollection;
import core.dependencyInjection.ServiceProvider;
import core.mvvm.View;
import core.navigation.Router;
import core.security.AuthContext;

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
     * Register application-specific services.
     * Override this method in your application.
     */
    protected abstract void registerServices(ServiceCollection services);

    /**
     * Start the application with initial navigation.
     * Override to set the initial view.
     */
    protected abstract void startApplication();


    /**
     * Main entry point to run the application.
     * Call this from your main method.
     */
    public void run() {
        try {
            // Initialize logging
            initializeLogging();
            serviceProvider = buildServiceProvider();
            this.router = serviceProvider.getRequiredService(Router.class);
            this.configureWindow();
            this.startApplication();
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

        registerServices(services);

        return services.createScope();
    }

    /**
     * Register core framework services (Router, Logger, etc.)
     */
    private void registerCoreServices(ServiceCollection services) {
        services.registerSingleton(Logger.class, this.getLogger());
        services.registerSingleton(AuthContext.class, AuthContext.class);
        services.registerSingleton(Router.class, (Function<ServiceProvider, Router>) sp -> {
            return new SwingRouter(this.getLogger(), sp) {
            };
        });
    }

    /**
     * Configure window properties (title, size, etc.)
     * Override to customize window settings.
     */
    protected void configureWindow() {
        if (router instanceof SwingRouter swingRouter) {
            JFrame window = swingRouter.getWindow();
            window.setTitle("My Swing Application");
            window.setSize(800, 600);
            window.setLocationRelativeTo(null);
        }
    }

    /**
     * Initialize logging configuration.
     */
    protected void initializeLogging() {
        this.logger = Logger.getLogger(this.getClass().getName());

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
     * Show the main window.
     */
    protected void showWindow(Class<? extends View> initialView) {
        if (router instanceof SwingRouter r) {
            r.getWindow().setVisible(true);
            r.navigateTo(initialView);
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