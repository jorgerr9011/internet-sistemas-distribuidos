package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.response.Response;
import es.udc.ws.app.thrift.ThriftResponseDto;

import java.util.ArrayList;
import java.util.List;

public class ResponseToThriftResponseDtoConversor {

    public static List<ThriftResponseDto> toThriftResponseDtos(List<Response> responses) {

        List<ThriftResponseDto> dtos = new ArrayList<>(responses.size());

        for (Response response : responses) {
            dtos.add(toThriftResponseDto(response));
        }
        return dtos;
    }

    public static ThriftResponseDto toThriftResponseDto(Response response) {

        return new ThriftResponseDto(response.getResponseId(), response.getEventId(), response.getUserEmail(), response.isAttending());
    }
}