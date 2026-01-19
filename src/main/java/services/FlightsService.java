package services;

import contracts.FlightData;
import contracts.wrappers.Resource;
import core.networking.HttpRestClient;
import core.networking.HttpRestClientFactory;

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
            var flights = this.httpClient.getWithApiResponse("/flights", FlightData[].class);
            if (flights.isSuccess()) return Resource.success(flights.getData());
            return Resource.error(flights.getMessage());
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
            return Resource.error(e.getMessage());
        }
    }
}
