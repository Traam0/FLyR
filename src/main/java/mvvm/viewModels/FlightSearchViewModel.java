package mvvm.viewModels;

import contracts.wrappers.Resource;
import core.mvvm.Property;
import core.networking.HttpRestClient;
import core.networking.HttpRestClientFactory;
import services.FlightsService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightSearchViewModel {
    public Property<Boolean> isLoading = new Property<>(true);

    public FlightSearchViewModel(FlightsService flightsService, Logger logger) {
      new Thread(() -> {
          try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
          var result = flightsService.getFlights();
          switch (result.getStatus()) {
              case SUCCESS:
                  logger.log(Level.INFO, "Successfully loaded Flights Data");
                  logger.info("data length: " + result.getData().length);
                  this.isLoading.set(false);
                  break;
              case ERROR:
                  logger.log(Level.SEVERE, "Error loading Flights Data " + result.getMessage() );
                  this.isLoading.set(false);
                  break;
              default:
                  logger.log(Level.WARNING, "Unknown Flights Data Status or loading");
          }
      }).start();
    }
}
