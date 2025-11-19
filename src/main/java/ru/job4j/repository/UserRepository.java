package ru.job4j.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ru.job4j.model.User;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UserRepository {

    private final SessionFactory sessionFactory;

    public User create(User user) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return user;
    }

    public void update(User user) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.createQuery("UPDATE User SET login = :login, password = :password WHERE id = :id")
                    .setParameter("login", user.getLogin())
                    .setParameter("password", user.getPassword())
                    .setParameter("id", user.getId())
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    public void delete(Integer userId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.createQuery("DELETE User WHERE id = :id")
                    .setParameter("id", userId)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    public List<User> findAllOrderById() {
        Session session = sessionFactory.openSession();
        try {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    public Optional<User> findById(Integer userId) {
        Session session = sessionFactory.openSession();
        try {
            Query<User> query = session.createQuery("FROM User u WHERE u.id = :id", User.class)
                    .setParameter("id", userId);
            return Optional.ofNullable(query.uniqueResult());
        } finally {
            session.close();
        }
    }

    public List<User> findByLikeLogin(String key) {
        Session session = sessionFactory.openSession();
        try {
            Query<User> query = session.createQuery("FROM User u WHERE u.login LIKE :key", User.class)
                    .setParameter("key", "%" + key + "%");
            return query.list();
        } finally {
            session.close();
        }
    }

    public List<User> findByLogin(String login) {
        Session session = sessionFactory.openSession();
        try {
            Query<User> query = session.createQuery("FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login);
            return query.list();
        } finally {
            session.close();
        }
    }
}
