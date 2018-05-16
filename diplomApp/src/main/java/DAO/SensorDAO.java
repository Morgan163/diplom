package DAO;

import model.FiberEntity;
import model.SensorEntity;
import model.TopologiesEntity;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface SensorDAO {
    public void addSensor(SensorEntity sensorEntity) throws SQLException;

    public void updateSensor(SensorEntity sensorEntity) throws SQLException;

    public void deleteSensor(SensorEntity sensorEntity) throws SQLException;

    public SensorEntity getSensorById(Long id) throws SQLException;

    public Collection<SensorEntity> getAllSensors() throws SQLException;
}
