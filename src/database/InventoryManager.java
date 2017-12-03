package database;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

public class InventoryManager {

    private SessionFactory sessionFactory;

    // https://docs.jboss.org/hibernate/orm/current/quickstart/html_single/
    // took code from example 4 in link above
    public void setUp() throws Exception {
        // first load the env variables and set them into the hibernate config file
        Properties properties = new Properties();
        Configuration configuration = new Configuration();

        try {
            properties.load(new FileInputStream("src/hibernate.properties"));
        }catch(Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }

        configuration.configure("hibernate.config.xml");
        configuration.setProperty("hibernate.connection.driver_class", properties.getProperty("hibernate.connection.driver_class"));
        configuration.setProperty("hibernate.connection.url", properties.getProperty("hibernate.connection.url"));
        configuration.setProperty("hibernate.connection.username", properties.getProperty("hibernate.connection.username"));
        configuration.setProperty("hibernate.connection.password", properties.getProperty("hibernate.connection.password"));

        // A SessionFactory is set up once for an application!
        final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();

        try {
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }catch (Exception e) {
            e.printStackTrace();
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
            throw new Exception(e);
        }
    }

    public void exit() {
        sessionFactory.close();
    }

    public Inventory create(Inventory inventory) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(inventory);

        System.out.println("Inventory created: ");
        System.out.println(inventory.toString());

        session.getTransaction().commit();
        session.close();

        return inventory;
    }

    public Inventory read(long id) {
        Session session = sessionFactory.openSession();

        Inventory inventory = session.get(Inventory.class, id);

        if(inventory != null) {
            System.out.println("Inventory found: ");
            System.out.println(inventory.toString());
        }else {
            System.out.println("Inventory id: " + id + " does not exist");
        }

        session.close();

        return inventory;
    }

    public Inventory update(Inventory inventory) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        long id = inventory.getId();
        Inventory newInventory = session.get(Inventory.class, id);

        if(newInventory != null) {
            newInventory.setName(inventory.getName());
            newInventory.setQuantity(inventory.getQuantity());
            session.update(newInventory);
            System.out.println("Inventory updated: ");
            System.out.println(newInventory.toString());
        }else {
            System.out.println("Inventory id: " + id + " does not exist");
        }

        session.getTransaction().commit();
        session.close();

        return newInventory;
    }

    public Inventory delete(long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Inventory inventory = session.get(Inventory.class, id);

        if(inventory != null) {
            session.delete(inventory);
            System.out.println("Inventory deleted: ");
            System.out.println(inventory.toString());
        }else {
            System.out.println("Inventory id: " + id + " does not exist");
        }

        session.getTransaction().commit();
        session.close();

        return inventory;
    }

    public List<Inventory> list() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query<Inventory> query = session.createQuery("from Inventory", Inventory.class);
        List<Inventory> list = query.list();

        for(Inventory item: list) {
            System.out.println(item.toString());
        }

        session.getTransaction().commit();
        session.close();

        return list;
    }
}
