package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.thrift.ThriftEventDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventToThriftEventDtoConversor {

    public static Event toEvent(ThriftEventDto evento) {

        LocalDateTime celebrationDate = LocalDateTime.parse(evento.getCelebrationDate());

        return new Event(evento.getEventId(), evento.getName(), evento.getDescription(), evento.getDuration(), celebrationDate,
                evento.isCancelled(), evento.getNumSi(), evento.getNumNo());
    }

    public static List<ThriftEventDto> toThriftEventDtos(List<Event> events){

        List<ThriftEventDto> dtos = new ArrayList<>(events.size());

        for(Event evento : events){
            dtos.add(toThriftEventDto(evento));
        }
        return dtos;
    }

    public static ThriftEventDto toThriftEventDto(Event event){

        String date = String.valueOf(event.getCelebrationDate());

        return new ThriftEventDto(event.getEventId(), event.getName(), event.getDescription(), event.getDuration(),
                date, event.isCancelled(), event.getNumSi(),
                event.getNumNo());
    }
}