import models.KeyListEntity;
import models.UserProfileEntity;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

/**
 * Created by trevorBye on 8/29/16.
 */
public class Main {
    private static final SessionFactory ourSessionFactory;
    private static final ServiceRegistry serviceRegistry;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            ourSessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void main(final String[] args) throws Exception {
        staticFileLocation("/public");


        get("/", (request, response) -> {
            return new ModelAndView(null, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/sign-up", (request, response) -> {
            return new ModelAndView(null, "sign-up.hbs");
        }, new HandlebarsTemplateEngine());

        get("/log-in", (request, response) -> {
            return new ModelAndView(null, "log-in.hbs");
        }, new HandlebarsTemplateEngine());


        post("/user-home", (request, response) -> {
            String pageURL;
            String email = request.queryParams("emailInput");
            String password = request.queryParams("passwordInput");
            List<UserProfileEntity> userList = userProfiles();
            Boolean authenticated = false;

                //query all user profiles in database and cross-check against input
                for (UserProfileEntity user : userList) {
                    String queryEmail = user.getEmail();
                    String queryPass = user.getPassword();

                    if (queryEmail.equals(email)) {
                        if (queryPass.equals(password)) {
                            authenticated = true;
                            break;
                        }
                    }
                }

                //direct back to login if credentials are invalid
                if (authenticated) {
                    pageURL = "user-home.hbs";
                } else {
                    pageURL = "log-in.hbs";
                }

            return new ModelAndView(null, pageURL);
        }, new HandlebarsTemplateEngine());



        post("/new-user", (request, response) -> {

            String keyToRemove = null;
            String pageURL;
            Boolean authenticated1 = false;
            Boolean authenticated2 = false;
            String keyInput = request.queryParams("keyInput");
            String emailInput = request.queryParams("emailInput");
            String firstInput = request.queryParams("firstInput");
            String lastInput = request.queryParams("lastInput");
            String password1 = request.queryParams("password1");
            String password2 = request.queryParams("password2");

            //loop through security keys in database and check for a match
            List<KeyListEntity> keys = queryKeyList();
            for (KeyListEntity key : keys) {
                String currentKey = key.getKeyNumber();
                if (keyInput.equals(currentKey)) {
                    authenticated1 = true;
                    keyToRemove = currentKey;
                    break;
                }
            }


            //check password length and match
            if (password1.equals(password2) && password2.length() <= 20 && password2.length() >= 6) {
                authenticated2 = true;
            }

            //if fully authenticated, add user to database. else, redirect back to sign-up
            if (authenticated1 && authenticated2) {
                saveUserProfile(firstInput, lastInput, emailInput, password2);
                deleteKey(keyToRemove);
                pageURL = "new-user.hbs";
            } else {
                pageURL = "sign-up.hbs";
            }

            return new ModelAndView(null, pageURL);
        }, new HandlebarsTemplateEngine());

        get("/new-user", (request, response) -> {
            return new ModelAndView(null, "new-user.hbs");
        }, new HandlebarsTemplateEngine());
    }



    //HIBERNATE methods
    //method to query all security keys in database; compare to user input to authenticate
    @SuppressWarnings("unchecked")
    private static List<KeyListEntity> queryKeyList() throws HibernateException{
        String hql = "FROM KeyListEntity";
        Session session = getSession();
        session.beginTransaction();
        Query query = session.createQuery(hql);
        List<KeyListEntity> list = query.list();
        session.close();
        return list;
    }

    private static void deleteKey(String key) throws HibernateException{
        String hql = "DELETE FROM KeyListEntity WHERE keyNumber= :keyNumber";
        Session session = getSession();
        session.beginTransaction();
        Query query = session.createQuery(hql).setParameter("keyNumber", key);
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    private static void saveUserProfile(String first, String last, String email, String password) throws HibernateException{
        UserProfileEntity user = new UserProfileEntity();
        user.setId("1");
        user.setFirstName(first);
        user.setLastName(last);
        user.setEmail(email);
        user.setPassword(password);
        Session session = getSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    private static List<UserProfileEntity> userProfiles() throws HibernateException{
        String hql = "FROM UserProfileEntity";
        Session session = getSession();
        session.beginTransaction();
        Query query = session.createQuery(hql);
        List<UserProfileEntity> list = query.list();
        session.close();
        return list;
    }













}
