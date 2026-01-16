package core.dependencyInjection;

import java.util.function.Function;

public class ServiceDescriptor {
    private final Class<?> serviceType;
    private final Class<?> implementationType;
    private final Function<ServiceProvider, ?> factory;
    private final Object instance;
    private final ServiceLifetime scope;

    private ServiceDescriptor(Class<?> serviceType, Class<?> implementationType,
                              Function<ServiceProvider, ?> factory, Object instance,
                              ServiceLifetime scope) {
        this.serviceType = serviceType;
        this.implementationType = implementationType;
        this.factory = factory;
        this.instance = instance;
        this.scope = scope;
    }

    // Singleton registrations
    public static <T> ServiceDescriptor Singleton(Class<T> serviceType, Class<? extends T> implementationType) {
        return new ServiceDescriptor(serviceType, implementationType, null, null, ServiceLifetime.Singleton);
    }

    public static <T> ServiceDescriptor Singleton(Class<T> serviceType, Function<ServiceProvider, T> factory) {
        return new ServiceDescriptor(serviceType, null, factory, null, ServiceLifetime.Singleton);
    }

    public static <T> ServiceDescriptor Singleton(Class<T> serviceType, T instance) {
        return new ServiceDescriptor(serviceType, null, null, instance, ServiceLifetime.Singleton);
    }

    // Scoped registrations
    public static <T> ServiceDescriptor Scoped(Class<T> serviceType, Class<? extends T> implementationType) {
        return new ServiceDescriptor(serviceType, implementationType, null, null, ServiceLifetime.Scoped);
    }

    public static <T> ServiceDescriptor Scoped(Class<T> serviceType, Function<ServiceProvider, T> factory) {
        return new ServiceDescriptor(serviceType, null, factory, null, ServiceLifetime.Scoped);
    }


    public static <T> ServiceDescriptor Transient(Class<T> serviceType, Class<? extends T> implementationType) {
        return new ServiceDescriptor(serviceType, implementationType, null, null, ServiceLifetime.Transient);
    }

    public static <T> ServiceDescriptor Transient(Class<T> serviceType, Function<ServiceProvider, T> factory) {
        return new ServiceDescriptor(serviceType, null, factory, null, ServiceLifetime.Transient);
    }

    public Class<?> getServiceType() { return serviceType; }
    public Class<?> getImplementationType() { return implementationType; }
    public Function<ServiceProvider, ?> getFactory() { return factory; }
    public Object getInstance() { return instance; }
    public ServiceLifetime getScope() { return scope; }

    public boolean hasFactory() { return factory != null; }
    public boolean hasInstance() { return instance != null; }
    public boolean hasImplementationType() { return implementationType != null; }
}
