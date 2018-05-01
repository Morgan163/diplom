package DAO.impl.daoUtils;

import org.hibernate.Session;
import utils.HibernateUil;

import java.sql.SQLException;

public class DaoUtils {
    public Session createTransaction() throws SQLException {
        Session session = null;
        session = HibernateUil.getSessionFactory().openSession();
        session.beginTransaction();
        if(session==null){
            throw new SQLException("Соединение с базой данных не установлено");
        }
        return session;
    }

    public void closeSession(Session session){
        session.getTransaction().commit();
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}
