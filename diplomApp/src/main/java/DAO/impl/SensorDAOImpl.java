package DAO.impl;

import DAO.SensorDAO;
import DAO.impl.daoUtils.DaoUtils;
import model.SensorEntity;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SensorDAOImpl implements SensorDAO {
    private DaoUtils daoUtils;

    public SensorDAOImpl() {
        daoUtils = new DaoUtils();
    }

    public void addSensor(SensorEntity sensorEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.saveOrUpdate(sensorEntity);
        daoUtils.closeSession(session);
    }

    public void updateSensor(SensorEntity sensorEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.update(sensorEntity);
        daoUtils.closeSession(session);
    }

    public void deleteSensor(SensorEntity sensorEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.delete(sensorEntity);
        daoUtils.closeSession(session);
    }

    public SensorEntity getSensorById(Long id) throws SQLException {
        Session session = daoUtils.createTransaction();
        SensorEntity sensorEntity = session.load(SensorEntity.class, id);
        daoUtils.closeSession(session);
        return sensorEntity;
    }

    public Collection<SensorEntity> getAllSensors() throws SQLException {
        Session session = daoUtils.createTransaction();
        CriteriaQuery<SensorEntity> criteriaQuery = session.getCriteriaBuilder()
                .createQuery(SensorEntity.class);
        criteriaQuery.from(SensorEntity.class);
        List<SensorEntity> sensorEntities = session.createQuery(criteriaQuery).getResultList();
        daoUtils.closeSession(session);
        return sensorEntities;
    }
}
