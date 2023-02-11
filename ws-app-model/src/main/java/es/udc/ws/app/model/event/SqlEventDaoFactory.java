package es.udc.ws.app.model.event;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

/**
 * A factory to get
 * <code>SqlEventDao</code> objects. <p> Required configuration parameters: <ul>
 * <li><code>SqlEventDaoFactory.className</code>: it must specify the full class
 * name of the class implementing
 * <code>SqlEventDao</code>.</li> </ul>
 */
public class SqlEventDaoFactory {

    private final static String CLASS_NAME_PARAMETER = "SqlEventDaoFactory.className";

    private static SqlEventDao dao = null;

    private SqlEventDaoFactory() {
    }

    private static SqlEventDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlEventDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static SqlEventDao getDao() {
        if (dao == null) {
            dao = getInstance();
        }

        return dao;
    }
}
