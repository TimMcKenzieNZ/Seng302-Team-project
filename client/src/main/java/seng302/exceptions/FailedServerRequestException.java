package seng302.exceptions;

public class FailedServerRequestException extends Exception {

    private int code;

    public FailedServerRequestException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}