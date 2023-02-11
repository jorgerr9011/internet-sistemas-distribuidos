package es.udc.ws.app.client.service.dto;

public class ClientResponseDto {
    private Long responseId;

    private Long eventId;

    private String userEmail;

    private boolean attending;

    public ClientResponseDto() {
    }

    public ClientResponseDto(Long responseId, Long eventId, String userEmail, boolean attending) {
        this.responseId = responseId;
        this.eventId = eventId;
        this.userEmail = userEmail;
        this.attending = attending;
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

    public boolean isAttending() {
        return this.attending;
    }

    public void setUserEmailB(boolean attending) {
        this.attending = attending;
    }
}
