package es.udc.ws.app.model.response;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlResponseDao {
    public Response create(Connection connection, Response response);

    public List<Response> find(Connection connection, String userEmail, boolean affirmatives);

    public void remove(Connection connection, Long responseId)
            throws InstanceNotFoundException;

    public boolean alreadyResponded(Connection connection, Long eventId, String userEmail);
}
