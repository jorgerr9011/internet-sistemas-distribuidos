package es.udc.ws.app.restservice.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.udc.ws.app.model.event.Event;

public class EventToRestEventDtoConversor {

    public static List<RestEventDto> toRestEventDtos(List<Event> events) {
        List<RestEventDto> eventDtos = new ArrayList<>(events.size());

        for (Event event : events) {
            eventDtos.add(toRestEventDto(event));
        }

        return eventDtos;
    }

    public static RestEventDto toRestEventDto(Event event) {
        return new RestEventDto(event.getEventId(), event.getName(), event.getDescription(), event.getCelebrationDate().toString(),
                event.getDuration(), event.isCancelled(), event.getNumSi(), event.getNumNo() + event.getNumSi());
    }

    public static Event toEvent(RestEventDto event) {
        return new Event(event.getEventId(), event.getName(), (event.getDescription()), event.getDuration(), LocalDateTime.parse(event.getCelebrationDate()), event.isCancelled(), event.getNumSi(), event.getNumResponses() - event.getNumSi());
    }
}