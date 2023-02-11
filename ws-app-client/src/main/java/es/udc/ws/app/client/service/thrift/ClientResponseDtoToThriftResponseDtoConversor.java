package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientResponseDto;
import es.udc.ws.app.thrift.ThriftResponseDto;

import java.util.List;
import java.util.ArrayList;

public class ClientResponseDtoToThriftResponseDtoConversor {

    public static List<ClientResponseDto> toClientResponseDtos(List<ThriftResponseDto> responses) {
        List<ClientResponseDto> clientResponseDtos = new ArrayList<>(responses.size());

        for (ThriftResponseDto response : responses) {
            clientResponseDtos.add(toClientResponseDto(response));
        }

        return clientResponseDtos;
    }

    public static ClientResponseDto toClientResponseDto(ThriftResponseDto response) {

        return new ClientResponseDto(
                response.getResponseId(),
                response.getEventId(),
                response.getUserEmail(),
                response.isAttending()
        );
    }
}
