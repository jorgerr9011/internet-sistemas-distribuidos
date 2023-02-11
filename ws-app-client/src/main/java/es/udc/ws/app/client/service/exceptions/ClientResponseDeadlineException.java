package es.udc.ws.app.client.service.exceptions;

public class ClientResponseDeadlineException extends Exception {
    private Long eventId;

    public ClientResponseDeadlineException(Long eventId) {
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
