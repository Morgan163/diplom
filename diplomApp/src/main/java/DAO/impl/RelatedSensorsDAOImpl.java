package DAO.impl;

import DAO.RelatedSensorsDAO;
import DAO.impl.daoUtils.DaoUtils;
import model.RelatedSensorsEntity;
import model.TopologiesEntity;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class RelatedSensorsDAOImpl implements RelatedSensorsDAO {
    private DaoUtils daoUtils;

    public RelatedSensorsDAOImpl() {
        daoUtils = new DaoUtils();
    }

    public void addRelatedSensors(RelatedSensorsEntity relatedSensorsEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.save(relatedSensorsEntity);
        daoUtils.closeSession(session);
    }

    public void updateRelatedSensors(RelatedSensorsEntity relatedSensorsEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.update(relatedSensorsEntity);
        daoUtils.closeSession(session);
    }

    public void deleteRelatedSensors(RelatedSensorsEntity relatedSensorsEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.delete(relatedSensorsEntity);
        daoUtils.closeSession(session);
    }

    public List getRelatedSensorsByTopology(TopologiesEntity topologiesEntity)
            throws SQLException {
        Session session = daoUtils.createTransaction();
        String sql = "SELECT rs.ID, rs.SENSOR1_ID, rs.FIBER_ID, rs.SENSOR2_ID,  LEVEL " +
                "  FROM RELATED_SENSORS rs " +
                "  CONNECT BY rs.SENSOR1_ID = PRIOR rs.SENSOR2_ID " +
                "  START WITH rs.SENSOR1_ID = (SELECT t.SENSOR " +
                "                              FROM TOPOLOGIES t " +
                "                              WHERE t.ID="+topologiesEntity.getSensorBySensor().getId()+");";
        List relatedSensorsEntities = session.createNativeQuery(sql).
                addEntity(RelatedSensorsEntity.class).list();
        daoUtils.closeSession(session);
        return relatedSensorsEntities;
    }

    public Collection<RelatedSensorsEntity> getAllRelatedSensors() throws SQLException {
        Session session = daoUtils.createTransaction();
        CriteriaQuery<RelatedSensorsEntity> criteriaQuery = session.getCriteriaBuilder()
                .createQuery(RelatedSensorsEntity.class);
        criteriaQuery.from(RelatedSensorsEntity.class);
        List<RelatedSensorsEntity> relatedSensorsEntities = session.createQuery(criteriaQuery).getResultList();
        daoUtils.closeSession(session);
        return relatedSensorsEntities;
    }

    public RelatedSensorsEntity getRElatedSensorsById(Long id) throws SQLException {
        Session session = daoUtils.createTransaction();
        RelatedSensorsEntity relatedSensorsEntity = session.load(RelatedSensorsEntity.class,id);
        daoUtils.closeSession(session);
        return relatedSensorsEntity;
    }
}
