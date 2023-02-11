package es.udc.ws.app.model.eventservice.exceptions;

public class ResponseDeadlineException extends Exception {
    private Long eventId;

    public ResponseDeadlineException(Long eventId) {
        super("Response for event with id=\"" + eventId + "\n cannot be made because the deadline has already passed.");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
