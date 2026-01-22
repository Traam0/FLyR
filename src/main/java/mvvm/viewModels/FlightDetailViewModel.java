package mvvm.viewModels;


import contracts.wrappers.Resource;
import core.mvvm.IterableProperty;
import core.mvvm.Property;
import core.mvvm.PropertyFlags;
import core.navigation.Router;
import mvvm.models.Flight;
import mvvm.models.Seat;
import mvvm.models.SeatClass;
import services.FlightsService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FlightDetailViewModel {
    private final FlightsService flightsService;
    private final Logger logger;
    private final Router router;
    public final Property<Resource<Flight>> flight;
    public final Property<String> selectedSeatClass;
    public final IterableProperty<Seat> selectedSeats;

    public FlightDetailViewModel(FlightsService flightsService, Logger logger, Router router) {
        this.flightsService = flightsService;
        this.logger = logger;
        this.router = router;
        this.flight = new Property<>(Resource.loading(), PropertyFlags.DISTINCT_VALUE);
        this.selectedSeatClass = new Property<>("ALL SEATS", PropertyFlags.EQUALS_VALUE);
        this.selectedSeats = new IterableProperty<>(PropertyFlags.DISTINCT_VALUE);

    }


    public void loadData() {
        new Thread(() -> {
            var response = this.flightsService.getFlight((Integer) this.router.getParams().get("id"));
            this.logger.info(String.format("Setting flight Property to %s state with data  %s", response.getStatus().name(), response.getData() == null ? "no" : "yes"));
            this.flight.set(response);
        }).start();
    }
}