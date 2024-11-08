package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientResponseDto;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCelebratedException;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyRespondedException;
import es.udc.ws.app.client.service.rest.json.JsonToClientEventDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientResponseDtoConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class RestClientEventService implements ClientEventService {

    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientEventService.endpointAddress";
    private String endpointAddress;

    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException {

        try {

            HttpResponse response = Request.Post(getEndpointAddress() + "events").
                    bodyStream(toInputStream(event), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent()).getEventId();

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientEventDto> findByDate(LocalDateTime untilDate, String keywords) throws InputValidationException {

        try {
            HttpResponse response;
            if (keywords != null) {
                response = Request.Get(getEndpointAddress() + "events?keywords=" +
                                URLEncoder.encode(keywords, StandardCharsets.UTF_8) + "&untilDate=" + URLEncoder.encode(String.valueOf(untilDate), StandardCharsets.UTF_8)).
                        execute().returnResponse();

            } else {
                response = Request.Get(getEndpointAddress() + "events?untilDate=" +
                        URLEncoder.encode(String.valueOf(untilDate), StandardCharsets.UTF_8)).execute().returnResponse();

            }
            validateStatusCode(HttpStatus.SC_OK, response);
            return JsonToClientEventDtoConversor.toClientEventDtos(response.getEntity()
                    .getContent());

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException {

        try {
            HttpResponse response = Request.Get(getEndpointAddress() + "events/" + eventId).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent());

        } catch (InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long addResponse(Long eventId, String userEmail, boolean attending) throws InstanceNotFoundException, InputValidationException, ClientEventAlreadyRespondedException, ClientEventAlreadyCancelledException {

        try {

            HttpResponse response = Request.Post(getEndpointAddress() + "responses").
                    bodyForm(
                            Form.form().
                                    add("eventId", Long.toString(eventId)).
                                    add("userEmail", userEmail).
                                    add("attending", Boolean.toString(attending)).
                                    build()).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientResponseDtoConversor.toClientResponseDto(response.getEntity().getContent()).getResponseId();

        } catch (InstanceNotFoundException | InputValidationException | ClientEventAlreadyRespondedException |
                 ClientEventAlreadyCancelledException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelEvent(Long eventId) throws ClientEventAlreadyCelebratedException,
            InstanceNotFoundException, InputValidationException, ClientEventAlreadyCancelledException {

        try {
            HttpResponse response = Request.Put(getEndpointAddress() + "events/" + eventId)
                    .execute().returnResponse();

            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (InputValidationException | InstanceNotFoundException | ClientEventAlreadyCelebratedException |
                 ClientEventAlreadyCancelledException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientResponseDto> getResponses(String userEmail, Boolean affirmatives) {

        try {

            HttpResponse response = Request.Get(getEndpointAddress() + "responses?userEmail=" +
                    URLEncoder.encode(userEmail, StandardCharsets.UTF_8) + "&affirmatives=" + URLEncoder.encode(
                    String.valueOf(affirmatives), StandardCharsets.UTF_8)).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientResponseDtoConversor.toClientResponseDtos(response.getEntity().getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }

        return endpointAddress;
    }

    private InputStream toInputStream(ClientEventDto event) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientEventDtoConversor.toObjectNode(event));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateStatusCode(int successCode, HttpResponse response) throws Exception {

        try {

            int statusCode = response.getStatusLine().getStatusCode();

            /* Success? */
            if (statusCode == successCode) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND -> throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_BAD_REQUEST -> throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_FORBIDDEN -> throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_GONE -> throw JsonToClientExceptionConversor.fromGoneErrorCode(
                        response.getEntity().getContent());
                default -> throw new RuntimeException("HTTP error; status code = "
                        + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

