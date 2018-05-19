package controller;

import DAO.*;
import model.*;

import java.sql.SQLException;
import java.util.*;

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

    public void deleteTopology(TopologyUtil topologyUtil) throws SQLException {
        List<FiberEntity> fiberEntities = getFibersByTopology(topologyUtil);
        List<SensorEntity> sensorEntities = getSensorsByTopology(topologyUtil);
        List<RelatedSensorsEntity>relatedSensorsEntities = topologyUtil.getRelatedSensorsEntitySet();
        topologiesDAO.deleteTopology(topologyUtil.getTopologiesEntity());
        for(RelatedSensorsEntity relatedSensorsEntity:relatedSensorsEntities){
            relatedSensorsDAO.deleteRelatedSensors(relatedSensorsEntity);
        }
        for(SensorEntity sensorEntity:sensorEntities){
            sensorDAO.deleteSensor(sensorEntity);
        }
        for(FiberEntity f :fiberEntities){
            fiberDAO.deleteFiber(f);
        }
    }

    public List<SensorEntity> getSensorsByTopology(TopologyUtil topologyUtil){
        Set<SensorEntity> sensorEntities = new HashSet<>();
        for(RelatedSensorsEntity relatedSensorsEntity:topologyUtil.getRelatedSensorsEntitySet()){
            sensorEntities.add(relatedSensorsEntity.getSensorBySensor1Id());
            sensorEntities.add(relatedSensorsEntity.getSensorBySensor2Id());
        }
        List<SensorEntity> list = new ArrayList<>(sensorEntities);
        list.remove(topologyUtil.getTopologiesEntity().getSensorBySensor());
        list.add(0, topologyUtil.getTopologiesEntity().getSensorBySensor());
        return list;
    }

    public List<FiberEntity> getFibersByTopology(TopologyUtil topologyUtil){
        Set<FiberEntity> fiberEntities = new HashSet<>();
        for(RelatedSensorsEntity relatedSensorsEntity:topologyUtil.getRelatedSensorsEntitySet()){
            fiberEntities.add(relatedSensorsEntity.getFiberByFiberId());
        }
        return new ArrayList<>(fiberEntities);
    }
}
