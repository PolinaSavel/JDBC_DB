package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Servlet")
public class StudentServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5433/students";
    private static final String USER = "students";
    private static final String PASSWORD = "students";

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Регистрация драйвера PostgreSQL
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("Не удалось загрузить драйвер PostgreSQL.", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM students");
             ResultSet rs = stmt.executeQuery()) {

            out.println("<h1>Студенты</h1>");
            out.println("<table border='1'><tr><th>ID</th><th>Имя</th></tr>");
            while (rs.next()) {
                out.println("<tr><td>" + rs.getInt("id") + "</td><td>" + rs.getString("name") + "</td></tr>");
            }
            out.println("</table>");

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Ошибка при подключении к базе данных");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentName = request.getParameter("name");

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO students (name) VALUES (?)")) {

            pstmt.setString(1, studentName);
            pstmt.executeUpdate();
            response.sendRedirect("students");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}