package vn.iotstar.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import vn.iotstar.entity.Rating;
import vn.iotstar.util.JPAUtil;

public class RatingDAO {

    public void save(Rating rating) {

        EntityManager em = JPAUtil.getEntityManager();

        EntityTransaction transaction =
                em.getTransaction();

        try {

            transaction.begin();

            em.persist(rating);

            transaction.commit();

        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new IllegalStateException("Không thể lưu dữ liệu. Kiểm tra thông tin liên quan.", e);

        } finally {

            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}