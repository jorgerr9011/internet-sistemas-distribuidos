package es.udc.ws.app.restservice.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCelebratedException;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.restservice.json.JsonToRestEventDtoConversor;
import es.udc.ws.app.restservice.dto.EventToRestEventDtoConversor;
import es.udc.ws.app.restservice.json.EventsExceptionToJsonConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;

@SuppressWarnings("serial")
public class EventsServlet extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException {
        ServletUtils.checkEmptyPath(req);

        RestEventDto eventDto = JsonToRestEventDtoConversor.toRestEventDto(req.getInputStream());
        Event event = EventToRestEventDtoConversor.toEvent(eventDto);

        event = EventServiceFactory.getService().addEvent(event);

        eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
        String eventURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + event.getEventId();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", eventURL);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestEventDtoConversor.toObjectNode(eventDto), headers);
    }

    @Override
    protected void processPut(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        Long eventId = ServletUtils.getIdFromPath(req, "eventId");

        try {
            EventServiceFactory.getService().cancelEvent(eventId);
        } catch (EventAlreadyCelebratedException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    EventsExceptionToJsonConversor.toEventAlreadyCelebratedException(e), null);
        } catch (EventAlreadyCancelledException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    EventsExceptionToJsonConversor.toEventAlreadyCancelledException(e), null);
        }

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        String path = ServletUtils.normalizePath(req.getPathInfo());

        if (path != null && path.length() > 0) {
            Long eventId = Long.parseLong(path.substring(1));

            Event event;
            event = EventServiceFactory.getService().findEvent(eventId);

            RestEventDto eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                    JsonToRestEventDtoConversor.toObjectNode(eventDto), null);
        } else {
            String toDate = ServletUtils.getMandatoryParameter(req, "untilDate");
            String keywords = req.getParameter("keywords");

            LocalDateTime date;

            try {
                date = LocalDateTime.parse(toDate);
            } catch (DateTimeParseException e) {
                throw new InputValidationException("Invalid Request: parameter toDate must have yyyy-MM-ddTHH:mm format");
            }

            List<Event> events = EventServiceFactory.getService().findEventsByDate(LocalDateTime.now(), date, keywords);
            List<RestEventDto> eventDtos = EventToRestEventDtoConversor.toRestEventDtos(events);

            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                    JsonToRestEventDtoConversor.toArrayNode(eventDtos), null);
        }
    }
}
