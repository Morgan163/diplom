package controller;

import DAO.*;
import model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseController {
    private TopologiesDAO topologiesDAO = FactoryDAO.getInstance().getTopologiesDAO();
    private SensorDAO sensorDAO = FactoryDAO.getInstance().getSensorDAO();
    private RelatedSensorsDAO relatedSensorsDAO = FactoryDAO.getInstance().getRelatedSensorsDAO();
    private FiberDAO fiberDAO = FactoryDAO.getInstance().getFiberDAO();

    public List<TopologyUtil> getTopologies() throws SQLException {
        List<TopologyUtil> topologyUtils = new ArrayList<>();
        Collection<TopologiesEntity> topologiesEntities = topologiesDAO.getAllTopologies();
        for (TopologiesEntity topologiesEntity:topologiesEntities){
            TopologyUtil topologyUtil = new TopologyUtil();
            topologyUtil.setTopologiesEntity(topologiesEntity);
            topologyUtil.setRelatedSensorsEntitySet(relatedSensorsDAO.getRelatedSensorsByTopology(topologiesEntity));
            topologyUtils.add(topologyUtil);
        }
        return topologyUtils;
    }

    public void saveSensor(SensorEntity sensorEntity) throws SQLException {
        sensorDAO.addSensor(sensorEntity);
    }

    public void saveFiber(FiberEntity fiberEntity) throws SQLException {
        fiberDAO.addFiber(fiberEntity);
    }

    public void saveRelatedSensors(RelatedSensorsEntity relatedSensorsEntity) throws SQLException {
        relatedSensorsDAO.addRelatedSensors(relatedSensorsEntity);
    }

    public void saveTopology(TopologyUtil topologyUtil) throws SQLException {
        topologiesDAO.addTopology(topologyUtil.getTopologiesEntity());
    }
}