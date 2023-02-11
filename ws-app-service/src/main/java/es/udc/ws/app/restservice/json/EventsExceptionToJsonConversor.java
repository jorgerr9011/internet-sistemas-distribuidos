package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCelebratedException;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyRespondedException;
import es.udc.ws.app.model.eventservice.exceptions.ResponseDeadlineException;

public class EventsExceptionToJsonConversor {

    public static ObjectNode toEventAlreadyCancelledException(EventAlreadyCancelledException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "EventAlreadyCancelled");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);

        return exceptionObject;
    }

    public static ObjectNode toEventAlreadyCelebratedException(EventAlreadyCelebratedException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "EventAlreadyCelebrated");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);

        return exceptionObject;
    }

    public static ObjectNode toEventAlreadyRespondedException(EventAlreadyRespondedException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "EventAlreadyResponded");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);

        if (ex.getEmail() != null) {
            exceptionObject.put("userEmail", ex.getEmail());
        } else {
            exceptionObject.set("userEmail", null);
        }

        return exceptionObject;
    }

    public static ObjectNode toResponseDeadlineException(ResponseDeadlineException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "ResponseDeadline");
        exceptionObject.put("responseId", (ex.getEventId() != null) ? ex.getEventId() : null);

        return exceptionObject;
    }
}
