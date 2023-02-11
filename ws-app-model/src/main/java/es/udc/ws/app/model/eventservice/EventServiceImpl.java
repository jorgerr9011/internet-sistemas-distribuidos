package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
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
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static es.udc.ws.app.model.util.ModelConstants.MAX_DURATION;

public class EventServiceImpl implements EventService {

    private final DataSource dataSource;
    private SqlEventDao eventDao = null;
    private SqlResponseDao responseDao = null;

    public EventServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        eventDao = SqlEventDaoFactory.getDao();
        responseDao = SqlResponseDaoFactory.getDao();
    }

    private void validateEvent(Event event) throws InputValidationException {

        PropertyValidator.validateMandatoryString("name", event.getName());
        PropertyValidator.validateLong("duration", event.getDuration(), 0, MAX_DURATION);
        PropertyValidator.validateMandatoryString("description", event.getDescription());
    }

    @Override
    public Event addEvent(Event event) throws InputValidationException {

        validateEvent(event);

        if (event.getCelebrationDate().isBefore(LocalDateTime.now())) {
            throw new InputValidationException("Event already celebrated.");
        }

        if (event.isCancelled()) {
            throw new InputValidationException("Cannot add cancelled event");
        }

        event.setCreationDate(LocalDateTime.now());
        event.setCancelled(false);
        event.setNumSi(0);
        event.setNumNo(0);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                Event createdEvent = eventDao.create(connection, event);

                /* Commit. */
                connection.commit();

                return createdEvent;

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

    @Override
    public Event findEvent(Long eventId) throws InstanceNotFoundException {

        try (Connection connection = dataSource.getConnection()) {
            return eventDao.find(connection, eventId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Event> findEventsByDate(LocalDateTime start, LocalDateTime end, String keyword) throws InputValidationException {

        if (start == null || end == null) {
            throw new InputValidationException(null);
        }

        try (Connection connection = dataSource.getConnection()) {
            return eventDao.findByDate(connection, start, end, keyword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response addResponse(Long eventId, String userEmail, boolean attending)
            throws InstanceNotFoundException, EventAlreadyCancelledException, EventAlreadyRespondedException, ResponseDeadlineException {

        try (Connection connection = dataSource.getConnection()) {

            try {
                /* prepare connection */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                if (responseDao.alreadyResponded(connection, eventId, userEmail)) {
                    throw new EventAlreadyRespondedException(eventId, userEmail);
                }

                Event event = eventDao.find(connection, eventId);
                LocalDateTime dateResponse = LocalDateTime.now();

                if (event.getCelebrationDate().minusDays(1).isBefore(LocalDateTime.now())) {
                    throw new ResponseDeadlineException(event.getEventId());
                }

                if (event.isCancelled()) {
                    throw new EventAlreadyCancelledException(event.getEventId());
                }

                Response response = responseDao.create(connection, new Response(eventId, userEmail, dateResponse, attending));

                if (attending) {
                    event.addNumSi();
                } else {
                    event.addNumNo();
                }

                eventDao.update(connection, event);

                /* Commit */
                connection.commit();

                return response;

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw e;
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

    @Override
    public void cancelEvent(Long eventId) throws InputValidationException, InstanceNotFoundException,
            EventAlreadyCancelledException, EventAlreadyCelebratedException {

        try (Connection connection1 = dataSource.getConnection()) {
            Event event = eventDao.find(connection1, eventId);

            validateEvent(event);

            try (Connection connection2 = dataSource.getConnection()) {

                try {

                    /* Prepare connection. */
                    connection2.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    connection2.setAutoCommit(false);

                    if (event.hasBeenCelebrated()) {
                        throw new EventAlreadyCelebratedException(event.getEventId());
                    }

                    if (event.isCancelled()) {
                        throw new EventAlreadyCancelledException(event.getEventId());
                    }

                    event.setCancelled(true);

                    /* Do work. */
                    eventDao.update(connection2, event);

                    /* Commit. */
                    connection2.commit();

                } catch (InstanceNotFoundException e) {
                    connection2.commit();
                    throw e;
                } catch (SQLException e) {
                    connection2.rollback();
                    throw new RuntimeException(e);
                } catch (RuntimeException | Error e) {
                    connection2.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Response> getResponses(String userEmail, boolean affirmatives) {

        try (Connection connection = dataSource.getConnection()) {
            return responseDao.find(connection, userEmail, affirmatives);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
