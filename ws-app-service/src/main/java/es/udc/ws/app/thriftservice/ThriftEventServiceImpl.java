package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCelebratedException;
import es.udc.ws.app.model.response.Response;
import es.udc.ws.app.thrift.*;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public class ThriftEventServiceImpl implements ThriftEventService.Iface {

    @Override
    public ThriftEventDto addEvent(ThriftEventDto eventDto) throws ThriftInputValidationException {

        Event evento = EventToThriftEventDtoConversor.toEvent(eventDto);;

        try {
            Event addedEvent = EventServiceFactory.getService().addEvent(evento);
            return EventToThriftEventDtoConversor.toThriftEventDto(addedEvent);
        } catch (InputValidationException e){
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    @Override
    public List<ThriftEventDto> findByDate(String untilDate, String keywords) throws ThriftInputValidationException, ThriftDateTimeParseException {

        List<Event> events;
        LocalDateTime date = LocalDateTime.parse(untilDate);

        try {
            events = EventServiceFactory.getService().findEventsByDate(LocalDateTime.now(), date, keywords);
            return EventToThriftEventDtoConversor.toThriftEventDtos(events);

        } catch (InputValidationException e) {
                throw new ThriftInputValidationException(e.getMessage());
        }
    }

    @Override
    public void cancelEvent(long eventId) throws ThriftInstanceNotFoundException, ThriftInputValidationException, ThriftEventAlreadyCancelledException, ThriftEventAlreadyCelebratedException {

        try {
            EventServiceFactory.getService().cancelEvent(eventId);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        } catch (EventAlreadyCelebratedException e) {
            throw new ThriftEventAlreadyCelebratedException(e.getEventId());
        } catch (EventAlreadyCancelledException e) {
            throw new ThriftEventAlreadyCancelledException(e.getEventId());
        }

    }

    @Override
    public List<ThriftResponseDto> getResponses(String userEmail, boolean affirmatives) {

        List<Response> responses = EventServiceFactory.getService().getResponses(userEmail, affirmatives);

        return ResponseToThriftResponseDtoConversor.toThriftResponseDtos(responses);

    }


}