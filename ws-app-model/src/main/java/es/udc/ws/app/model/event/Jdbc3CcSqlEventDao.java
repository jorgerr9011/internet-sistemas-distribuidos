package es.udc.ws.app.model.event;

import java.sql.*;

public class Jdbc3CcSqlEventDao extends AbstractSqlEventDao {

    @Override
    public Event create(Connection connection, Event event) {

        /* Create "queryString". */
        String queryString = "INSERT INTO Event"
                + " (name, description, duration, celebrationDate, creationDate, cancelled, numSi, numNo)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS)) {
            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, event.getName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setShort(i++, event.getDuration());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCelebrationDate()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCreationDate()));
            preparedStatement.setBoolean(i++, event.isCancelled());
            preparedStatement.setInt(i++, 0);
            preparedStatement.setInt(i++, 0);

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long eventId = resultSet.getLong(1);

            /* Return event. */
            return new Event(event.getName(), event.getDescription(),
                    event.getDuration(), event.getCelebrationDate(), event.getCreationDate(), event.isCancelled(), eventId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
