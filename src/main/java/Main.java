import core.abstraction.SwingApp;
import core.dependencyInjection.ServiceCollection;
import core.dependencyInjection.ServiceProvider;

public class Main extends SwingApp {

    public static void main(String[] args) {
//        ServiceCollection serviceCollection = new ServiceCollection();
//        serviceCollection.registerSingleton(TestWrapper.class, TestWrapper.class);
//        serviceCollection.registerTransient(Example.class, Example.class);
//
//        ServiceProvider provider = serviceCollection.buildServiceProvider();
//
//        var cl = provider.getRequiredService(TestWrapper.class);
        var app = new Main();
        
    }
}