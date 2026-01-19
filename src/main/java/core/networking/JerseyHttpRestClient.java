package core.networking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import core.networking.exceptions.RestClientException;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class JerseyHttpRestClient implements HttpRestClient {
    private static final Logger logger = Logger.getLogger(JerseyHttpRestClient.class.getName());

    // Static ObjectMapper instance (thread-safe)
    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = createDefaultObjectMapper();

    private final Client client;
    final ObjectMapper objectMapper;
    private String baseUrl;
    private String authToken;
    private final Map<String, String> defaultHeaders;

    // Factory method for creating configured client
    public static JerseyHttpRestClient create(String baseUrl) {
        return new JerseyHttpRestClient(baseUrl, DEFAULT_OBJECT_MAPPER);
    }

    // Factory method with custom configuration
    public static JerseyHttpRestClient create(String baseUrl, ObjectMapper objectMapper) {
        return new JerseyHttpRestClient(baseUrl, objectMapper);
    }

    /**
     * Private constructor - use factory methods
     */
    private JerseyHttpRestClient(String baseUrl, ObjectMapper objectMapper) {
        this.baseUrl = sanitizeBaseUrl(baseUrl);
        this.objectMapper = objectMapper;
        this.defaultHeaders = new HashMap<>();

        // Create Jersey client with reasonable defaults
        this.client = ClientBuilder.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        // Register Jackson for JSON processing
        this.client.register(org.glassfish.jersey.jackson.JacksonFeature.class);

        // Set default headers
        this.defaultHeaders.put("Accept", MediaType.APPLICATION_JSON);
        this.defaultHeaders.put("Content-Type", MediaType.APPLICATION_JSON);
    }

    private static ObjectMapper createDefaultObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    private String sanitizeBaseUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null or empty");
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    @Override
    public <T> T get(String path, Class<T> responseType) {
        return get(path, null, responseType);
    }

    @Override
    public <T> T get(String path, Map<String, Object> queryParams, Class<T> responseType) {
        try {
            WebTarget target = buildTarget(path, queryParams);
            Response response = buildRequest(target).get();
            return handleResponse(response, responseType);
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            throw new RestClientException("GET request failed for path: " + path, e);
        }
    }

    @Override
    public <T> T post(String path, Object body, Class<T> responseType) {
        return post(path, body, null, responseType);
    }

    @Override
    public <T> T post(String path, Object body, Map<String, Object> queryParams, Class<T> responseType) {
        try {
            WebTarget target = buildTarget(path, queryParams);
            String jsonBody = serializeToJson(body);
            Response response = buildRequest(target)
                    .post(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));
            return handleResponse(response, responseType);
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            throw new RestClientException("POST request failed for path: " + path, e);
        }
    }

    @Override
    public <T> T put(String path, Object body, Class<T> responseType) {
        return put(path, body, null, responseType);
    }

    @Override
    public <T> T put(String path, Object body, Map<String, Object> queryParams, Class<T> responseType) {
        try {
            WebTarget target = buildTarget(path, queryParams);
            String jsonBody = serializeToJson(body);
            Response response = buildRequest(target)
                    .put(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));
            return handleResponse(response, responseType);
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            throw new RestClientException("PUT request failed for path: " + path, e);
        }
    }

    @Override
    public <T> T delete(String path, Class<T> responseType) {
        return delete(path, null, responseType);
    }

    @Override
    public <T> T delete(String path, Map<String, Object> queryParams, Class<T> responseType) {
        try {
            WebTarget target = buildTarget(path, queryParams);
            Response response = buildRequest(target).delete();
            return handleResponse(response, responseType);
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            throw new RestClientException("DELETE request failed for path: " + path, e);
        }
    }

    @Override
    public <T> ApiResponse<T> getWithApiResponse(String path, Class<T> dataType) {
        try {
            String fullPath = baseUrl + path;
            logger.fine("Making GET request to: " + fullPath);

            WebTarget target = client.target(fullPath);
            Response response = buildRequest(target).get();

            String responseBody = response.readEntity(String.class);
            logger.fine("Response status: " + response.getStatus());
            logger.finest("Response body: " + responseBody);

            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                // Try to parse as ApiResponse
                try {
                    return objectMapper.readValue(responseBody,
                            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, dataType));
                } catch (JsonProcessingException e) {
                    // If not ApiResponse, wrap the data
                    T data = objectMapper.readValue(responseBody, dataType);
                    return ApiResponse.success(data);
                }
            } else {
                throw new RestClientException("API request failed",
                        response.getStatus(), responseBody);
            }
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            throw new RestClientException("Failed to make API request to: " + path, e);
        }
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = sanitizeBaseUrl(baseUrl);
    }

    @Override
    public void setAuthToken(String token) {
        this.authToken = token;
        if (token != null) {
            defaultHeaders.put("Authorization", "Bearer " + token);
        } else {
            defaultHeaders.remove("Authorization");
        }
    }

    @Override
    public void clearAuthToken() {
        this.authToken = null;
        defaultHeaders.remove("Authorization");
    }

    @Override
    public void addDefaultHeader(String name, String value) {
        defaultHeaders.put(name, value);
    }

    @Override
    public void removeDefaultHeader(String name) {
        defaultHeaders.remove(name);
    }

    // Private helper methods
    private WebTarget buildTarget(String path, Map<String, Object> queryParams) {
        String fullPath = baseUrl + path;
        logger.fine("Building request to: " + fullPath);

        WebTarget target = client.target(fullPath);

        if (queryParams != null) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                if (entry.getValue() != null) {
                    target = target.queryParam(entry.getKey(), entry.getValue());
                }
            }
        }

        return target;
    }

    private Invocation.Builder buildRequest(WebTarget target) {
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);

        // Add default headers
        for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }

        return builder;
    }

    private <T> T handleResponse(Response response, Class<T> responseType) {
        int status = response.getStatus();
        String responseBody = response.readEntity(String.class);

        logger.fine("Response status: " + status);
        logger.finest("Response body: " + responseBody);

        if (status >= 200 && status < 300) {
            try {
                if (responseType == String.class) {
                    return responseType.cast(responseBody);
                } else if (responseType == Void.class || responseType == void.class) {
                    return null;
                } else {
                    return objectMapper.readValue(responseBody, responseType);
                }
            } catch (JsonProcessingException e) {
                throw new RestClientException("Failed to parse response", e);
            }
        } else {
            throw new RestClientException("HTTP request failed", status, responseBody);
        }
    }

    private String serializeToJson(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RestClientException("Failed to serialize request body", e);
        }
    }

    // Cleanup method
    public void close() {
        if (client != null) {
            client.close();
            logger.fine("HttpRestClient closed");
        }
    }
}