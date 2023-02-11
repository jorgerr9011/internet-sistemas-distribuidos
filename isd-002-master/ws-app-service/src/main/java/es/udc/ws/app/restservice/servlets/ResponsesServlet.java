package es.udc.ws.app.restservice.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyRespondedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.udc.ws.app.restservice.dto.RestResponseDto;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.ResponseDeadlineException;
import es.udc.ws.app.model.response.Response;
import es.udc.ws.app.restservice.json.EventsExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestResponseDtoConversor;
import es.udc.ws.app.restservice.dto.ResponseToRestResponseDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;

public class ResponsesServlet extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        ServletUtils.checkEmptyPath(req);
        Long eventId = ServletUtils.getMandatoryParameterAsLong(req, "eventId");
        String userEmail = ServletUtils.getMandatoryParameter(req, "userEmail");
        boolean attending = Boolean.parseBoolean(ServletUtils.getMandatoryParameter(req, "attending"));

        Response response = null;

        try {
            response = EventServiceFactory.getService().addResponse(eventId, userEmail, attending);
        } catch (EventAlreadyCancelledException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    EventsExceptionToJsonConversor.toEventAlreadyCancelledException(e), null);
            return;
        } catch (ResponseDeadlineException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    EventsExceptionToJsonConversor.toResponseDeadlineException(e), null);
            return;
        } catch (EventAlreadyRespondedException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    EventsExceptionToJsonConversor.toEventAlreadyRespondedException(e), null);
            return;
        }

        RestResponseDto responseDto = ResponseToRestResponseDtoConversor.toRestResponseDto(response);
        String responseURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + response.getResponseId().toString();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", responseURL);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestResponseDtoConversor.toObjectNode(responseDto), headers);
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        String userEmail = ServletUtils.getMandatoryParameter(req, "userEmail");
        boolean affirmatives = Boolean.parseBoolean(ServletUtils.getMandatoryParameter(req, "affirmatives"));

        List<Response> responses;
        responses = EventServiceFactory.getService().getResponses(userEmail, affirmatives);

        List<RestResponseDto> responseDtos = ResponseToRestResponseDtoConversor.toRestResponseDtos(responses);

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestResponseDtoConversor.toArrayNode(responseDtos), null);
    }
}
