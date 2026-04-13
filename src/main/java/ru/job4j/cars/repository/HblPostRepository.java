package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@AllArgsConstructor
public class HblPostRepository implements PostRepository {

    private final SessionFactory sf;

    @Override
    public List<Post> showPostsForTheLastDay() {
        try (Session session = sf.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
            Root<Post> root = criteriaQuery.from(Post.class);

            Timestamp lastDay = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Post> query = criteriaBuilder.createQuery(Post.class);
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