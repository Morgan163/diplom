package DAO.impl;

import DAO.TopologiesDAO;
import DAO.impl.daoUtils.DaoUtils;
import model.SensorEntity;
import model.TopologiesEntity;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class TopologiesDAOImpl implements TopologiesDAO {
    private DaoUtils daoUtils;

    public TopologiesDAOImpl() {
        daoUtils = new DaoUtils();
    }

    public void addTopology(TopologiesEntity topologiesEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.save(topologiesEntity);
        daoUtils.closeSession(session);
    }

    public void updateTopology(TopologiesEntity topologiesEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.update(topologiesEntity);
        daoUtils.closeSession(session);
    }

    public void deleteTopology(TopologiesEntity topologiesEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.delete(topologiesEntity);
        daoUtils.closeSession(session);
    }

    public TopologiesEntity getTopologyById(Long id) throws SQLException {
        Session session = daoUtils.createTransaction();
        TopologiesEntity topologiesEntity = session.load(TopologiesEntity.class,id);
        daoUtils.closeSession(session);
        return topologiesEntity;
    }

    public Collection<TopologiesEntity> getAllSensors() throws SQLException {
        Session session = daoUtils.createTransaction();
        CriteriaQuery<TopologiesEntity> criteriaQuery = session.getCriteriaBuilder()
                .createQuery(TopologiesEntity.class);
        criteriaQuery.from(TopologiesEntity.class);
        List<TopologiesEntity> topologiesEntities = session.createQuery(criteriaQuery).getResultList();
        daoUtils.closeSession(session);
        return topologiesEntities;
    }
}
