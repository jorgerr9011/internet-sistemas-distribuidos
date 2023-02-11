package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientResponseDto;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCelebratedException;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyRespondedException;
import es.udc.ws.app.client.service.exceptions.ClientResponseDeadlineException;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.app.thrift.*;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.time.LocalDateTime;
import java.util.List;

public class ThriftClientEventService implements ClientEventService {

    private final static String ENDPOINT_ADDRESS_PARAMETER = "ThriftClientEventService.endpointAddress";

    private final static String endpointAddress = ConfigurationParametersManager.getParameter(
            ENDPOINT_ADDRESS_PARAMETER);

    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try {
            transport.open();

            return client.addEvent(ClientEventDtoToThriftEventDtoConversor.toThriftEventDto(event)).getEventId();
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public List<ClientEventDto> findByDate(LocalDateTime untilDate, String keywords) {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try {
            transport.open();

            return ClientEventDtoToThriftEventDtoConversor.toClientEventDtos(
                    client.findByDate(String.valueOf(untilDate), keywords));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException {
        return null;
    }

    @Override
    public Long addResponse(Long eventId, String userEmail, boolean respond) throws InstanceNotFoundException,
            InputValidationException, ClientEventAlreadyRespondedException, ClientEventAlreadyCancelledException {
        return null;
    }

    @Override
    public void cancelEvent(Long eventId) throws ClientEventAlreadyCelebratedException, InstanceNotFoundException,
            InputValidationException, ClientEventAlreadyCancelledException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();
            client.cancelEvent(eventId);

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftEventAlreadyCelebratedException e) {
            throw new ClientEventAlreadyCelebratedException(e.getEventId());
        } catch (ThriftEventAlreadyCancelledException e) {
            throw new ClientEventAlreadyCancelledException(e.getEventId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }

    }

    @Override
    public List<ClientResponseDto> getResponses(String email, Boolean affirmatives) {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();

            return ClientResponseDtoToThriftResponseDtoConversor.toClientResponseDtos(client.getResponses(email, affirmatives));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    private ThriftEventService.Client getClient() {

        try {
            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);

            return new ThriftEventService.Client(protocol);

        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
    }
}
