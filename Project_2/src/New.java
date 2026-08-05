import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class New extends JFrame implements ActionListener {

    JLabel name, roll, branch, gender;
    JTextField txtName, txtRoll, txtBranch;
    JRadioButton male, female;
    JCheckBox terms;
    JButton submit, reset;
    ButtonGroup genderGroup;

    // Database Credentials
    private static final String URL = "jdbc:mysql://localhost:3306/College";
    private static final String USER = "root";
    private static final String PASSWORD = "MyNewPassword123!"; // Change if needed

    public New() {

        setTitle("Student Registration Form");
        setSize(500, 420);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        name = new JLabel("Full Name");
        name.setBounds(40, 30, 100, 30);
        add(name);

        txtName = new JTextField();
        txtName.setBounds(150, 30, 180, 30);
        add(txtName);

        roll = new JLabel("Roll No");
        roll.setBounds(40, 70, 100, 30);
        add(roll);

        txtRoll = new JTextField();
        txtRoll.setBounds(150, 70, 180, 30);
        add(txtRoll);

        branch = new JLabel("Branch");
        branch.setBounds(40, 110, 100, 30);
        add(branch);

        txtBranch = new JTextField();
        txtBranch.setBounds(150, 110, 180, 30);
        add(txtBranch);

        gender = new JLabel("Gender");
        gender.setBounds(40, 150, 100, 30);
        add(gender);

        male = new JRadioButton("Male");
        male.setBounds(150, 150, 80, 30);

        female = new JRadioButton("Female");
        female.setBounds(240, 150, 100, 30);

        genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);

        add(male);
        add(female);

        terms = new JCheckBox("Terms and Conditions");
        terms.setBounds(40, 190, 220, 30);
        add(terms);

        submit = new JButton("SUBMIT");
        submit.setBounds(50, 250, 120, 35);
        submit.addActionListener(this);
        add(submit);

        reset = new JButton("RESET");
        reset.setBounds(210, 250, 120, 35);
        reset.addActionListener(this);
        add(reset);

        setVisible(true);
    }

    // Database Connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == submit) {

            String Name = txtName.getText().trim();
            String Roll = txtRoll.getText().trim();
            String Branch = txtBranch.getText().trim();

            if (Name.isEmpty() || Roll.isEmpty() || Branch.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String Gender;

            if (male.isSelected()) {
                Gender = "Male";
            } else if (female.isSelected()) {
                Gender = "Female";
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select gender.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!terms.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "Please accept Terms and Conditions.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {

                Connection con = getConnection();

                String sql = "INSERT INTO Student(name, roll_no, branch, gender) VALUES (?, ?, ?, ?)";

                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, Name);
                ps.setString(2, Roll);
                ps.setString(3, Branch);
                ps.setString(4, Gender);

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    JOptionPane.showMessageDialog(this,
                            "Registration Successful!\n\n"
                                    + "Name : " + Name + "\n"
                                    + "Roll No : " + Roll + "\n"
                                    + "Branch : " + Branch + "\n"
                                    + "Gender : " + Gender,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    txtName.setText("");
                    txtRoll.setText("");
                    txtBranch.setText("");
                    genderGroup.clearSelection();
                    terms.setSelected(false);

                }

                ps.close();
                con.close();

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);

            }

        }

        if (e.getSource() == reset) {

            txtName.setText("");
            txtRoll.setText("");
            txtBranch.setText("");
            genderGroup.clearSelection();
            terms.setSelected(false);

        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new New());

    }
}