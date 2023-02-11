package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.thrift.ThriftEventDto;
import es.udc.ws.util.exceptions.InputValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

public class ClientEventDtoToThriftEventDtoConversor {

    public static ThriftEventDto toThriftEventDto(ClientEventDto clientEventDto) {

        Long eventId = clientEventDto.getEventId();

        int duration = (int) MINUTES.between(clientEventDto.getStartDate(),
                clientEventDto.getEndDate());

        return new ThriftEventDto(
                eventId == null ? -1 : eventId.longValue(),
                clientEventDto.getName(),
                clientEventDto.getDescription(),
                (short) duration,
                String.valueOf(clientEventDto.getStartDate()),
                clientEventDto.isCancelled(),
                clientEventDto.getNumSi(),
                clientEventDto.getNumResponses());
    }

    public static List<ClientEventDto> toClientEventDtos(List<ThriftEventDto> events) throws InputValidationException {

        List<ClientEventDto> clientEventDtos = new ArrayList<>(events.size());

        for (ThriftEventDto event : events) {
            clientEventDtos.add(toClientEventDto(event));
        }

        return clientEventDtos;
    }

    public static ClientEventDto toClientEventDto(ThriftEventDto event) {

        short duration = event.getDuration();
        String celebrationDate = event.getCelebrationDate();
        LocalDateTime startDate = LocalDateTime.parse(celebrationDate);
        LocalDateTime endDate = startDate.plusMinutes(duration);

        return new ClientEventDto(
                event.getEventId(),
                event.getName(),
                event.getDescription(),
                startDate,
                endDate,
                event.isCancelled(),
                event.getNumSi(),
                event.getNumNo()
        );
    }
}
