package es.udc.ws.app.test.model.eventservice;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
import es.udc.ws.app.model.eventservice.EventService;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyCelebratedException;
import es.udc.ws.app.model.eventservice.exceptions.EventAlreadyRespondedException;
import es.udc.ws.app.model.eventservice.exceptions.ResponseDeadlineException;
import es.udc.ws.app.model.response.Response;
import es.udc.ws.app.model.response.SqlResponseDao;
import es.udc.ws.app.model.response.SqlResponseDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static es.udc.ws.app.model.util.ModelConstants.MAX_DURATION;
import static org.junit.jupiter.api.Assertions.*;

public class EventServiceTest {

    private final long NON_EXISTENT_EVENT_ID = -1;

    private final long NON_EXISTENT_RESPONSE_ID = -1;

    private final LocalDateTime OK_DATE = LocalDateTime.of(2024, 2, 10, 10, 10);

    private final LocalDateTime BAD_DATE = LocalDateTime.of(2010, 5, 10, 10, 10);
    private static EventService eventService = null;
    private static SqlResponseDao responseDao = null;

    private static SqlEventDao eventDao = null;

    @BeforeAll
    public static void init() {
        /*
         * Create a simple data source and add it to "DataSourceLocator" (this
         * is needed to test "es.udc.ws.event.model.eventservice.EventService"
         */
        DataSource dataSource = new SimpleDataSource();
        /* Add "dataSource" to "DataSourceLocator". */
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

        eventService = EventServiceFactory.getService();

        responseDao = SqlResponseDaoFactory.getDao();

        eventDao = SqlEventDaoFactory.getDao();
    }

    private Event getValidEvent(String title) {
        return new Event(title, "Event description", (short) 72, LocalDateTime.of(2025, 1, 13, 20, 20), LocalDateTime.now());
    }

    private Event getValidEvent() {
        return getValidEvent("Event not celebrated yet!");
    }

    private Event getCelebratedEvent(String title) {
        return new Event(title, "Event description", (short) 72, LocalDateTime.of(2020, 1, 13, 20, 20), LocalDateTime.now());
    }

    private Event getCancelledEvent(String title) {
        return new Event(title, "Event description", (short) 72, LocalDateTime.of(2025, 1, 13, 20, 20), LocalDateTime.now(), true);
    }

    private Event getCancelledEvent() {
        return getCancelledEvent("Cancelled event!");
    }

    private Event createEvent(Event event) {

        Event addedEvent = null;
        try {
            addedEvent = eventService.addEvent(event);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedEvent;
    }

    private void updateEvent(Event evento) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                eventDao.update(connection, evento);

                connection.commit();
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeEvent(Long eventId) {

        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                eventDao.remove(connection, eventId);

                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeResponse(Long responseId) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                responseDao.remove(connection, responseId);

                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddEventAndFindEvent() throws InputValidationException, InstanceNotFoundException {
        Event event = getValidEvent();
        Event addedEvent = null;

        try {
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0).minusSeconds(1);

            addedEvent = eventService.addEvent(event);

            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0).plusSeconds(1);

            Event foundEvent = eventService.findEvent(addedEvent.getEventId());

            assertEquals(addedEvent, foundEvent);
            assertEquals(addedEvent.getName(), foundEvent.getName());
            assertEquals(addedEvent.getDescription(), foundEvent.getDescription());
            assertEquals(addedEvent.getDuration(), foundEvent.getDuration());
            assertEquals(addedEvent.getCelebrationDate(), foundEvent.getCelebrationDate());

            assertTrue((foundEvent.getCreationDate().compareTo(beforeCreationDate) >= 0)
                    && (foundEvent.getCreationDate().compareTo(afterCreationDate) <= 0));

        } finally {
            // Clear Database
            if (addedEvent != null) {
                removeEvent(addedEvent.getEventId());
            }
        }
    }

