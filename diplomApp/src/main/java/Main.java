
import DAO.FactoryDAO;
import DAO.RelatedSensorsDAO;
import DAO.SensorDAO;
import DAO.TopologiesDAO;
import model.RelatedSensorsEntity;
import model.SensorEntity;
import model.TopologiesEntity;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Main {
   /* private static final SessionFactory ourSessionFactory;

     *//* static {
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
    }*//*

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }*/

    public static void main(final String[] args) throws Exception {

        SensorEntity sensorEntity = new SensorEntity();
        sensorEntity.setWave(654L);
        SensorDAO sensorDAO = FactoryDAO.getInstance().getSensorDAO();
        sensorDAO.addSensor(sensorEntity);
        /*TopologiesDAO topologiesDAO = FactoryDAO.getInstance().getTopologiesDAO();
        RelatedSensorsDAO relatedSensorsDAO = FactoryDAO.getInstance().getRelatedSensorsDAO();
        List<TopologiesEntity> topologiesEntitySet = (List<TopologiesEntity>) topologiesDAO.getAllTopologies();
        for(TopologiesEntity topologiesEntity:topologiesEntitySet){
            List<RelatedSensorsEntity> relatedSensorsEntitySet = (List<RelatedSensorsEntity>) relatedSensorsDAO.
                    getRelatedSensorsByTopology(topologiesEntity);
            for (RelatedSensorsEntity relatedSensorsEntity:relatedSensorsEntitySet){
                System.out.print(relatedSensorsEntity.getId());
                System.out.print(relatedSensorsEntity.getFiberByFiberId().getId());
                System.out.print(relatedSensorsEntity.getSensorBySensor1Id().getId());
                System.out.print(relatedSensorsEntity.getSensorBySensor2Id().getId());
                System.out.println();
            }

        }*/
      /*  final Session session = getSession();
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
        }*/

       /* Class.forName("oracle.jdbc.OracleDriver");
        String URL = "jdbc:oracle:thin:@//localhost:1521/xe";
       *//* DriverManager.registerDriver(new OracleDriver());*//*
        Connection conn = DriverManager.getConnection(URL, "system", "253634");
        conn.close();*/
    }
}