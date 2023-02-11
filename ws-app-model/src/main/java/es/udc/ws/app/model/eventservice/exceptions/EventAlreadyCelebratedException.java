package es.udc.ws.app.model.eventservice.exceptions;

public class EventAlreadyCelebratedException extends Exception {

    private Long eventId;

    public EventAlreadyCelebratedException(Long eventId) {
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
