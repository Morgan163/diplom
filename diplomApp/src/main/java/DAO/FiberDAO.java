package DAO;

import model.FiberEntity;

import java.sql.SQLException;
import java.util.Collection;

public interface FiberDAO {
    public void addFiber(FiberEntity fiberEntity) throws SQLException;
    public void updateFiber(FiberEntity fiberEntity) throws SQLException;
    public void deleteFiber(FiberEntity fiberEntity) throws SQLException;
    public FiberEntity getFiberById(Long id) throws SQLException;
    public Collection<FiberEntity> getAllFibers() throws SQLException;
}
