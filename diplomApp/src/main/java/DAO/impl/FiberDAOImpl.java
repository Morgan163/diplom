package DAO.impl;

import DAO.FiberDAO;
import DAO.impl.daoUtils.DaoUtils;
import model.FiberEntity;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class FiberDAOImpl implements FiberDAO {
    private DaoUtils daoUtils;

    public FiberDAOImpl() {
        daoUtils = new DaoUtils();
    }

    public void addFiber(FiberEntity fiberEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.save(fiberEntity);
        daoUtils.closeSession(session);
    }

    public void updateFiber(FiberEntity fiberEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.update(fiberEntity);
        daoUtils.closeSession(session);
    }

    public void deleteFiber(FiberEntity fiberEntity) throws SQLException {
        Session session = daoUtils.createTransaction();
        session.delete(fiberEntity);
        daoUtils.closeSession(session);
    }

    public FiberEntity getFiberById(Long id) throws SQLException {
        Session session = daoUtils.createTransaction();
        FiberEntity fiberEntity = session.load(FiberEntity.class, id);
        daoUtils.closeSession(session);
        return fiberEntity;
    }

    public Collection<FiberEntity> getAllFibers() throws SQLException {
        Session session = daoUtils.createTransaction();
        CriteriaQuery<FiberEntity> criteriaQuery = session.getCriteriaBuilder().createQuery(FiberEntity.class);
        criteriaQuery.from(FiberEntity.class);
        List<FiberEntity> fiberEntities = session.createQuery(criteriaQuery).getResultList();
        daoUtils.closeSession(session);
        return fiberEntities;
    }
}
