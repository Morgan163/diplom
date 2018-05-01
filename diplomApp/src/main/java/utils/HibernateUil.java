package utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUil {
    private static final SessionFactory SESSION_FACTORY;
    static {
        try {
            SESSION_FACTORY = new Configuration().configure().buildSessionFactory();
        }catch (Throwable o_O) {
            System.err.println("Initial SessionFactory creation failed." + o_O);
            throw new ExceptionInInitializerError(o_O);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}
