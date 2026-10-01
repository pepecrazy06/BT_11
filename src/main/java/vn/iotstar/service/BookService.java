package vn.iotstar.service;

import java.util.List;

import vn.iotstar.dao.BookDAO;
import vn.iotstar.entity.Book;

public class BookService {

    private BookDAO bookDAO = new BookDAO();

    // Home - 6 sách/trang
    public List<Book> findAll(int page) {
        return bookDAO.findAll(page);
    }

    // Tổng số sách
    public long count() {
        return bookDAO.count();
    }

    // Admin - 6 sách/trang
    public List<Book> findAllAdmin(int page) {
        return bookDAO.findAllAdmin(page);
    }

    public Book findById(int id) {
        return bookDAO.findById(id);
    }

    public void save(Book book) {
        bookDAO.save(book);
    }

    public void delete(int id) {
        bookDAO.delete(id);
    }
}