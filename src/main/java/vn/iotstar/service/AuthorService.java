package vn.iotstar.service;

import java.util.List;

import vn.iotstar.dao.AuthorDAO;
import vn.iotstar.entity.Author;

public class AuthorService {

    private AuthorDAO authorDAO = new AuthorDAO();

    // 6 tác giả/trang
    public List<Author> findAll(int page) {
        return authorDAO.findAll(page);
    }

    // Tổng số tác giả
    public long count() {
        return authorDAO.count();
    }

    public Author findById(int id) {
        return authorDAO.findById(id);
    }

    public void save(Author author) {
        authorDAO.save(author);
    }

    public void delete(int id) {
        authorDAO.delete(id);
    }
}