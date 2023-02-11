package es.udc.ws.app.model.response;

import java.time.LocalDateTime;
import java.util.Objects;

public class Response {

    private Long responseId;

    private Long eventId;

    private String userEmail;

    private LocalDateTime responseDate;

    private boolean attending;

    public Response(Long eventId, String userEmail, LocalDateTime responseDate, boolean attending) {
        this.eventId = eventId;
        this.userEmail = userEmail;
        this.responseDate = (responseDate != null) ? responseDate.withNano(0) : null;
        this.attending = attending;
    }

    public Response(Long responseId, Long eventId, String userEmail, LocalDateTime responseDate, boolean attending) {
        this(eventId, userEmail, responseDate, attending);
        this.responseId = responseId;
    }

    public Long getResponseId() {
        return responseId;
    }

    public void setResponseId(Long responseId) {
        this.responseId = responseId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public boolean isAttending() {
        return attending;
    }

    public void setAttending(boolean attending) {
        this.attending = attending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response = (Response) o;

        if (attending != response.attending) return false;
        if (!Objects.equals(responseId, response.responseId)) return false;
        if (!Objects.equals(eventId, response.eventId)) return false;
        if (!Objects.equals(userEmail, response.userEmail)) return false;
        return Objects.equals(responseDate, response.responseDate);
    }

    @Override
    public int hashCode() {
        int result = responseId != null ? responseId.hashCode() : 0;
        result = 31 * result + (eventId != null ? eventId.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + (responseDate != null ? responseDate.hashCode() : 0);
        result = 31 * result + (attending ? 1 : 0);
        return result;
    }
}
