package mvvm.viewModels;

import contracts.FlightData;
import contracts.requests.FlightSearchFilter;
import contracts.wrappers.Resource;
import core.mvvm.Command;
import core.mvvm.Property;
import core.mvvm.RelayCommand;
import services.FlightsService;

import java.util.logging.Logger;

public class FlightSearchViewModel {

    public final Property<String> departureCity;
    public final Property<String> arrivalCity;
    public final Property<String> departureDate;
    public final Property<String> arrivalDate;
    public final Property<Integer> passengerCount;
    public final Property<Boolean> isRoundTrip;
    public final Property<Resource<FlightData[]>> flights;
    private final FlightsService flightsService;
    private final RelayCommand clearFormCommand;
    private final RelayCommand searchCommand;

    public FlightSearchViewModel(FlightsService flightsService) {
        // Properties
        this.flightsService = flightsService;
        this.departureCity = new Property<>("");
        this.arrivalCity = new Property<>("");
        this.departureDate = new Property<>("");
        this.arrivalDate = new Property<>("");
        this.passengerCount = new Property<>(0);
        this.isRoundTrip = new Property<>(false);
        this.flights = new Property<>(Resource.loading());

        // Commands
        this.clearFormCommand = new RelayCommand(param -> {
            this.departureCity.set("");
            this.arrivalCity.set("");
            this.departureDate.set("");
            this.arrivalDate.set("");
            this.passengerCount.set(1);
            this.isRoundTrip.set(false);
        }, param -> true);

        this.searchCommand = new RelayCommand(param -> {
            this.flights.set(Resource.loading());
            new Thread(() -> {
                var result = this.flightsService.filterFlights(new FlightSearchFilter(departureCity.get(), arrivalCity.get(), departureDate.get(), arrivalDate.get(), passengerCount.get(), isRoundTrip.get()));
                this.flights.set(result);
            }).start();
        }, param -> true);
        this.loadData();
    }

    private void loadData() {
        new Thread(() -> {
            var result = this.flightsService.getFlights();
            this.flights.set(result);
        }).start();
    }

    public Command getClearFormCommand() {
        return this.clearFormCommand;
    }

    public Command getSearchCommand() {
        return this.searchCommand;
    }

}
