package epicode.it.service.exceptions;

public class PublicationNotFoundException extends Exception{
    public PublicationNotFoundException() {
    }

    public PublicationNotFoundException(String message) {
        super(message);
    }
}
