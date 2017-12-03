package database;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

/**
 * Name: Kenneth Korcal
 * Date: 12/03/2017
 * Description:
 *
 * Loads environment variables, sets them to hibernate config file, creates hibernate session
 *
 */

/**
 * PLEASE READ: in order to connect to the local database, please follow the steps below
 *
 * 1. login to mysql through the terminal
 *
 * 2. open the schema directory and copy/paste the database.sql and inventory.sql into the mysql terminal
 *
 * 3. create a hibernate.properties file in the same directory as hibernate.config.xml
 * and define the USERNAME, PASSWORD, AND DATABASE_NAME parameters:
 *
 * hibernate.connection.driver_class=com.mysql.jdbc.Driver
 * hibernate.connection.url=jdbc:mysql://localhost:3306/DATABASE_NAME
 * hibernate.connection.username=USERNAME
 * hibernate.connection.password=PASSWORD
 *
 * 4. Please do not check the hibernate.properties file into github as it contains private info.
 * I added this into the .gitignore so it should be fine but I just wanted to give a heads up.
 *
 * 5. the connection is successful if the GUI appears with no errors
 * */
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
            sessionFactory = configuration
                    .addAnnotatedClass(Inventory.class)
                    .buildSessionFactory(serviceRegistry);
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
