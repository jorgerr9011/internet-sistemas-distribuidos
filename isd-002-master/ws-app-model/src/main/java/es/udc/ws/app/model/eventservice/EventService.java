package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCelebratedException;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyRespondedException;
import es.udc.ws.app.model.eventservice.exceptions.ResponseDeadlineException;
import es.udc.ws.app.model.response.Response;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    // FUNC-1
    public Event addEvent(Event event) throws InputValidationException;

    // FUNC-2

    public List<Event> findEventsByDate(LocalDateTime start, LocalDateTime end, String keyword) throws InputValidationException;

    // FUNC-3
    public Event findEvent(Long eventId) throws InstanceNotFoundException;

    // FUNC-4
    public Response addResponse(Long eventId, String userEmail, boolean attending)
            throws InstanceNotFoundException, EventAlreadyCancelledException, EventAlreadyRespondedException, ResponseDeadlineException;

    // FUNC-5
    public void cancelEvent(Long eventId) throws InputValidationException, InstanceNotFoundException,
            EventAlreadyCancelledException, EventAlreadyCelebratedException;

    // FUNC-6
    public List<Response> getResponses(String userEmail, boolean affirmatives);
}
