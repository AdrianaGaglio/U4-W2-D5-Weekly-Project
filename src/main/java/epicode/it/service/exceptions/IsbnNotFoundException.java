package epicode.it.service.exceptions;

public class IsbnNotFoundException extends Exception {
    public IsbnNotFoundException() {
    }

    public IsbnNotFoundException(String message) {
        super(message);
    }
}
