package es.udc.ws.app.client.service.exceptions;

public class ClientEventAlreadyCelebratedException extends Exception {

    private Long eventId;

    public ClientEventAlreadyCelebratedException(Long eventId) {
        super("Event with id=\"" + eventId + "\n cannot be created or cancelled because it has already been celebrated");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
