package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.ClientEventServiceFactory;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientResponseDto;
import es.udc.ws.util.exceptions.InputValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

public class AppServiceClient {
    public static void main(String[] args) {

        if (args.length == 0) {
            printUsageAndExit();
        }
        ClientEventService clientEventService =
                ClientEventServiceFactory.getService();

        if ("-addEvent".equalsIgnoreCase(args[0])) {
            validateArgs(args, 5, 5, new int[]{});

            // [addEvent]    -addEvent <name> <description> <start_date> <end_date>

            try {
                LocalDateTime startDate;
                LocalDateTime endDate;

                try {
                    startDate = LocalDateTime.parse(args[3]);
                    endDate = LocalDateTime.parse(args[4]);
                } catch (DateTimeParseException e) {
                    throw new InputValidationException("Formato de fecha inválido. Formato correcto: yyyy-MM-ddTHH:mm");
                }

                Long eventId = clientEventService.addEvent(new ClientEventDto(null,
                        args[1], args[2], startDate, endDate, false, 0, 0));

                System.out.println("Event " + eventId + " created successfully");

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if ("-findEvents".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, 3, new int[]{});

            // [findEvents]  -findEvents <untilDate> [<keyword>]

            try {
                List<ClientEventDto> events = null;
                if (args.length <= 2) {
                    events = clientEventService.findByDate(LocalDateTime.of(LocalDate.parse(args[1]), LocalTime.of(0, 0)), null);
                } else {
                    events = clientEventService.findByDate(LocalDateTime.of(LocalDate.parse(args[1]), LocalTime.of(0, 0)), args[2]);
                }
                System.out.println("Found " + events.size() +
                        " event(s) with limitDate '" + args[1] + "'");
                for (ClientEventDto eventDto : events) {
                    System.out.println("Id: " + eventDto.getEventId() +
                            ", Name: " + eventDto.getName() +
                            ", Duration: " + (MINUTES.between(eventDto.getStartDate(), eventDto.getEndDate())) +
                            ", Description: " + eventDto.getDescription() +
                            ", CelebrationDate: " + eventDto.getStartDate());
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-findEvent".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, 2, new int[]{1});

            // [findEvent]    -findEvent <eventId>

            try {
                ClientEventDto eventDto = clientEventService.findEvent(Long.parseLong(args[1]));
                System.out.println("Found " + eventDto.getName() + "with id " + eventDto.getEventId());

                System.out.println("Id: " + eventDto.getEventId() +
                        ", Name: " + eventDto.getName() +
                        ", Duration: " + (MINUTES.between(eventDto.getStartDate(), eventDto.getEndDate())) +
                        ", Description: " + eventDto.getDescription() +
                        ", CelebrationDate: " + eventDto.getStartDate());

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-respond".equalsIgnoreCase(args[0])) {
            validateArgs(args, 4, 4, new int[]{});

            // [respond]    -respond <userEmail> <eventId> <response>

            try {
                clientEventService.addResponse(Long.parseLong(args[2]), args[1], Boolean.parseBoolean(args[3]));

                System.out.println("Response " + args[1] + " added successfully");

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-cancel".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, 2, new int[]{1});

            // [cancel]   -cancel <eventId>

            try {
                clientEventService.cancelEvent(Long.parseLong(args[1]));

                System.out.println("Event with id " + args[1] +
                        " cancelled successfully");

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }


        } else if ("-findResponses".equalsIgnoreCase(args[0])) {
            validateArgs(args, 3, 3, new int[]{});

            // [findResponses]    -findResponses <userEmail> <onlyAffirmatives>

            List<ClientResponseDto> responses;
            try {
                responses = clientEventService.getResponses(args[1], Boolean.parseBoolean(args[2]));

                System.out.println("Found " + responses.size() +
                        " response(s) with userEmail " + args[1]);
                for (ClientResponseDto responseDto : responses) {
                    System.out.println("ResponseId: " + responseDto.getResponseId() +
                            ", EventId: " + responseDto.getEventId() +
                            ", UserEmail: " + (responseDto.getUserEmail()));
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    public static void validateArgs(String[] args, int minArgs, int maxArgs,
                                    int[] numericArguments) {
        if (args.length > maxArgs || args.length < minArgs) {
            printUsageAndExit();
        }
        for (int position : numericArguments) {
            try {
                Double.parseDouble(args[position]);
            } catch (NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("Usage:\n" +
                "    [addEvent]    -addEvent <name> <description> <start_date> <end_date>\n" +
                "    [findEvents]  -findEvents <untilDate> [<keyword>]\n" +
                "    [findEvent]    -findEvent <eventId>\n" +
                "    [respond]    -respond <userEmail> <eventId> <response>\n" +
                "    [cancel]   -cancel <eventId>\n" +
                "    [findResponses]    -findResponses <userEmail> <onlyAffirmatives>\n");
    }
}