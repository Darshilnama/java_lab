package main.java.libraryapp;

import java.sql.*;
import java.time.LocalDate;

public class LibraryDAO {

    public void addBook(String title, String author, int copies, double rate) throws SQLException {
        String sql = """
            INSERT INTO books(title, author, total_copies, available_copies, rental_rate_per_day)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, copies);
            ps.setInt(4, copies);
            ps.setDouble(5, Math.max(0, rate));
            ps.executeUpdate();
        }
    }

    public void addCopies(int bookId, int copies) throws SQLException {
        String sql = """
            UPDATE books
            SET total_copies = total_copies + ?,
                available_copies = available_copies + ?
            WHERE book_id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, copies);
            ps.setInt(2, copies);
            ps.setInt(3, bookId);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Book ID not found.");
            }
        }
    }

    public ResultSet getAllBooks(Connection con) throws SQLException {
        return con.createStatement().executeQuery(
            "SELECT * FROM books ORDER BY book_id"
        );
    }

    public ResultSet searchBooks(Connection con, String query, String type) throws SQLException {
        String column = type.equalsIgnoreCase("author") ? "author" : "title";
        String sql = "SELECT * FROM books WHERE " + column + " LIKE ? ORDER BY book_id";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, "%" + query + "%");
        return ps.executeQuery();
    }

    public int borrowBook(int bookId, int requestedDays) throws SQLException {
        requestedDays = Math.max(1, requestedDays);

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int available;
                double rate;

                String bookSql =
                    "SELECT available_copies, rental_rate_per_day FROM books WHERE book_id=? FOR UPDATE";

                try (PreparedStatement ps = con.prepareStatement(bookSql)) {
                    ps.setInt(1, bookId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Book ID not found.");
                        }
                        available = rs.getInt("available_copies");
                        rate = rs.getDouble("rental_rate_per_day");
                    }
                }

                if (available <= 0) {
                    throw new SQLException("No copies available.");
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE books SET available_copies=available_copies-1 WHERE book_id=?")) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                String loanSql = """
                    INSERT INTO loans(book_id, checkout_date, allowed_days, renewals, active)
                    VALUES (?, ?, ?, 0, TRUE)
                    """;

                int loanId;

                try (PreparedStatement ps = con.prepareStatement(
                        loanSql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, bookId);
                    ps.setDate(2, Date.valueOf(LocalDate.now()));
                    ps.setInt(3, requestedDays);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("Could not create loan.");
                        }
                        loanId = rs.getInt(1);
                    }
                }

                con.commit();

                double cost = requestedDays * rate;
                return loanId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void renewLoan(int loanId, int extraDays) throws SQLException {
        if (extraDays <= 0) {
            throw new SQLException("Renewal days should be positive.");
        }

        String check = "SELECT renewals, active FROM loans WHERE loan_id=?";
        String update = """
            UPDATE loans
            SET allowed_days = allowed_days + ?,
                renewals = renewals + 1
            WHERE loan_id=? AND active=TRUE AND renewals < 2
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(check)) {

            ps.setInt(1, loanId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !rs.getBoolean("active")) {
                    throw new SQLException("Active loan ID not found.");
                }

                if (rs.getInt("renewals") >= 2) {
                    throw new SQLException("Maximum renewals reached.");
                }
            }

            try (PreparedStatement ps2 = con.prepareStatement(update)) {
                ps2.setInt(1, extraDays);
                ps2.setInt(2, loanId);

                if (ps2.executeUpdate() == 0) {
                    throw new SQLException("Renewal failed.");
                }
            }
        }
    }

    public String returnBook(int loanId) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int bookId;
                LocalDate checkout;
                int allowedDays;

                String loanSql =
                    "SELECT book_id, checkout_date, allowed_days, active FROM loans WHERE loan_id=? FOR UPDATE";

                try (PreparedStatement ps = con.prepareStatement(loanSql)) {
                    ps.setInt(1, loanId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !rs.getBoolean("active")) {
                            throw new SQLException("Active loan ID not found.");
                        }

                        bookId = rs.getInt("book_id");
                        checkout = rs.getDate("checkout_date").toLocalDate();
                        allowedDays = rs.getInt("allowed_days");
                    }
                }

                long diff = java.time.temporal.ChronoUnit.DAYS.between(checkout, LocalDate.now());
                int daysHeld = (int) Math.max(1, diff);
                int overdue = Math.max(0, daysHeld - allowedDays);
                double fine = overdue * 5.0;

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE loans SET active=FALSE, return_date=?, fine=? WHERE loan_id=?")) {
                    ps.setDate(1, Date.valueOf(LocalDate.now()));
                    ps.setDouble(2, fine);
                    ps.setInt(3, loanId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE books SET available_copies=available_copies+1 WHERE book_id=?")) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                con.commit();

                return "Book returned successfully.\n"
                     + "Days held: " + daysHeld + "\n"
                     + "Allowed days: " + allowedDays + "\n"
                     + "Overdue days: " + overdue + "\n"
                     + "Fine: ₹" + String.format("%.2f", fine);

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public String getLoanDetails(int loanId) throws SQLException {
        String sql = """
            SELECT l.loan_id, l.book_id, b.title, l.checkout_date,
                   l.return_date, l.allowed_days, l.renewals, l.active, l.fine,
                   DATE_ADD(l.checkout_date, INTERVAL l.allowed_days DAY) AS due_date
            FROM loans l
            JOIN books b ON b.book_id = l.book_id
            WHERE l.loan_id=?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, loanId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Loan ID not found.");
                }

                StringBuilder s = new StringBuilder();
                s.append("Loan ID: ").append(rs.getInt("loan_id")).append("\n");
                s.append("Book ID: ").append(rs.getInt("book_id")).append("\n");
                s.append("Title: ").append(rs.getString("title")).append("\n");
                s.append("Checked out: ").append(rs.getDate("checkout_date")).append("\n");
                s.append("Due date: ").append(rs.getDate("due_date")).append("\n");
                s.append("Allowed days: ").append(rs.getInt("allowed_days")).append("\n");
                s.append("Renewals: ").append(rs.getInt("renewals")).append("/2\n");
                s.append("Active: ").append(rs.getBoolean("active") ? "Yes" : "No").append("\n");

                if (rs.getDate("return_date") != null) {
                    s.append("Returned: ").append(rs.getDate("return_date")).append("\n");
                }

                s.append("Fine: ₹").append(String.format("%.2f", rs.getDouble("fine")));

                return s.toString();
            }
        }
    }

    public double getBorrowCost(int bookId, int days) throws SQLException {
        String sql = "SELECT rental_rate_per_day FROM books WHERE book_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Book ID not found.");
                }

                return Math.max(0, days) * rs.getDouble("rental_rate_per_day");
            }
        }
    }
}
