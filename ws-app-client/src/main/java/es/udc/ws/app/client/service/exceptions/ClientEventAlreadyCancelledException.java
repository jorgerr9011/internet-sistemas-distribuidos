package es.udc.ws.app.client.service.exceptions;

public class ClientEventAlreadyCancelledException extends Exception {

    private Long eventId;

    public ClientEventAlreadyCancelledException(Long eventId) {
        super("Event with id=\"" + eventId + "\n cannot be responded or cancelled because it has already been cancelled");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
