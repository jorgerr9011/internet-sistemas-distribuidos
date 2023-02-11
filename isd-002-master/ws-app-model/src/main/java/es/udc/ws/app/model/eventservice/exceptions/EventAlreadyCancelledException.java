package es.udc.ws.app.model.eventservice.exceptions;

public class EventAlreadyCancelledException extends Exception {

    private Long eventId;

    public EventAlreadyCancelledException(Long eventId) {
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
