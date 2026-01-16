package core.dependencyInjection;
import core.dependencyInjection.ServiceLifetime;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public final class IocContainer implements ServiceProvider {
    private final List<ServiceDescriptor> services;
    private final Map<Class<?>, Object> singletonServices;
    private final Map<Class<?>, Object> scopedServices;
    private final boolean isRoot;

    public IocContainer(ServiceCollection services, boolean isRoot) {
        this.services = new ArrayList<>(services);
        this.singletonServices = new ConcurrentHashMap<>();
        this.scopedServices = new ConcurrentHashMap<>();
        this.isRoot = isRoot;

        // Pre-create singleton instances
        initializeSingletons();
    }

    private void initializeSingletons() {
        for (ServiceDescriptor descriptor : services) {
            if (descriptor.getScope() == ServiceLifetime.Singleton && !singletonServices.containsKey(descriptor.getServiceType())) {
                Object instance = createInstance(descriptor);
                singletonServices.put(descriptor.getServiceType(), instance);
            }
        }
    }

    @Override
    public <T> T getService(Class<? extends T> type) {
        ServiceDescriptor descriptor = findServiceDescriptor(type);
        return descriptor == null ? null : resolveService(descriptor);    }

    @Override
    public <T> T getRequiredService(Class<? extends T> type) {
        ServiceDescriptor descriptor = findServiceDescriptor(type);
        if (descriptor == null) {
            throw new RuntimeException("Service not registered: " + type.getName());
        }
        return resolveService(descriptor);    }

    private ServiceDescriptor findServiceDescriptor(Class<?> type) {
        return services.stream()
                .filter(descriptor -> descriptor.getServiceType().equals(type))
                .findFirst()
                .orElse(null);
    }

    private <T> T resolveService(ServiceDescriptor descriptor) {
        return switch (descriptor.getScope()) {
            case Singleton -> (T) resolveSingleton(descriptor);
            case Scoped -> (T) resolveScoped(descriptor);
            case Transient -> (T) createInstance(descriptor);
        };
    }

    private Object resolveSingleton(ServiceDescriptor descriptor) {
        return singletonServices.computeIfAbsent(descriptor.getServiceType(),
                k -> createInstance(descriptor));
    }

    private Object resolveScoped(ServiceDescriptor descriptor) {
        if (isRoot) {
            throw new IllegalStateException("Cannot resolve scoped service from root container");
        }
        return scopedServices.computeIfAbsent(descriptor.getServiceType(),
                k -> createInstance(descriptor));
    }

    private Object createInstance(ServiceDescriptor descriptor) {
        if (descriptor.hasInstance()) {
            return descriptor.getInstance();
        }

        if (descriptor.hasFactory()) {
            return descriptor.getFactory().apply(this);
        }

        if (descriptor.hasImplementationType()) {
            return createInstanceFromType(descriptor.getImplementationType());
        }

        throw new RuntimeException("Invalid service descriptor: " + descriptor.getServiceType().getName());
    }

    private <T> T createInstanceFromType(Class<T> implementationType) {
        try {
            Constructor<T> constructor = (Constructor<T>) Arrays.stream(implementationType.getConstructors())
                    .max(Comparator.comparing(Constructor::getParameterCount))
                    .orElseThrow(() -> new RuntimeException("No public constructors found for " + implementationType.getName()));

            Object[] parameters = Arrays.stream(constructor.getParameterTypes())
                    .map(this::getRequiredService)
                    .toArray();

            return constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + implementationType.getName(), e);
        }
    }
}
