package core.dependencyInjection;

import java.util.ArrayList;
import java.util.function.Function;

public final class ServiceCollection extends ArrayList<ServiceDescriptor> {

    /**
     * Register a singleton service with an implementation class
     */
    public <T> ServiceCollection registerSingleton(Class<T> serviceType, Class<? extends T> implementationType) {
        this.add(ServiceDescriptor.Singleton(serviceType, implementationType));
        return this;
    }

    /**
     * Register a singleton service with a factory function
     */
    public <T> ServiceCollection registerSingleton(Class<T> serviceType, Function<ServiceProvider, T> factory) {
        this.add(ServiceDescriptor.Singleton(serviceType, factory));
        return this;
    }

    /**
     * Register a singleton service with an existing instance
     */
    public <T> ServiceCollection registerSingleton(Class<T> serviceType, T instance) {
        this.add(ServiceDescriptor.Singleton(serviceType, instance));
        return this;
    }

    /**
     * Register a scoped service with an implementation class
     */
    public <T> ServiceCollection registerScoped(Class<T> serviceType, Class<? extends T> implementationType) {
        this.add(ServiceDescriptor.Scoped(serviceType, implementationType));
        return this;
    }

    /**
     * Register a scoped service with a factory function
     */
    public <T> ServiceCollection registerScoped(Class<T> serviceType, Function<ServiceProvider, T> factory) {
        this.add(ServiceDescriptor.Scoped(serviceType, factory));
        return this;
    }

    /**
     * Register a transient service with an implementation class
     */
    public <T> ServiceCollection registerTransient(Class<T> serviceType, Class<? extends T> implementationType) {
        this.add(ServiceDescriptor.Transient(serviceType, implementationType));
        return this;
    }

    /**
     * Register a transient service with a factory function
     */
    public <T> ServiceCollection registerTransient(Class<T> serviceType, Function<ServiceProvider, T> factory) {
        this.add(ServiceDescriptor.Transient(serviceType, factory));
        return this;
    }

    /**
     * Build the service provider from this collection
     */
    public ServiceProvider buildServiceProvider() {
        return new IocContainer(this, true);
    }

    /**
     * Create a scoped service provider
     */
    public ServiceProvider createScope() {
        return new IocContainer(this, false);
    }
}