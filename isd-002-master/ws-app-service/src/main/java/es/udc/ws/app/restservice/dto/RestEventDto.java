package es.udc.ws.app.restservice.dto;

public class RestEventDto {

    private Long eventId;

    private String name;

    private String description;

    private String celebrationDate;

    private short duration;

    private boolean cancelled;

    private int numSi;

    private int numResponses;

    public RestEventDto() {
    }

    public RestEventDto(Long eventId, String name, String description, String celebrationDate, short duration, boolean cancelled, int numSi, int numResponses) {

        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.celebrationDate = celebrationDate;
        this.duration = duration;
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

    public String getCelebrationDate() {
        return celebrationDate;
    }

    public void setCelebrationDate(String celebrationDate) {
        this.celebrationDate = celebrationDate;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
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
        return "RestEventDto{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", celebrationDate='" + celebrationDate + '\'' +
                ", duration=" + duration +
                ", cancelled=" + cancelled +
                ", numSi=" + numSi +
                ", numResponses=" + numResponses +
                '}';
    }
}