    @Test
    public void testAddInvalidEvent() {
        // Event name null
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Event name empty
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent("");
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Event description null
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDescription(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Event description empty
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDescription("");
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Event duration < 0
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDuration((short) -1);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Event duration > MAX_DURATION
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDuration((short) (MAX_DURATION + 1));
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Event celebration < now
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setCelebrationDate(BAD_DATE);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
    }

    @Test
    public void testCancelEvent() throws InputValidationException, InstanceNotFoundException,
            EventAlreadyCancelledException, EventAlreadyCelebratedException {

        Event eventToCancel = createEvent(getValidEvent());

        try {
            eventService.cancelEvent(eventToCancel.getEventId());

            Event cancelledEvent = eventService.findEvent(eventToCancel.getEventId());

            assertTrue(cancelledEvent.isCancelled());
        } finally {
            // Clear database
            if (eventToCancel != null) {
                removeEvent(eventToCancel.getEventId());
            }
        }
    }

    @Test
    public void testCancelNonExistentEvent() {

        Event event = getValidEvent();
        event.setEventId(NON_EXISTENT_EVENT_ID);
        event.setCreationDate(LocalDateTime.now());

        assertThrows(InstanceNotFoundException.class, () -> eventService.cancelEvent(event.getEventId()));
    }

    @Test
    public void testCancelCelebratedEvent() throws InstanceNotFoundException, InputValidationException {

        Event celebratedEvent = eventService.addEvent(getValidEvent());
        celebratedEvent.setCelebrationDate(BAD_DATE);
        updateEvent(celebratedEvent);

        try {
            Event event = eventService.findEvent(celebratedEvent.getEventId());
            assertThrows(EventAlreadyCelebratedException.class, () -> eventService.cancelEvent(event.getEventId()));
        } finally {
            // Clear Database
            removeEvent(celebratedEvent.getEventId());
        }
    }

    @Test
    public void testCancelCancelledEvent() throws InstanceNotFoundException, EventAlreadyCelebratedException, InputValidationException, EventAlreadyCancelledException {

        Event cancelledEvent = createEvent(getValidEvent());

        eventService.cancelEvent(cancelledEvent.getEventId());

        Long eventId = cancelledEvent.getEventId();


        try {
            Event event = eventService.findEvent(eventId);
            assertThrows(EventAlreadyCancelledException.class, () -> eventService.cancelEvent(event.getEventId()));
        } finally {
            // Clear Database
            removeEvent(eventId);
        }

    }

    @Test
    public void testRespond() throws EventAlreadyRespondedException, InstanceNotFoundException, EventAlreadyCelebratedException {
        Event event = createEvent(getValidEvent());
        Response response = null;
        Response response2 = null;
        Response responseAux;
        final String userEmail = "email.valido@yahoo.com";
        final String userEmail2 = "email.valido@server.net";
        List<Response> responseList;

        try {
            //creating response
            response = eventService.addResponse(event.getEventId(), userEmail, true);

            responseList = eventService.getResponses(userEmail, false);
            responseAux = responseList.get(0);

            //Check responses
            assertEquals(response, responseAux);

            //Comprobamos que al indicar solo las afirmativas, no inserta las "no asistencias"
            response2 = eventService.addResponse(event.getEventId(), userEmail2, false);
            responseList = eventService.getResponses(userEmail, true);

            assertEquals(1, responseList.size());
            assertEquals(responseList.get(0).getUserEmail(), userEmail);

            responseList = eventService.getResponses(userEmail2, false);
            assertEquals(responseList.get(0).getUserEmail(), userEmail2);

        } catch (ResponseDeadlineException | EventAlreadyCancelledException e) {
            throw new RuntimeException(e);
        } finally {
            //Clear database : remove response (if created)
            if (response != null) {
                removeResponse(response.getResponseId());
            }
            if (response2 != null) {
                removeResponse(response2.getResponseId());
            }
            removeEvent(event.getEventId());
        }
    }

    @Test
    public void testRespondToCancelledEvent() throws EventAlreadyCelebratedException, InstanceNotFoundException, InputValidationException, EventAlreadyCancelledException {
        Event cancelledEvent = createEvent(getValidEvent());
        eventService.cancelEvent(cancelledEvent.getEventId());

        final String userEmail = "email.valido@yahoo.com";

        assertThrows(EventAlreadyCancelledException.class, () -> eventService.addResponse(cancelledEvent.getEventId(),
                userEmail, true));

        removeEvent(cancelledEvent.getEventId());
    }

    @Test
    public void testRespondToNonExistentEvent() {
        final String userEmail = "email.valido@yahoo.com";

        assertThrows(InstanceNotFoundException.class, () -> {
            Response response = eventService.addResponse(NON_EXISTENT_RESPONSE_ID, userEmail, false);
            removeResponse(response.getResponseId());
        });
    }

    @Test
    public void testRespondAlreadyRespondedEvent() throws InstanceNotFoundException,
            EventAlreadyCelebratedException, EventAlreadyRespondedException, ResponseDeadlineException, EventAlreadyCancelledException {
        Event addedEvent = createEvent(getValidEvent());
        Response addedResponse = eventService.addResponse(addedEvent.getEventId(), "prueba@mailserver.io", true);

        try {
            assertThrows(EventAlreadyRespondedException.class, () -> {
                eventService.addResponse(addedEvent.getEventId(), "prueba@mailserver.io", false);
            });
        } finally {
            removeResponse(addedResponse.getResponseId());
            removeEvent(addedEvent.getEventId());
        }
    }

    @Test
    public void testResponseDeadLine() throws EventAlreadyCelebratedException {
        Event addedEvent = createEvent(getValidEvent());
        addedEvent.setCelebrationDate(LocalDateTime.now().plusDays(1).minusSeconds(1));
        updateEvent(addedEvent);
        try {
            assertThrows(ResponseDeadlineException.class, () -> {
                Response addedResponse = eventService.addResponse(addedEvent.getEventId(), "ERKJUYGV@GMAIL.ES", false);
                removeResponse(addedResponse.getResponseId());
            });
        } finally {
            removeEvent(addedEvent.getEventId());
        }
    }

    @Test
    public void testFindNonExistentEvent() {
        assertThrows(InstanceNotFoundException.class, () -> eventService.findEvent(NON_EXISTENT_EVENT_ID));
    }

    @Test
    public void testFindEvents() throws InstanceNotFoundException, InputValidationException {

        // Add events
        List<Event> events = new LinkedList<Event>();

        Event event1 = getValidEvent();
        event1.setDescription("Estamos en un gran evento");
        event1 = createEvent(event1);
        events.add(event1);

        Event event2 = getValidEvent();
        event2.setDescription("Estamos en un pequeño evento");
        event2 = createEvent(event2);
        events.add(event2);

        Event event3 = getValidEvent();
        event3.setDescription("Hola, buenos días");
        event3 = createEvent(event3);
        events.add(event3);

        try {
            // Buscando descripción exacta
            List<Event> foundEvents = eventService.findEventsByDate(LocalDateTime.of(2024, 1, 1, 10, 10, 10), LocalDateTime.of(2026, 1, 1, 10, 10, 10), "Estamos en un gran evento");
            assertEquals(events.get(0), foundEvents.get(0));

            // Buscando descripción con alteraciones en las mayusc. y minusc.
            foundEvents = eventService.findEventsByDate(LocalDateTime.of(2024, 1, 1, 10, 10, 10), LocalDateTime.of(2026, 1, 1, 10, 10, 10), "peQueÑo EvenTO");
            assertEquals(1, foundEvents.size());
            assertEquals(events.get(1), foundEvents.get(0));

            // Buscando por keywords inexistentes
            foundEvents = eventService.findEventsByDate(LocalDateTime.of(2024, 1, 1, 10, 10, 10), LocalDateTime.of(2026, 1, 1, 10, 10, 10), "QUETALESTAS");
            assertEquals(0, foundEvents.size());

            // Buscando sin keywords
            foundEvents = eventService.findEventsByDate(LocalDateTime.of(2024, 1, 1, 10, 10, 10), LocalDateTime.of(2026, 1, 1, 10, 10, 10), null);
            assertEquals(events, foundEvents);
        } finally {
            // Clear Database
            for (Event event : events) {
                removeEvent(event.getEventId());
            }
        }

    }

    @Test
    public void testFindInvalidEventDate() throws InputValidationException, EventAlreadyCelebratedException {
        Event event = getValidEvent();
        Event addedEvent = eventService.addEvent(event);
        assertThrows(InputValidationException.class, () ->
                eventService.findEventsByDate(null, OK_DATE, null));
        assertThrows(InputValidationException.class, () ->
                eventService.findEventsByDate(OK_DATE, null, null));
        assertThrows(InputValidationException.class, () ->
                eventService.findEventsByDate(null, null, "event"));
        removeEvent(addedEvent.getEventId());
    }

    @Test
    public void testGetResponses() throws EventAlreadyCelebratedException, EventAlreadyRespondedException, InstanceNotFoundException, EventAlreadyCancelledException, ResponseDeadlineException {

        Event addedEvent1 = createEvent(getValidEvent());
        Event addedEvent2 = createEvent(getValidEvent());
        Response addedResponseYes = null;
        Response addedResponseNo = null;
        String emailOk = "email@server.net";

        try {
            addedResponseNo = eventService.addResponse(addedEvent1.getEventId(), emailOk, false);
            addedResponseYes = eventService.addResponse(addedEvent2.getEventId(), emailOk, true);

            List<Response> responses = eventService.getResponses(emailOk, false);

            assertTrue(responses.contains(addedResponseYes));
            assertTrue(responses.contains(addedResponseNo));
            assertEquals(2, responses.size());

            List<Response> yesResponses = eventService.getResponses(emailOk, true);
            assertTrue(yesResponses.contains(addedResponseYes));
            assertEquals(1, yesResponses.size());

            List<Response> respuestaNull = eventService.getResponses("otro@correo.co", false);
            assertEquals(0, respuestaNull.size());
        } finally {
            if (addedResponseYes != null)
                removeResponse(addedResponseYes.getResponseId());
            if (addedResponseNo != null)
                removeResponse(addedResponseNo.getResponseId());
            if (addedEvent1 != null)
                removeEvent(addedEvent1.getEventId());
            if (addedEvent2 != null)
                removeEvent(addedEvent2.getEventId());
        }

    }
}
