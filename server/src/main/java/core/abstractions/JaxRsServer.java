package core.abstractions;

import core.database.ConnectionPool;
import core.dependencyInjection.ServiceCollection;
import core.dependencyInjection.ServiceProvider;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JaxRsServer {
    private ServiceProvider serviceProvider;
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
    protected abstract void startApplication() throws IOException;


    /**
     * Main entry point to run the application.
     * Call this from your main method.
     */
    public void run() {
        try {
            // Initialize logging
            initializeLogging();
            serviceProvider = buildServiceProvider();
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
        services.registerSingleton(Properties.class, (Function<ServiceProvider, Properties>) sp -> {
            Properties properties = new Properties();
            try {
                properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return properties;
        });
        services.registerSingleton(ConnectionPool.class, (Function<ServiceProvider, ConnectionPool>) sp -> {
            try {
                return new ConnectionPool(15, sp.getRequiredService(Properties.class).getProperty("db.connectionString"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
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
        System.exit(1);
    }

    /**
     * Get the service provider.
     */
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /**
     * Get the logger.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Exit the application.
     */
    public void exit() {
        logger.info("Application exiting");
        System.exit(0);
    }
}
