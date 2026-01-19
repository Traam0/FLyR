package core.networking;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRestClientFactory {
    private final String defaultBaseUrl;
    private final ObjectMapper objectMapper;

    public HttpRestClientFactory(String defaultBaseUrl) {
        this(defaultBaseUrl, JerseyHttpRestClient.create(defaultBaseUrl).objectMapper);
    }

    public HttpRestClientFactory(String defaultBaseUrl, ObjectMapper objectMapper) {
        this.defaultBaseUrl = defaultBaseUrl;
        this.objectMapper = objectMapper;
    }

    public HttpRestClient createClient() {
        return JerseyHttpRestClient.create(defaultBaseUrl, objectMapper);
    }

    public HttpRestClient createClient(String baseUrl) {
        return JerseyHttpRestClient.create(baseUrl, objectMapper);
    }

    public HttpRestClient createClientWithAuth(String authToken) {
        HttpRestClient client = createClient();
        client.setAuthToken(authToken);
        return client;
    }
}