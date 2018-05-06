package DAO;

import model.TopologiesEntity;

import java.sql.SQLException;
import java.util.Collection;

public interface TopologiesDAO {
    public void addTopology(TopologiesEntity topologiesEntity) throws SQLException;

    public void updateTopology(TopologiesEntity topologiesEntity) throws SQLException;

    public void deleteTopology(TopologiesEntity topologiesEntity) throws SQLException;

    public TopologiesEntity getTopologyById(Long id) throws SQLException;

    public Collection<TopologiesEntity> getAllTopologies() throws SQLException;
}
