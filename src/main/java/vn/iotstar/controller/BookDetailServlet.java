package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

import vn.iotstar.entity.Book;
import vn.iotstar.service.BookService;

@WebServlet("/detail")
public class BookDetailServlet extends HttpServlet {

    private BookService bookService = new BookService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {

            response.sendRedirect(
                    request.getContextPath() + "/home"
            );

            return;
        }

        try {

            int id = Integer.parseInt(idParam);

            Book book = bookService.findById(id);

            if (book == null) {

                response.sendRedirect(
                        request.getContextPath() + "/home"
                );

                return;
            }

            request.setAttribute(
                    "book",
                    book
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/detail.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    request.getContextPath() + "/home"
            );
        }
    }
}