
import oracle.jdbc.OracleDriver;
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.metamodel.EntityType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Locale;
import java.util.Map;

public class Main {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Locale.setDefault(Locale.ENGLISH);
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                    configuration.getProperties()).build();
            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }
    public static void main(final String[] args) throws Exception {

        final Session session = getSession();
        try {
            System.out.println("querying all the managed entities...");
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            for (EntityType<?> entityType : metamodel.getEntities()) {
                final String entityName = entityType.getName();
                final Query query = session.createQuery("from " + entityName);
                System.out.println("executing: " + query.getQueryString());
                for (Object o : query.list()) {
                    System.out.println("  " + o);
                }
            }
        } finally {
            session.close();
        }

       /* Class.forName("oracle.jdbc.OracleDriver");
        String URL = "jdbc:oracle:thin:@//localhost:1521/xe";
       *//* DriverManager.registerDriver(new OracleDriver());*//*
        Connection conn = DriverManager.getConnection(URL, "system", "253634");
        conn.close();*/
    }
}