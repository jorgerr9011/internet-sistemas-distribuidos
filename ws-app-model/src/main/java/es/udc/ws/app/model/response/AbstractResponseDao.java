package es.udc.ws.app.model.response;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractResponseDao implements SqlResponseDao{

    protected AbstractResponseDao(){
    }

    @Override
    public List<Response> find(Connection connection, String userEmail, boolean affirmatives) {

        String queryString = "SELECT responseId, eventId, responseDate, attending FROM Response WHERE userEmail = ?";
        if (affirmatives) {
            queryString += " AND attending = ?";
        }
        List<Response> responses = new ArrayList<Response>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, userEmail);
            if (affirmatives) {
                preparedStatement.setBoolean(i++, true);
            }
            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Get results. */
            while (resultSet.next()) {

                i = 1;
                Long responseId = resultSet.getLong(i++);
                Long eventId = resultSet.getLong(i++);
                LocalDateTime responseDate = resultSet.getTimestamp(i++).toLocalDateTime();
                boolean attending = resultSet.getBoolean(i++);
                if(!attending && affirmatives)
                    continue;

                responses.add(new Response(responseId, eventId, userEmail, responseDate, attending));
            }
            /* Return responses. */
            return responses;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long responseId)
            throws InstanceNotFoundException {
        /* Create "queryString". */
        String queryString = "DELETE FROM Response WHERE" + " responseId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, responseId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(responseId,
                        Response.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean alreadyResponded(Connection connection, Long eventId, String userEmail) {

        String queryString = "SELECT COUNT(*) FROM Response WHERE eventId = ? AND userEmail = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            int i = 1;
            preparedStatement.setLong(i++, eventId.longValue());
            preparedStatement.setString(i++, userEmail);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()){
                throw new SQLException("Error al recuperar la lista de respuestas");
            }

            i = 1;
            Long numeroRespuestas = resultSet.getLong(i++);

            return numeroRespuestas > 0;

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
