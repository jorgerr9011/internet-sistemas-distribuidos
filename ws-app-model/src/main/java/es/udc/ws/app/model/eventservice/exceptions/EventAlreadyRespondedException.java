package es.udc.ws.app.model.eventservice.exceptions;

public class EventAlreadyRespondedException extends Exception {

    private Long eventId;

    private String email;

    public EventAlreadyRespondedException(Long eventId, String email) {
        super(email + " already responded to Event with id=\"" + eventId + "\"");
        this.eventId = eventId;
        this.email = email;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
