package com.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Basic server-side validation
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showMessage(response, "Missing Fields", "Please fill in both fields.", "register.html", "Try Again");
            return;
        }

        try {
            boolean created = new UserDAO().register(username, password);
            if (created) {
                showMessage(response, "Registration Successful",
                        "Your account has been created. Please log in.",
                        "index.html", "Go to Login");
            } else {
                showMessage(response, "Registration Failed",
                        "Could not create account. Please try again.",
                        "register.html", "Try Again");
            }

        } catch (SQLIntegrityConstraintViolationException dup) {
            showMessage(response, "Username Taken",
                    "That username is already registered.",
                    "register.html", "Try Another");

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage(response, "Database Error",
                    "Something went wrong. Please try again later.",
                    "register.html", "Back");
        }
    }

    private void showMessage(HttpServletResponse response, String title, String message,
                             String link, String buttonText) throws IOException {
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>" + title + "</title>");
        out.println("<style>");
        out.println("  * { box-sizing: border-box; margin: 0; padding: 0; }");
        out.println("  body {");
        out.println("    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;");
        out.println("    background-color: #f8fafc;");
        out.println("    display: flex;");
        out.println("    justify-content: center;");
        out.println("    align-items: center;");
        out.println("    min-height: 100vh;");
        out.println("    color: #1e293b;");
        out.println("  }");
        out.println("  .card {");
        out.println("    background: #ffffff;");
        out.println("    padding: 40px;");
        out.println("    border-radius: 12px;");
        out.println("    box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06);");
        out.println("    width: 100%;");
        out.println("    max-width: 380px;");
        out.println("    margin: 20px;");
        out.println("    text-align: center;");
        out.println("  }");
        out.println("  .card h1 { font-size: 22px; font-weight: 600; margin-bottom: 8px; color: #0f172a; }");
        out.println("  .card p { font-size: 14px; color: #64748b; margin-bottom: 28px; }");
        out.println("  .btn {");
        out.println("    display: inline-block;");
        out.println("    width: 100%;");
        out.println("    padding: 11px;");
        out.println("    font-size: 15px;");
        out.println("    font-weight: 500;");
        out.println("    color: #ffffff;");
        out.println("    background-color: #2563eb;");
        out.println("    border-radius: 6px;");
        out.println("    text-decoration: none;");
        out.println("    transition: background-color 0.2s;");
        out.println("  }");
        out.println("  .btn:hover { background-color: #1d4ed8; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("  <div class='card'>");
        out.println("    <h1>" + title + "</h1>");
        out.println("    <p>" + message + "</p>");
        out.println("    <a href='" + link + "' class='btn'>" + buttonText + "</a>");
        out.println("  </div>");

        out.println("</body>");
        out.println("</html>");
    }
}