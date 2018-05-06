package DAO;

import model.FiberEntity;
import model.SensorEntity;

import java.sql.SQLException;
import java.util.Collection;

public interface SensorDAO {
    public void addSensor(SensorEntity sensorEntity) throws SQLException;

    public void updateSensor(SensorEntity sensorEntity) throws SQLException;

    public void deleteSensor(SensorEntity sensorEntity) throws SQLException;

    public SensorEntity getSensorById(Long id) throws SQLException;

    public Collection<SensorEntity> getAllSensors() throws SQLException;
}
