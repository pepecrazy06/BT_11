package vn.iotstar.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import vn.iotstar.entity.Author;
import vn.iotstar.util.JPAUtil;

import java.util.List;

public class AuthorDAO {

    // 6 tác giả/trang
    public List<Author> findAll(int page) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT a FROM Author a ORDER BY a.author_id DESC",
                    Author.class
            )
            .setFirstResult((page - 1) * 6)
            .setMaxResults(6)
            .getResultList();

        } finally {
            em.close();
        }
    }

    // Giữ lại hàm cũ nếu nơi khác đang sử dụng
    public List<Author> findAll() {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT a FROM Author a ORDER BY a.author_id DESC",
                    Author.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // Đếm tổng tác giả
    public long count() {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT COUNT(a) FROM Author a",
                    Long.class
            ).getSingleResult();

        } finally {
            em.close();
        }
    }

    public Author findById(int id) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Author.class, id);

        } finally {
            em.close();
        }
    }

    public void save(Author author) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tr = em.getTransaction();

        try {

            tr.begin();

            if (author.getAuthor_id() == null) {
                em.persist(author);
            } else {
                em.merge(author);
            }

            tr.commit();

        } catch (Exception e) {

            if (tr.isActive()) {
                tr.rollback();
            }

            throw new IllegalStateException("Không thể lưu dữ liệu. Kiểm tra thông tin liên quan.", e);

        } finally {
            em.close();
        }
    }

    public void delete(int id) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tr = em.getTransaction();

        try {

            tr.begin();

            Author author = em.find(Author.class, id);

            if (author != null) {
                em.remove(author);
            }

            tr.commit();

        } catch (Exception e) {

            if (tr.isActive()) {
                tr.rollback();
            }

            throw new IllegalStateException("Không thể lưu dữ liệu. Kiểm tra thông tin liên quan.", e);

        } finally {
            em.close();
        }
    }
}