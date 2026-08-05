
import java.awt.event.*;
import javax.swing.*;

public class New extends JFrame implements ActionListener {

    JLabel name, roll, branch, gender;
    JTextField txtName, txtRoll, txtBranch;
    JRadioButton male, female;
    JCheckBox terms;
    JButton submit, reset;
    ButtonGroup genderGroup;

    New() {
        setSize(500, 500);
        setLayout(null);
        setTitle("Student Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        name = new JLabel("Full Name");
        name.setBounds(30, 30, 80, 30);
        add(name);

        txtName = new JTextField();
        txtName.setBounds(120, 30, 150, 30);
        add(txtName);

        roll = new JLabel("Roll No");
        roll.setBounds(30, 70, 80, 30);
        add(roll);

        txtRoll = new JTextField();
        txtRoll.setBounds(120, 70, 150, 30);
        add(txtRoll);

        branch = new JLabel("Branch");
        branch.setBounds(30, 110, 80, 30);
        add(branch);

        txtBranch = new JTextField();
        txtBranch.setBounds(120, 110, 150, 30);
        add(txtBranch);

        gender = new JLabel("Gender");
        gender.setBounds(30, 150, 80, 30);
        add(gender);

        male = new JRadioButton("Male");
        male.setBounds(120, 150, 80, 30);
        add(male);

        female = new JRadioButton("Female");
        female.setBounds(200, 150, 100, 30);
        add(female);

        genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);

        terms = new JCheckBox("Terms and Conditions");
        terms.setBounds(30, 190, 200, 30);
        add(terms);

        submit = new JButton("SUBMIT");
        submit.setBounds(30, 240, 100, 30);
        submit.addActionListener(this);
        add(submit);

        reset = new JButton("RESET");
        reset.setBounds(150, 240, 100, 30);
        reset.addActionListener(this);
        add(reset);

        setVisible(true);
    }

   @Override
public void actionPerformed(ActionEvent e) {

    if (e.getSource() == submit) {

        // Check if Terms & Conditions is accepted
        if (!terms.isSelected()) {
            JOptionPane.showMessageDialog(this,
                    "Please accept the Terms and Conditions before submitting!",
                    "Submission Failed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get entered values
        String Name = txtName.getText();
        String Roll = txtRoll.getText();
        String Branch = txtBranch.getText();

        String Gender = "";
        if (male.isSelected()) {
            Gender = "Male";
        } else if (female.isSelected()) {
            Gender = "Female";
        } else {
            Gender = "Not Selected";
        }

        // Display entered details
        JOptionPane.showMessageDialog(this,
                "Registration Successful!\n\n"
                + "Name   : " + Name + "\n"
                + "Roll No: " + Roll + "\n"
                + "Branch : " + Branch + "\n"
                + "Gender : " + Gender,
                "Student Details",
                JOptionPane.INFORMATION_MESSAGE);

        // Reset the form
        txtName.setText("");
        txtRoll.setText("");
        txtBranch.setText("");
        genderGroup.clearSelection();
        terms.setSelected(false);
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
        new New();
    }
}