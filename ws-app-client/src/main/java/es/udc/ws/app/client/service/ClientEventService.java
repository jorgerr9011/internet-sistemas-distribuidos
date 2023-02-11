package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientResponseDto;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCelebratedException;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyRespondedException;
import es.udc.ws.app.client.service.exceptions.ClientResponseDeadlineException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.transport.TTransportException;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientEventService {

    public Long addEvent(ClientEventDto event) throws InputValidationException;

    public List<ClientEventDto> findByDate(LocalDateTime untilDate, String keywords) throws InputValidationException, TTransportException;

    public ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException;

    public Long addResponse(Long eventId, String userEmail, boolean respond) throws InstanceNotFoundException, InputValidationException, ClientEventAlreadyRespondedException, ClientEventAlreadyCancelledException;

    public void cancelEvent(Long eventId) throws ClientEventAlreadyCelebratedException, InstanceNotFoundException, InputValidationException, ClientEventAlreadyCancelledException;

    public List<ClientResponseDto> getResponses(String email, Boolean affirmatives);
}
