package sarebApp.com.sareb.exception;
/**
 * @author Assem
 */
public class ApiGetException extends RuntimeException {
    public ApiGetException(String message) {
        super(message);
    }

    public ApiGetException(String message, Throwable cause) {
        super(message, cause);
    }
}
