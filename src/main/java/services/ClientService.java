package services;

import contracts.wrappers.Resource;
import core.networking.HttpRestClient;
import core.networking.HttpRestClientFactory;
import mvvm.models.Client;
import mvvm.models.Flight;

import java.util.logging.Logger;

public class ClientService {
    private final HttpRestClient httpClient;
    private final Logger logger;

    public ClientService(HttpRestClientFactory httpClientFactory, Logger logger) {
        this.httpClient = httpClientFactory.createClient();
        this.logger = logger;
    }

    public Resource<Client> getClient(int id) {
        try {
            var response = this.httpClient.getWithApiResponse(String.format("/clients/%d", id), Client.class);
            if (response.isSuccess()) return Resource.success(response.getData());
            return Resource.error(response.getMessage());
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
            return Resource.error(e.getMessage());
        }
    }
}
