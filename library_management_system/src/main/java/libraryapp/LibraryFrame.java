package main.java.libraryapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LibraryFrame extends JFrame {

    private final LibraryDAO dao = new LibraryDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Author", "Total", "Available", "Rate/Day"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public LibraryFrame() {
        setTitle("Library Management System");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        loadBooks();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addBook = new JButton("Add Book");
        JButton addCopies = new JButton("Add Copies");
        JButton borrow = new JButton("Borrow");
        JButton renew = new JButton("Renew");
        JButton returnBook = new JButton("Return");
        JButton details = new JButton("Loan Details");
        JButton cost = new JButton("Cost Estimate");
        JButton refresh = new JButton("Refresh");

        top.add(addBook);
        top.add(addCopies);
        top.add(borrow);
        top.add(renew);
        top.add(returnBook);
        top.add(details);
        top.add(cost);
        top.add(refresh);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType =
                new JComboBox<>(new String[]{"Title", "Author"});
        JButton search = new JButton("Search");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchType);
        searchPanel.add(search);

        JPanel north = new JPanel(new BorderLayout());
        north.add(top, BorderLayout.NORTH);
        north.add(searchPanel, BorderLayout.SOUTH);

        table.setAutoCreateRowSorter(true);
        table.setRowHeight(25);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addBook.addActionListener(e -> addBookDialog());
        addCopies.addActionListener(e -> addCopiesDialog());
        borrow.addActionListener(e -> borrowDialog());
        renew.addActionListener(e -> renewDialog());
        returnBook.addActionListener(e -> returnDialog());
        details.addActionListener(e -> loanDetailsDialog());
        cost.addActionListener(e -> costDialog());
        refresh.addActionListener(e -> loadBooks());

        search.addActionListener(e -> {
            try {
                searchBooks(searchField.getText(), searchType.getSelectedItem().toString());
            } catch (SQLException ex) {
                showError(ex);
            }
        });
    }

    private void loadBooks() {
        model.setRowCount(0);

        try (Connection con = DBConnection.getConnection();
             ResultSet rs = dao.getAllBooks(con)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies"),
                        rs.getDouble("rental_rate_per_day")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void searchBooks(String query, String type) throws SQLException {
        model.setRowCount(0);

        try (Connection con = DBConnection.getConnection();
             ResultSet rs = dao.searchBooks(con, query, type)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies"),
                        rs.getDouble("rental_rate_per_day")
                });
            }
        }
    }

    private void addBookDialog() {
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField copies = new JTextField("1");
        JTextField rate = new JTextField("0");

        Object[] fields = {
                "Title:", title,
                "Author:", author,
                "Copies:", copies,
                "Rental rate/day:", rate
        };

        if (JOptionPane.showConfirmDialog(
                this, fields, "Add Book",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            try {
                int c = Math.max(1, Integer.parseInt(copies.getText()));
                double r = Math.max(0, Double.parseDouble(rate.getText()));

                dao.addBook(title.getText().trim(), author.getText().trim(), c, r);
                loadBooks();

                JOptionPane.showMessageDialog(this, "Book added successfully.");
            } catch (Exception e) {
                showError(e);
            }
        }
    }

    private void addCopiesDialog() {
        JTextField id = new JTextField();
        JTextField copies = new JTextField();

        Object[] fields = {"Book ID:", id, "Copies to add:", copies};

        if (JOptionPane.showConfirmDialog(
                this, fields, "Add Copies",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            try {
                dao.addCopies(
                        Integer.parseInt(id.getText()),
                        Integer.parseInt(copies.getText())
                );

                loadBooks();
                JOptionPane.showMessageDialog(this, "Copies added.");
            } catch (Exception e) {
                showError(e);
            }
        }
    }

    private void borrowDialog() {
        JTextField bookId = new JTextField();
        JTextField days = new JTextField("7");

        Object[] fields = {"Book ID:", bookId, "Borrow days:", days};

        if (JOptionPane.showConfirmDialog(
                this, fields, "Borrow Book",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            try {
                int loanId = dao.borrowBook(
                        Integer.parseInt(bookId.getText()),
                        Integer.parseInt(days.getText())
                );

                loadBooks();

                JOptionPane.showMessageDialog(
                        this,
                        "Borrow successful!\nLoan ID: " + loanId,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                showError(e);
            }
        }
    }

    private void renewDialog() {
        JTextField loanId = new JTextField();
        JTextField extra = new JTextField();

        Object[] fields = {"Loan ID:", loanId, "Extra days:", extra};

        if (JOptionPane.showConfirmDialog(
                this, fields, "Renew Loan",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            try {
                dao.renewLoan(
                        Integer.parseInt(loanId.getText()),
                        Integer.parseInt(extra.getText())
                );

                JOptionPane.showMessageDialog(this, "Loan renewed successfully.");
            } catch (Exception e) {
                showError(e);
            }
        }
    }

    private void returnDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter Loan ID:");

        if (input == null) return;

        try {
            String result = dao.returnBook(Integer.parseInt(input));
            loadBooks();

            JOptionPane.showMessageDialog(
                    this, result, "Return Details",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    private void loanDetailsDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter Loan ID:");

        if (input == null) return;

        try {
            String result = dao.getLoanDetails(Integer.parseInt(input));

            JTextArea area = new JTextArea(result);
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

            JOptionPane.showMessageDialog(
                    this, new JScrollPane(area),
                    "Loan Details",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    private void costDialog() {
        JTextField bookId = new JTextField();
        JTextField days = new JTextField();

        Object[] fields = {"Book ID:", bookId, "Number of days:", days};

        if (JOptionPane.showConfirmDialog(
                this, fields, "Cost Estimate",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            try {
                double cost = dao.getBorrowCost(
                        Integer.parseInt(bookId.getText()),
                        Integer.parseInt(days.getText())
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Estimated borrowing cost: ₹" + String.format("%.2f", cost)
                );
            } catch (Exception e) {
                showError(e);
            }
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
