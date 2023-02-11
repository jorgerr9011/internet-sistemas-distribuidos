package es.udc.ws.app.model.event;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlEventDao implements SqlEventDao {

    protected AbstractSqlEventDao() {
    }

    @Override
    public List<Event> findByDate(Connection connection, LocalDateTime start, LocalDateTime end, String keywords) {
        String[] words = keywords != null ? keywords.split(" ") : null;

        String queryStringEvent = "SELECT eventId, name, description, "
                + "duration, celebrationDate, creationDate, cancelled, numSi, numNo "
                + "FROM Event";

        queryStringEvent += " WHERE";

        if (words != null && words.length > 0) {
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    queryStringEvent += " AND";
                }
                queryStringEvent += " LOWER(description) LIKE LOWER(?)";
            }
            queryStringEvent += " AND";
        }

        queryStringEvent += " celebrationDate >= ? AND celebrationDate < ? ORDER BY celebrationDate";


        try (PreparedStatement preparedStatement = connection.prepareStatement(queryStringEvent)) {
            /* Fill preparedStatement */
            int i = 0;
            if (words != null) {
                /* Fill "preparedStatement". */
                for (i = 0; i < words.length; i++) {
                    preparedStatement.setString(i + 1, "%" + words[i] + "%");
                }
            }
            preparedStatement.setDate(++i, Date.valueOf(start.toLocalDate()));
            preparedStatement.setDate(++i, Date.valueOf(end.toLocalDate()));

            /* Execute query */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Read Events */
            List<Event> events = new ArrayList<Event>();

            while (resultSet.next()) {
                i = 1;
                Long eventId = resultSet.getLong(i++);
                String name = resultSet.getString(i++);
                String description = resultSet.getString(i++);
                short duration = resultSet.getShort(i++);
                Timestamp celebrationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime celebrationDate = celebrationDateAsTimestamp.toLocalDateTime();
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
                boolean cancelled = resultSet.getBoolean(i++);
                int numSi = resultSet.getInt(i++);
                int numNo = resultSet.getInt(i++);

                events.add(new Event(name, description, duration, celebrationDate, creationDate, cancelled, numSi, numNo, eventId));
            }

            return events;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Event find(Connection connection, Long eventId)
            throws InstanceNotFoundException {
        /* Create "queryString". */
        String queryString = "SELECT name, description, "
                + " duration, celebrationDate, creationDate, cancelled, numSi, numNo FROM Event WHERE eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, eventId);

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(eventId,
                        Event.class.getName());
            }

            /* Get results. */
            i = 1;
            String name = resultSet.getString(i++);
            String description = resultSet.getString(i++);
            short duration = resultSet.getShort(i++);
            Timestamp celebrationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime celebrationDate = celebrationDateAsTimestamp.toLocalDateTime();
            Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
            boolean cancelled = resultSet.getBoolean(i++);
            int numSi = resultSet.getInt(i++);
            int numNo = resultSet.getInt(i++);

            /* Return Event. */
            return new Event(name, description, duration, celebrationDate, creationDate, cancelled, numSi, numNo, eventId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Connection connection, Event event) throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "UPDATE Event " +
                "SET name = ?, " +
                "description = ?, " +
                "celebrationDate = ?, " +
                "duration = ?, " +
                "creationDate = ?, " +
                "cancelled = ?, " +
                "numSi = ?, " +
                "numNo = ? " +
                "WHERE eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, event.getName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCelebrationDate()));
            preparedStatement.setShort(i++, event.getDuration());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCreationDate()));
            preparedStatement.setBoolean(i++, event.isCancelled());
            preparedStatement.setInt(i++, event.getNumSi());
            preparedStatement.setInt(i++, event.getNumNo());
            preparedStatement.setLong(i++, event.getEventId());

            /* Execute query. */
            int updatedRow = preparedStatement.executeUpdate();

            if (updatedRow == 0) {
                throw new InstanceNotFoundException(event.getEventId(), Event.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long eventId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "DELETE FROM Event WHERE" + " eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, eventId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(eventId,
                        Event.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
