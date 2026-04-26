package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HblPostRepository implements PostRepository {

    private final SessionFactory sf;

    @Override
    public Post create(Post post) {
        try (var session = sf.openSession()) {
            session.beginTransaction();
            session.save(post);
            session.getTransaction().commit();
            return post;
        }
    }

    @Override
    public void delete(int id) {
        try (var session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE Post WHERE id =:id")
                    .setParameter("id", id).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public void edit(Post postFromSession) {
        try (var session = sf.openSession()) {
            session.beginTransaction();
            var postInDb = session.get(Post.class, postFromSession.getId());
            postInDb.setPhoto(postFromSession.getPhoto());
            postInDb.setDescription(postFromSession.getDescription());
            postInDb.getCar().setBrand(postFromSession.getCar().getBrand());
            postInDb.getCar().setEngine(postFromSession.getCar().getEngine());
            session.getTransaction().commit();
        }
    }

    @Override
    public Optional<Post> findById(int id) {
        try (var session = sf.openSession()) {
            var criteriaBuilder = session.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Post.class);
            Root<Post> root = criteriaQuery.from(Post.class);
            criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
            Query<Post> sessionQuery = session.createQuery(criteriaQuery);
            return Optional.of(sessionQuery.getResultList().get(0));
        }
    }

    @Override
    public void sellCar(int id) {
        try (var session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("UPDATE Post SET sold = :sold WHERE id = :id AND sold <> :sold")
                    .setParameter("id", id)
                    .setParameter("sold", true).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Post> getAllPosts() {
        try (var session = sf.openSession()) {
            var criteriaBuilder = session.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Post.class);
            Root<Post> root = criteriaQuery.from(Post.class);
            criteriaQuery.select(root);
            Query<Post> sessionQuery = session.createQuery(criteriaQuery);
            return sessionQuery.getResultList();
        }
    }

    @Override
    public List<Post> showPostsForTheLastDay() {
        try (Session session = sf.openSession()) {
            var criteriaBuilder = session.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Post.class);
            Root<Post> root = criteriaQuery.from(Post.class);

            LocalDateTime lastDay = LocalDateTime.now().minusDays(1);
            criteriaQuery.select(root)
                    .where(criteriaBuilder.greaterThanOrEqualTo(root.get("created"), lastDay))
                    .distinct(true);

            Query<Post> sessionQuery = session.createQuery(criteriaQuery);
            return sessionQuery.getResultList();
        }
    }

    @Override
    public List<Post> postsWithPhoto() {
        try (Session session = sf.openSession()) {
            var criteriaBuilder = session.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Post.class);
            Root<Post> root = criteriaQuery.from(Post.class);
            root.fetch("car");
            criteriaQuery.select(root)
                    .where(criteriaBuilder.isNotNull(root.get("photo")))
                    .distinct(true);
            Query<Post> sessionQuery = session.createQuery(criteriaQuery);
            return sessionQuery.getResultList();
        }
    }

    @Override
    public List<Post> postsWithSpecialCarModel(String carModel) {
        try (Session session = sf.openSession()) {
            var criteriaBuilder = session.getCriteriaBuilder();
            var query = criteriaBuilder.createQuery(Post.class);
            Root<Post> root = query.from(Post.class);

            root.fetch("car", JoinType.LEFT);
            query.select(root)
                    .where(criteriaBuilder.equal(root.get("car").get("brand").get("name"), carModel))
                    .distinct(true);

            Query<Post> sessionQuery = session.createQuery(query);
            return sessionQuery.getResultList();
        }
    }
}