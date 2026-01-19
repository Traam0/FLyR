package core.networking.exceptions;

public final class RestClientException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public RestClientException(String message) {
        super(message);
        this.statusCode = 0;
        this.responseBody = null;
    }

    public RestClientException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.responseBody = null;
    }

    public RestClientException(String message, int statusCode, String responseBody) {
        super(message + " (Status: " + statusCode + ")");
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}