package core.networking;

import java.util.Map;

public interface HttpRestClient {
    <T> T get(String path, Class<T> responseType);
    <T> T get(String path, Map<String, Object> queryParams, Class<T> responseType);

    <T> T post(String path, Object body, Class<T> responseType);
    <T> T post(String path, Object body, Map<String, Object> queryParams, Class<T> responseType);

    <T> T put(String path, Object body, Class<T> responseType);
    <T> T put(String path, Object body, Map<String, Object> queryParams, Class<T> responseType);

    <T> T delete(String path, Class<T> responseType);
    <T> T delete(String path, Map<String, Object> queryParams, Class<T> responseType);

    <T> ApiResponse<T> getWithApiResponse(String path, Class<T> dataType);

    void setBaseUrl(String baseUrl);
    void setAuthToken(String token);
    void clearAuthToken();

    void addDefaultHeader(String name, String value);
    void removeDefaultHeader(String name);
}