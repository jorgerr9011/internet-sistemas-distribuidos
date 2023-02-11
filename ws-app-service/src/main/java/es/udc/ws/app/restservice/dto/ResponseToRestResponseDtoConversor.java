package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.response.Response;

import java.util.ArrayList;
import java.util.List;

public class ResponseToRestResponseDtoConversor {

    public static RestResponseDto toRestResponseDto(Response response) {
        return new RestResponseDto(response.getResponseId(), response.getEventId(), response.getUserEmail(), response.isAttending());
    }

    public static List<RestResponseDto> toRestResponseDtos(List<Response> responses) {
        List<RestResponseDto> responseDtos = new ArrayList<>(responses.size());

        for (Response response : responses) {
            responseDtos.add(toRestResponseDto(response));
        }

        return responseDtos;
    }

}