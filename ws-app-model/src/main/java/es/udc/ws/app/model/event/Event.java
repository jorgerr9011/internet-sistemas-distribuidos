package es.udc.ws.app.model.event;

import java.time.LocalDateTime;

public class Event {

    private Long eventId;

    private String name;

    private String description;

    private LocalDateTime celebrationDate;

    private short duration;

    private LocalDateTime creationDate;

    private boolean cancelled;

    private int numSi;

    private int numNo;

    public Event(String name, String description, short duration, LocalDateTime celebrationDate, LocalDateTime creationDate) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.celebrationDate = celebrationDate;
        this.creationDate = creationDate;
        this.cancelled = false;
        this.numSi = 0;
        this.numNo = 0;
    }

    public Event(String name, String description, short duration, LocalDateTime celebrationDate, LocalDateTime creationDate, boolean cancelled) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.celebrationDate = celebrationDate;
        this.creationDate = creationDate;
        this.cancelled = cancelled;
        this.numSi = 0;
        this.numNo = 0;
    }

    public Event(Long eventId, String name, String description, short duration, LocalDateTime celebrationDate, boolean cancelled, int numSi, int numNo) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.celebrationDate = celebrationDate;
        this.cancelled = cancelled;
        this.numSi = numSi;
        this.numNo = numNo;
    }

    public Event(String name, String description, short duration, LocalDateTime celebrationDate, LocalDateTime creationDate, Long eventId) {
        this(name, description, duration, celebrationDate, creationDate, false);
        this.numSi = 0;
        this.numNo = 0;
        this.eventId = eventId;
    }

    public Event(String name, String description, short duration, LocalDateTime celebrationDate, LocalDateTime creationDate, boolean cancelled, Long eventId) {
        this(name, description, duration, celebrationDate, creationDate, cancelled);
        this.numSi = 0;
        this.numNo = 0;
        this.eventId = eventId;
    }

    public Event(String name, String description, short duration, LocalDateTime celebrationDate, LocalDateTime creationDate, boolean cancelled, int numSi, int numNo, Long eventId) {
        this(name, description, duration, celebrationDate, creationDate, cancelled);
        this.numSi = numSi;
        this.numNo = numNo;
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long id) {
        this.eventId = id;
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

    public LocalDateTime getCelebrationDate() {
        return celebrationDate;
    }

    public void setCelebrationDate(LocalDateTime celebrationDate) {
        this.celebrationDate = celebrationDate;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean isCancelled) {
        this.cancelled = isCancelled;
    }

    public boolean hasBeenCelebrated() {
        return this.getCelebrationDate().isBefore(LocalDateTime.now());
    }

    public int getNumSi() {
        return numSi;
    }

    public void setNumSi(int num) {
        this.numSi = num;
    }

    public void addNumSi() {
        this.numSi++;
    }

    public int getNumNo() {
        return numNo;
    }

    public void setNumNo(int num) {
        this.numNo = num;
    }

    public void addNumNo() {
        this.numNo++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (duration != event.duration) return false;
        if (!eventId.equals(event.eventId)) return false;
        if (!name.equals(event.name)) return false;
        if (!description.equals(event.description)) return false;

        return celebrationDate.equals(event.celebrationDate);
    }

    @Override
    public int hashCode() {
        int result;

        result = ((eventId == null) ? 0 : eventId.hashCode());

        result = 31 * result + ((name == null) ? 0 : name.hashCode());
        result = 31 * result + ((description == null) ? 0 : description.hashCode());
        result = 31 * result + ((celebrationDate == null) ? 0 : celebrationDate.hashCode());
        result = 31 * result + duration;

        return result;
    }
}
