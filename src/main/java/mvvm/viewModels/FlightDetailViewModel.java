package mvvm.viewModels;


import contracts.wrappers.Resource;
import core.mvvm.Property;
import core.mvvm.PropertyFlags;
import mvvm.models.Flight;
import services.FlightsService;

import java.util.logging.Logger;

public class FlightDetailViewModel {
    private final FlightsService flightsService;
    private final Logger logger;
    public final Property<Resource<Flight>> flight;

    public FlightDetailViewModel(FlightsService flightsService, Logger logger) {
        this.flightsService = flightsService;
        this.logger = logger;
        this.flight = new Property<>(Resource.loading(), PropertyFlags.EQUALS_VALUE/*, PropertyFlags.REPLAY_LAST*/);

        this.loadData();
    }


    private void loadData(){

    }
}