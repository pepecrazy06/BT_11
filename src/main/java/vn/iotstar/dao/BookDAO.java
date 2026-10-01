package vn.iotstar.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import vn.iotstar.entity.Book;
import vn.iotstar.util.JPAUtil;

import java.util.List;

public class BookDAO {

    // Lấy sách cho trang Home - 6 sách/trang
    public List<Book> findAll(int page) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT b FROM Book b ORDER BY b.bookid DESC",
                    Book.class
            )
            .setFirstResult((page - 1) * 6)
            .setMaxResults(6)
            .getResultList();

        } finally {
            em.close();
        }
    }

    // Lấy sách cho Admin - 6 sách/trang
    public List<Book> findAllAdmin(int page) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT b FROM Book b ORDER BY b.bookid DESC",
                    Book.class
            )
            .setFirstResult((page - 1) * 6)
            .setMaxResults(6)
            .getResultList();

        } finally {
            em.close();
        }
    }

    // Đếm tổng số sách
    public long count() {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT COUNT(b) FROM Book b",
                    Long.class
            ).getSingleResult();

        } finally {
            em.close();
        }
    }

    public Book findById(int id) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Book.class, id);

        } finally {
            em.close();
        }
    }

    public void save(Book book) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tr = em.getTransaction();

        try {

            tr.begin();

            if (book.getBookid() == null) {
                em.persist(book);
            } else {
                em.merge(book);
            }

            tr.commit();

        } catch (Exception e) {

            if (tr.isActive()) {
                tr.rollback();
            }

            throw new IllegalStateException("Không thể lưu hoặc xóa sách. Kiểm tra các dữ liệu liên quan.", e);

        } finally {
            em.close();
        }
    }

    public void delete(int id) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tr = em.getTransaction();

        try {

            tr.begin();

            Book book = em.find(Book.class, id);

            if (book != null) {
                em.remove(book);
            }

            tr.commit();

        } catch (Exception e) {

            if (tr.isActive()) {
                tr.rollback();
            }

            throw new IllegalStateException("Không thể lưu hoặc xóa sách. Kiểm tra các dữ liệu liên quan.", e);

        } finally {
            em.close();
        }
    }

    public List<Book> search(String q, boolean available, String sort, int page) {
        EntityManager em=JPAUtil.getEntityManager();
        try {
            String order=switch(sort){case "price-asc" -> "b.price asc, b.bookid";case "price-desc" -> "b.price desc, b.bookid";case "title" -> "b.title, b.bookid";default -> "b.bookid desc";};
            return em.createQuery("select distinct b from Book b left join b.authors a where (lower(b.title) like :q escape '!' or lower(b.isbn) like :q escape '!' or lower(a.author_name) like :q escape '!')"+(available?" and b.quantity>0 and b.price>=0":"")+" order by "+order,Book.class)
                .setParameter("q",pattern(q)).setFirstResult((page-1)*8).setMaxResults(8).getResultList();
        } finally {em.close();}
    }
    public long searchCount(String q,boolean available) {
        EntityManager em=JPAUtil.getEntityManager();
        try{return em.createQuery("select count(distinct b) from Book b left join b.authors a where (lower(b.title) like :q escape '!' or lower(b.isbn) like :q escape '!' or lower(a.author_name) like :q escape '!')"+(available?" and b.quantity>0 and b.price>=0":""),Long.class).setParameter("q",pattern(q)).getSingleResult();}finally{em.close();}
    }
    private String pattern(String q){return "%"+q.toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";}
}