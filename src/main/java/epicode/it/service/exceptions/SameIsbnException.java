package epicode.it.service.exceptions;

public class SameIsbnException extends Exception {
    public SameIsbnException() {
    }

    public SameIsbnException(String message) {
        super(message);
    }
}
