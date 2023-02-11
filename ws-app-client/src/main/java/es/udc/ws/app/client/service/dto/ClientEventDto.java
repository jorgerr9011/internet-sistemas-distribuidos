package es.udc.ws.app.client.service.dto;

import java.time.LocalDateTime;

public class ClientEventDto {

    private Long eventId;

    private String name;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean cancelled;

    private int numSi;

    private int numResponses;

    public ClientEventDto() {

    }

    public ClientEventDto(Long eventId, String name, String description, LocalDateTime startDate, LocalDateTime endDate,
                          boolean cancelled, int numSi, int numResponses) {

        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cancelled = cancelled;
        this.numSi = numSi;
        this.numResponses = numResponses;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public int getNumSi() {
        return numSi;
    }

    public void setNumSi(int numSi) {
        this.numSi = numSi;
    }

    public int getNumResponses() {
        return numResponses;
    }

    public void setNumResponses(int numResponses) {
        this.numResponses = numResponses;
    }

    @Override
    public String toString() {
        return "ClientEventDto{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", cancelled=" + cancelled +
                ", numSi=" + numSi +
                ", numResponses=" + numResponses +
                '}';
    }
}
