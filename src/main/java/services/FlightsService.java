package services;

import contracts.FlightData;
import contracts.requests.FlightSearchFilter;
import contracts.wrappers.Resource;
import core.networking.HttpRestClient;
import core.networking.HttpRestClientFactory;
import jakarta.ws.rs.core.Response;
import mvvm.models.Flight;

import java.util.List;
import java.util.logging.Logger;

public class FlightsService {
    private final HttpRestClient httpClient;
    private final Logger logger;

    public FlightsService(HttpRestClientFactory httpClientFactory, Logger logger) {
        this.logger = logger;
        this.httpClient = httpClientFactory.createClient();
    }

    public Resource<FlightData[]> getFlights() {
        try {
            var response = this.httpClient.getWithApiResponse("/flights", FlightData[].class);
            if (response.isSuccess()) return Resource.success(response.getData());
            return Resource.error(response.getMessage());
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
            return Resource.error(e.getMessage());
        }
    }

    public Resource<FlightData[]> filterFlights(FlightSearchFilter filter) {
        try {
            var response = this.httpClient.getWithApiResponse(String.format("/flights/search?%s", filter), FlightData[].class);
            if (response.isSuccess()) return Resource.success(response.getData());
            return Resource.error(response.getMessage());
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
            return Resource.error(e.getMessage());
        }
    }

    public Resource<Flight> getFlight(int id) {
        try {
            var response = this.httpClient.getWithApiResponse(String.format("/flights/%s/details", id), Flight.class);
            if (response.isSuccess()) return Resource.success(response.getData());
            return Resource.error(response.getMessage());
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
            return Resource.error(e.getMessage());
        }
    }
}
