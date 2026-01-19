package contracts.wrappers;


import jakarta.annotation.Nonnull;

public class Resource<T> {
    private Status status;
    private T data;
    private String message;

    private Resource() {
        this.status = Status.LOADING;
    }

    private Resource(String message, T data) {
        this();
        this.message = message;
        this.data = data;
    }

    private Resource(Status status, String message, T data) {
        this(message, data);
        this.status = status;
    }

    public static <T> Resource<T> loading() {
        return new Resource<>();
    }

    public static <T> Resource<T> success(@Nonnull T data) {
        return new Resource<>(Status.SUCCESS, null, data);
    }

    public static <T> Resource<T> error(@Nonnull String message) {
        return new Resource<>(Status.ERROR, message, null);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public enum Status {LOADING, SUCCESS, ERROR}
}
