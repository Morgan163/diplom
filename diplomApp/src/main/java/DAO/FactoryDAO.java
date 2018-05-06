package DAO;

import DAO.impl.FiberDAOImpl;
import DAO.impl.RelatedSensorsDAOImpl;
import DAO.impl.SensorDAOImpl;
import DAO.impl.TopologiesDAOImpl;

public class FactoryDAO {
    private static FiberDAO fiberDAO = null;
    private static RelatedSensorsDAO relatedSensorsDAO = null;
    private static SensorDAO sensorDAO = null;
    private static TopologiesDAO topologiesDAO = null;
    private static FactoryDAO instance = null;

    public static synchronized FactoryDAO getInstance() {
        if (instance == null) {
            instance = new FactoryDAO();
        }
        return instance;
    }

    public FiberDAO getFiberDAO() {
        if (fiberDAO == null) {
            fiberDAO = new FiberDAOImpl();
        }
        return fiberDAO;
    }

    public RelatedSensorsDAO getRelatedSensorsDAO() {
        if (relatedSensorsDAO == null) {
            relatedSensorsDAO = new RelatedSensorsDAOImpl();
        }
        return relatedSensorsDAO;
    }

    public SensorDAO getSensorDAO() {
        if (sensorDAO == null) {
            sensorDAO = new SensorDAOImpl();
        }
        return sensorDAO;
    }

    public TopologiesDAO getTopologiesDAO() {
        if (topologiesDAO == null) {
            topologiesDAO = new TopologiesDAOImpl();
        }
        return topologiesDAO;
    }
}
