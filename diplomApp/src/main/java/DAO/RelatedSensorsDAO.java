package DAO;

import model.RelatedSensorsEntity;
import model.TopologiesEntity;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface RelatedSensorsDAO {
    public void addRelatedSensors(RelatedSensorsEntity relatedSensorsEntity) throws SQLException;

    public void updateRelatedSensors(RelatedSensorsEntity relatedSensorsEntity) throws SQLException;

    public void deleteRelatedSensors(RelatedSensorsEntity relatedSensorsEntity) throws SQLException;

    public List getRelatedSensorsByTopology(TopologiesEntity topologiesEntity)
            throws SQLException;

    public Collection<RelatedSensorsEntity> getAllRelatedSensors() throws SQLException;

    public RelatedSensorsEntity getRElatedSensorsById(Long id) throws SQLException;
}
