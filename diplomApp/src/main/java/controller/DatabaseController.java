package controller;

import DAO.*;
import model.RelatedSensorsEntity;
import model.TopologiesEntity;
import model.TopologyUtil;

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
}
