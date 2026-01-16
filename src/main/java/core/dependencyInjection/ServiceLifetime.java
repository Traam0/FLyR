package core.dependencyInjection;

/**
 * Defines the lifetime of a service registered within the IoC container.
 * <p>
 * The {@code ServiceScope} enum determines how and when service instances
 * are created and reused.
 * </p>
 */
public enum ServiceLifetime {

    /**
     * A single instance is created and shared throughout the lifetime of the application
     */
    Singleton,

    /**
     * A new instance is created once per defined scope (e.g., per request,
     * per session, or per unit of work), and disposed of when the scope ends.
     */
    Scoped,

    /**
     * A new instance is created every time the service is requested.
     */
    Transient
}
