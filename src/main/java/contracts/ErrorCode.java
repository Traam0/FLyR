package contracts;

public enum ErrorCode {
    NOT_FOUND("ERR0404"),
    INVALID_CREDENTIALS("ERR0401"),
    INTERNAL_ERROR("ERR0500");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    private String getCode() {
        return this.code;
    }
}
