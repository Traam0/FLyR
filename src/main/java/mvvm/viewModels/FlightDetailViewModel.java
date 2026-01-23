package mvvm.viewModels;

import contracts.soap.BookingException;
import contracts.wrappers.Resource;
import core.mvvm.*;
import core.navigation.Router;
import mvvm.models.Client;
import mvvm.models.Flight;
import mvvm.models.Seat;
import mvvm.views.reservations.ReservationsView;
import networking.SoapProvider;
import services.ClientService;
import services.FlightsService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightDetailViewModel {
    private final FlightsService flightsService;
    private final ClientService clientService;
    private final Logger logger;
    private final Router router;
    public final Property<Resource<Flight>> flight;
    public final Property<String> selectedSeatClass;
    public final IterableProperty<Seat> selectedSeats;
    public final Property<Resource<Client>> client;
    private final Command bookCommand;

    public FlightDetailViewModel(FlightsService flightsService, ClientService clientService, Logger logger, Router router) {
        this.flightsService = flightsService;
        this.clientService = clientService;
        this.logger = logger;
        this.router = router;
        this.flight = new Property<>(Resource.loading(), PropertyFlags.DISTINCT_VALUE);
        this.selectedSeatClass = new Property<>("ALL SEATS", PropertyFlags.EQUALS_VALUE);
        this.selectedSeats = new IterableProperty<>(PropertyFlags.DISTINCT_VALUE);
        this.client = new Property<>(Resource.loading(), PropertyFlags.NONE);
        this.bookCommand = new RelayCommand(arg -> {
            for (var seat : selectedSeats.get()) {
                try {
                    SoapProvider.getBookingSoapService().bookSeat(client.get().getData().getId(), this.flight.get().getData().getId(), seat.getId());
                } catch (BookingException e) {
                    logger.log(Level.WARNING, e.getMessage());
                }
            }

            router.navigateTo(ReservationsView.class);
        }, arg -> !this.selectedSeats.get().isEmpty());
    }


    public void loadData() {
        new Thread(() -> {
            var response = this.flightsService.getFlight((Integer) this.router.getParams().get("id"));
            this.logger.info(String.format("Setting flight Property to %s state with data  %s", response.getStatus().name(), response.getData() == null ? "no" : "yes"));
            this.flight.set(response);
        }).start();
    }

    public void loadClient() {
        new Thread(() -> {
            var response = this.clientService.getClient((Integer) this.router.getParams().get("id"));
            if (response.getStatus() == Resource.Status.SUCCESS) {
                this.logger.info(response.getData().getFirstName() + " " + response.getData().getLastName());
            }
            this.client.set(response);
        }).start();
    }


    public Command  getBookCommand() {
        return this.bookCommand;
    }

}