package es.udc.ws.app.model.response;

import java.sql.*;

public class Jdbc3CcSqlResponseDao extends AbstractResponseDao{

    @Override
    public Response create(Connection connection, Response response) {

        /* Create "queryString". */
        String queryString = "INSERT INTO Response"
                + " (eventId, userEmail, responseDate, attending)"
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS)) {
            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, response.getEventId());
            preparedStatement.setString(i++, response.getUserEmail());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(response.getResponseDate()));
            preparedStatement.setBoolean(i++, response.isAttending());

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long responseId = resultSet.getLong(1);

            /* Return response. */
            return new Response(responseId, response.getEventId(), response.getUserEmail(),
                    response.getResponseDate(), response.isAttending());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
