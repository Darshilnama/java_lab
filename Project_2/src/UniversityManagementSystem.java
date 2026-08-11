import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UniversityManagementSystem extends JFrame implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(UniversityManagementSystem.class.getName());

    // Main Components
    JTabbedPane tabbedPane;

    // Panel declarations (MUST be class fields)
    JPanel dashboardPanel;
    JPanel studentPanel;
    JPanel staffPanel;
    JPanel deptPanel;
    JPanel coursePanel;
    JPanel enrollmentPanel;

    // Student Panel Components
    JTextField txtStudentName, txtStudentRoll, txtStudentBranch, txtStudentEmail, txtStudentPhone;
    JRadioButton rbMale, rbFemale;
    JCheckBox terms;
    JButton btnStudentSubmit, btnStudentReset, btnStudentUpdate, btnStudentDelete, btnStudentView;
    ButtonGroup genderGroup;
    JTable studentTable;
    DefaultTableModel studentTableModel;
    JScrollPane studentScrollPane;
    JTextField txtStudentSearch;
    JButton btnStudentSearch;

    // Staff Panel Components
    JTextField txtStaffId, txtStaffName, txtStaffDept, txtStaffDesignation, txtStaffEmail, txtStaffPhone, txtStaffSalary;
    JButton btnStaffAdd, btnStaffUpdate, btnStaffDelete, btnStaffView, btnStaffReset;
    JTable staffTable;
    DefaultTableModel staffTableModel;
    JScrollPane staffScrollPane;
    JTextField txtStaffSearch;
    JButton btnStaffSearch;

    // Department Panel Components
    JTextField txtDeptId, txtDeptName, txtDeptHead, txtDeptBudget;
    JButton btnDeptAdd, btnDeptUpdate, btnDeptDelete, btnDeptView, btnDeptReset;
    JTable deptTable;
    DefaultTableModel deptTableModel;
    JScrollPane deptScrollPane;

    // Course Panel Components
    JTextField txtCourseId, txtCourseName, txtCourseCredits, txtCourseInstructor;
    JComboBox<ComboItem> cmbCourseDept;
    JButton btnCourseAdd, btnCourseUpdate, btnCourseDelete, btnCourseView, btnCourseReset;
    JTable courseTable;
    DefaultTableModel courseTableModel;
    JScrollPane courseScrollPane;

    // Enrollment Panel Components
    JTextField txtEnrollSemester, txtEnrollGrade;
    JComboBox<ComboItem> cmbEnrollStudent, cmbEnrollCourse;
    JButton btnEnrollAdd, btnEnrollUpdate, btnEnrollDelete, btnEnrollView, btnEnrollReset;
    JTable enrollmentTable;
    DefaultTableModel enrollmentTableModel;
    JScrollPane enrollmentScrollPane;

    // Dashboard Panel
    JLabel lblStudentCount, lblStaffCount, lblDeptCount, lblCourseCount;

    // Database Credentials
    private static final String URL = "jdbc:mysql://localhost:3306/UniversityDB";
    private static final String USER = "root";
    private static final String PASSWORD = "MyNewPassword123!";

    public UniversityManagementSystem() {
        setTitle("University Management System");
        setSize(1200, 750);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createMenuBar();

        tabbedPane = new JTabbedPane();

        // Initialize all panels
        initDashboardPanel();
        initStudentPanel();
        initStaffPanel();
        initDepartmentPanel();
        initCoursePanel();
        initEnrollmentPanel();

        // Add panels to tabbed pane
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Students", studentPanel);
        tabbedPane.addTab("Staff", staffPanel);
        tabbedPane.addTab("Departments", deptPanel);
        tabbedPane.addTab("Courses", coursePanel);
        tabbedPane.addTab("Enrollments", enrollmentPanel);

        add(tabbedPane, BorderLayout.CENTER);

        refreshDashboard();
        loadComboBoxData();

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "University Management System v1.0\nDeveloped in Java Swing & MySQL",
                        "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void initDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(new Color(240, 240, 245));

        JLabel titleLabel = new JLabel("University Management Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        dashboardPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        cardsPanel.setBackground(new Color(240, 240, 245));

        JPanel card1 = createStatCard("Total Students", "—", new Color(52, 152, 219));
        JPanel card2 = createStatCard("Total Staff", "—", new Color(46, 204, 113));
        JPanel card3 = createStatCard("Departments", "—", new Color(155, 89, 182));
        JPanel card4 = createStatCard("Courses", "—", new Color(230, 126, 34));

        lblStudentCount = (JLabel) card1.getClientProperty("valueLabel");
        lblStaffCount = (JLabel) card2.getClientProperty("valueLabel");
        lblDeptCount = (JLabel) card3.getClientProperty("valueLabel");
        lblCourseCount = (JLabel) card4.getClientProperty("valueLabel");

        cardsPanel.add(card1);
        cardsPanel.add(card2);
        cardsPanel.add(card3);
        cardsPanel.add(card4);

        dashboardPanel.add(cardsPanel, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh Dashboard");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 14));
        btnRefresh.addActionListener(e -> refreshDashboard());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 240, 245));
        bottomPanel.add(btnRefresh);
        dashboardPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(220, 150));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 48));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.putClientProperty("valueLabel", valueLabel);

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private void initStudentPanel() {
        studentPanel = new JPanel(new BorderLayout(10, 10));
        studentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formContainer = new JPanel(new BorderLayout());
        JPanel studentFormPanel = new JPanel();
        studentFormPanel.setLayout(new BoxLayout(studentFormPanel, BoxLayout.Y_AXIS));
        studentFormPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Student Registration Form",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        txtStudentName = createFormField("Full Name:");
        txtStudentRoll = createFormField("Roll No:");
        txtStudentBranch = createFormField("Branch:");
        txtStudentEmail = createFormField("Email:");
        txtStudentPhone = createFormField("Phone:");

        studentFormPanel.add(txtStudentName.getParent());
        studentFormPanel.add(txtStudentRoll.getParent());
        studentFormPanel.add(txtStudentBranch.getParent());

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.add(new JLabel("Gender:"));
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        studentFormPanel.add(genderPanel);

        studentFormPanel.add(txtStudentEmail.getParent());
        studentFormPanel.add(txtStudentPhone.getParent());

        terms = new JCheckBox("I accept the Terms and Conditions");
        studentFormPanel.add(terms);
        studentFormPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        btnStudentSubmit = createButton("Register", new Color(46, 139, 87));
        btnStudentUpdate = createButton("Update", new Color(255, 140, 0));
        btnStudentDelete = createButton("Delete", new Color(220, 20, 60));
        btnStudentView = createButton("View All", new Color(70, 130, 180));
        btnStudentReset = createButton("Reset", new Color(128, 128, 128));

        buttonPanel.add(btnStudentSubmit);
        buttonPanel.add(btnStudentUpdate);
        buttonPanel.add(btnStudentDelete);
        buttonPanel.add(btnStudentView);
        buttonPanel.add(btnStudentReset);
        studentFormPanel.add(buttonPanel);

        btnStudentSubmit.addActionListener(this);
        btnStudentUpdate.addActionListener(this);
        btnStudentDelete.addActionListener(this);
        btnStudentView.addActionListener(this);
        btnStudentReset.addActionListener(this);

        JScrollPane formScroll = new JScrollPane(studentFormPanel);
        formScroll.setPreferredSize(new Dimension(320, 0));
        formScroll.setBorder(null);
        formContainer.add(formScroll, BorderLayout.CENTER);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        txtStudentSearch = new JTextField(20);
        btnStudentSearch = new JButton("Search");
        btnStudentSearch.addActionListener(this);
        searchPanel.add(txtStudentSearch);
        searchPanel.add(btnStudentSearch);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        String[] studentColumns = {"ID", "Name", "Roll No", "Branch", "Gender", "Email", "Phone"};
        studentTableModel = new DefaultTableModel(studentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setReorderingAllowed(false);
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = studentTable.getSelectedRow();
                if (row >= 0) {
                    txtStudentName.setText(String.valueOf(studentTableModel.getValueAt(row, 1)));
                    txtStudentRoll.setText(String.valueOf(studentTableModel.getValueAt(row, 2)));
                    txtStudentBranch.setText(String.valueOf(studentTableModel.getValueAt(row, 3)));
                    String gender = String.valueOf(studentTableModel.getValueAt(row, 4));
                    if ("Male".equals(gender)) rbMale.setSelected(true);
                    else if ("Female".equals(gender)) rbFemale.setSelected(true);
                    txtStudentEmail.setText(String.valueOf(studentTableModel.getValueAt(row, 5)));
                    txtStudentPhone.setText(String.valueOf(studentTableModel.getValueAt(row, 6)));
                }
            }
        });
        studentScrollPane = new JScrollPane(studentTable);
        tablePanel.add(studentScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formContainer, tablePanel);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.3);

        studentPanel.add(splitPane, BorderLayout.CENTER);
    }

    private void initStaffPanel() {
        staffPanel = new JPanel(new BorderLayout(10, 10));
        staffPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formContainer = new JPanel(new BorderLayout());
        JPanel staffFormPanel = new JPanel();
        staffFormPanel.setLayout(new BoxLayout(staffFormPanel, BoxLayout.Y_AXIS));
        staffFormPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Staff Management Form",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        txtStaffId = createFormField("Staff ID:");
        txtStaffName = createFormField("Name:");
        txtStaffDept = createFormField("Department:");
        txtStaffDesignation = createFormField("Designation:");
        txtStaffEmail = createFormField("Email:");
        txtStaffPhone = createFormField("Phone:");
        txtStaffSalary = createFormField("Salary:");

        staffFormPanel.add(txtStaffId.getParent());
        staffFormPanel.add(txtStaffName.getParent());
        staffFormPanel.add(txtStaffDept.getParent());
        staffFormPanel.add(txtStaffDesignation.getParent());
        staffFormPanel.add(txtStaffEmail.getParent());
        staffFormPanel.add(txtStaffPhone.getParent());
        staffFormPanel.add(txtStaffSalary.getParent());
        staffFormPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        btnStaffAdd = createButton("Add", new Color(46, 139, 87));
        btnStaffUpdate = createButton("Update", new Color(255, 140, 0));
        btnStaffDelete = createButton("Delete", new Color(220, 20, 60));
        btnStaffView = createButton("View All", new Color(70, 130, 180));
        btnStaffReset = createButton("Reset", new Color(128, 128, 128));

        buttonPanel.add(btnStaffAdd);
        buttonPanel.add(btnStaffUpdate);
        buttonPanel.add(btnStaffDelete);
        buttonPanel.add(btnStaffView);
        buttonPanel.add(btnStaffReset);
        staffFormPanel.add(buttonPanel);

        btnStaffAdd.addActionListener(this);
        btnStaffUpdate.addActionListener(this);
        btnStaffDelete.addActionListener(this);
        btnStaffView.addActionListener(this);
        btnStaffReset.addActionListener(this);

        JScrollPane formScroll = new JScrollPane(staffFormPanel);
        formScroll.setPreferredSize(new Dimension(320, 0));
        formScroll.setBorder(null);
        formContainer.add(formScroll, BorderLayout.CENTER);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        txtStaffSearch = new JTextField(20);
        btnStaffSearch = new JButton("Search");
        btnStaffSearch.addActionListener(this);
        searchPanel.add(txtStaffSearch);
        searchPanel.add(btnStaffSearch);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        String[] staffColumns = {"ID", "Name", "Department", "Designation", "Email", "Phone", "Salary"};
        staffTableModel = new DefaultTableModel(staffColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffTable = new JTable(staffTableModel);
        staffTable.getTableHeader().setReorderingAllowed(false);
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = staffTable.getSelectedRow();
                if (row >= 0) {
                    txtStaffId.setText(String.valueOf(staffTableModel.getValueAt(row, 0)));
                    txtStaffName.setText(String.valueOf(staffTableModel.getValueAt(row, 1)));
                    txtStaffDept.setText(String.valueOf(staffTableModel.getValueAt(row, 2)));
                    txtStaffDesignation.setText(String.valueOf(staffTableModel.getValueAt(row, 3)));
                    txtStaffEmail.setText(String.valueOf(staffTableModel.getValueAt(row, 4)));
                    txtStaffPhone.setText(String.valueOf(staffTableModel.getValueAt(row, 5)));
                    txtStaffSalary.setText(String.valueOf(staffTableModel.getValueAt(row, 6)));
                }
            }
        });
        staffScrollPane = new JScrollPane(staffTable);
        tablePanel.add(staffScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formContainer, tablePanel);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.3);

        staffPanel.add(splitPane, BorderLayout.CENTER);
    }

    private void initDepartmentPanel() {
        deptPanel = new JPanel(new BorderLayout(10, 10));
        deptPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formContainer = new JPanel(new BorderLayout());
        JPanel deptFormPanel = new JPanel();
        deptFormPanel.setLayout(new BoxLayout(deptFormPanel, BoxLayout.Y_AXIS));
        deptFormPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Department Management",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        txtDeptId = createFormField("Dept ID:");
        txtDeptName = createFormField("Dept Name:");
        txtDeptHead = createFormField("Head of Dept:");
        txtDeptBudget = createFormField("Budget:");

        deptFormPanel.add(txtDeptId.getParent());
        deptFormPanel.add(txtDeptName.getParent());
        deptFormPanel.add(txtDeptHead.getParent());
        deptFormPanel.add(txtDeptBudget.getParent());
        deptFormPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        btnDeptAdd = createButton("Add", new Color(46, 139, 87));
        btnDeptUpdate = createButton("Update", new Color(255, 140, 0));
        btnDeptDelete = createButton("Delete", new Color(220, 20, 60));
        btnDeptView = createButton("View All", new Color(70, 130, 180));
        btnDeptReset = createButton("Reset", new Color(128, 128, 128));

        buttonPanel.add(btnDeptAdd);
        buttonPanel.add(btnDeptUpdate);
        buttonPanel.add(btnDeptDelete);
        buttonPanel.add(btnDeptView);
        buttonPanel.add(btnDeptReset);
        deptFormPanel.add(buttonPanel);

        btnDeptAdd.addActionListener(this);
        btnDeptUpdate.addActionListener(this);
        btnDeptDelete.addActionListener(this);
        btnDeptView.addActionListener(this);
        btnDeptReset.addActionListener(this);

        JScrollPane formScroll = new JScrollPane(deptFormPanel);
        formScroll.setPreferredSize(new Dimension(320, 0));
        formScroll.setBorder(null);
        formContainer.add(formScroll, BorderLayout.CENTER);

        String[] deptColumns = {"Dept ID", "Department Name", "Head", "Budget"};
        deptTableModel = new DefaultTableModel(deptColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        deptTable = new JTable(deptTableModel);
        deptTable.getTableHeader().setReorderingAllowed(false);
        deptTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = deptTable.getSelectedRow();
                if (row >= 0) {
                    txtDeptId.setText(String.valueOf(deptTableModel.getValueAt(row, 0)));
                    txtDeptName.setText(String.valueOf(deptTableModel.getValueAt(row, 1)));
                    txtDeptHead.setText(String.valueOf(deptTableModel.getValueAt(row, 2)));
                    txtDeptBudget.setText(String.valueOf(deptTableModel.getValueAt(row, 3)));
                }
            }
        });
        deptScrollPane = new JScrollPane(deptTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formContainer, deptScrollPane);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.3);

        deptPanel.add(splitPane, BorderLayout.CENTER);
    }

    private void initCoursePanel() {
        coursePanel = new JPanel(new BorderLayout(10, 10));
        coursePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formContainer = new JPanel(new BorderLayout());
        JPanel courseFormPanel = new JPanel();
        courseFormPanel.setLayout(new BoxLayout(courseFormPanel, BoxLayout.Y_AXIS));
        courseFormPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Course Management",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        txtCourseId = createFormField("Course ID:");
        txtCourseName = createFormField("Course Name:");
        txtCourseCredits = createFormField("Credits:");
        txtCourseInstructor = createFormField("Instructor:");
        cmbCourseDept = new JComboBox<>();

        courseFormPanel.add(txtCourseId.getParent());
        courseFormPanel.add(txtCourseName.getParent());

        JPanel deptComboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deptComboPanel.add(new JLabel("Department:"));
        deptComboPanel.add(cmbCourseDept);
        courseFormPanel.add(deptComboPanel);

        courseFormPanel.add(txtCourseCredits.getParent());
        courseFormPanel.add(txtCourseInstructor.getParent());
        courseFormPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        btnCourseAdd = createButton("Add", new Color(46, 139, 87));
        btnCourseUpdate = createButton("Update", new Color(255, 140, 0));
        btnCourseDelete = createButton("Delete", new Color(220, 20, 60));
        btnCourseView = createButton("View All", new Color(70, 130, 180));
        btnCourseReset = createButton("Reset", new Color(128, 128, 128));

        buttonPanel.add(btnCourseAdd);
        buttonPanel.add(btnCourseUpdate);
        buttonPanel.add(btnCourseDelete);
        buttonPanel.add(btnCourseView);
        buttonPanel.add(btnCourseReset);
        courseFormPanel.add(buttonPanel);

        btnCourseAdd.addActionListener(this);
        btnCourseUpdate.addActionListener(this);
        btnCourseDelete.addActionListener(this);
        btnCourseView.addActionListener(this);
        btnCourseReset.addActionListener(this);

        JScrollPane formScroll = new JScrollPane(courseFormPanel);
        formScroll.setPreferredSize(new Dimension(320, 0));
        formScroll.setBorder(null);
        formContainer.add(formScroll, BorderLayout.CENTER);

        String[] courseColumns = {"Course ID", "Course Name", "Department", "Credits", "Instructor"};
        courseTableModel = new DefaultTableModel(courseColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(courseTableModel);
        courseTable.getTableHeader().setReorderingAllowed(false);
        courseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = courseTable.getSelectedRow();
                if (row >= 0) {
                    txtCourseId.setText(String.valueOf(courseTableModel.getValueAt(row, 0)));
                    txtCourseName.setText(String.valueOf(courseTableModel.getValueAt(row, 1)));
                    selectComboItem(cmbCourseDept, String.valueOf(courseTableModel.getValueAt(row, 2)));
                    txtCourseCredits.setText(String.valueOf(courseTableModel.getValueAt(row, 3)));
                    txtCourseInstructor.setText(String.valueOf(courseTableModel.getValueAt(row, 4)));
                }
            }
        });
        courseScrollPane = new JScrollPane(courseTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formContainer, courseScrollPane);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.3);

        coursePanel.add(splitPane, BorderLayout.CENTER);
    }

    private void initEnrollmentPanel() {
        enrollmentPanel = new JPanel(new BorderLayout(10, 10));
        enrollmentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formContainer = new JPanel(new BorderLayout());
        JPanel enrollFormPanel = new JPanel();
        enrollFormPanel.setLayout(new BoxLayout(enrollFormPanel, BoxLayout.Y_AXIS));
        enrollFormPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Enrollment Management",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        txtEnrollSemester = createFormField("Semester:");
        txtEnrollGrade = createFormField("Grade:");
        cmbEnrollStudent = new JComboBox<>();
        cmbEnrollCourse = new JComboBox<>();

        JPanel studentComboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        studentComboPanel.add(new JLabel("Student:"));
        studentComboPanel.add(cmbEnrollStudent);
        enrollFormPanel.add(studentComboPanel);

        JPanel courseComboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        courseComboPanel.add(new JLabel("Course:"));
        courseComboPanel.add(cmbEnrollCourse);
        enrollFormPanel.add(courseComboPanel);

        enrollFormPanel.add(txtEnrollSemester.getParent());
        enrollFormPanel.add(txtEnrollGrade.getParent());
        enrollFormPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        btnEnrollAdd = createButton("Enroll", new Color(46, 139, 87));
        btnEnrollUpdate = createButton("Update", new Color(255, 140, 0));
        btnEnrollDelete = createButton("Delete", new Color(220, 20, 60));
        btnEnrollView = createButton("View All", new Color(70, 130, 180));
        btnEnrollReset = createButton("Reset", new Color(128, 128, 128));

        buttonPanel.add(btnEnrollAdd);
        buttonPanel.add(btnEnrollUpdate);
        buttonPanel.add(btnEnrollDelete);
        buttonPanel.add(btnEnrollView);
        buttonPanel.add(btnEnrollReset);
        enrollFormPanel.add(buttonPanel);

        btnEnrollAdd.addActionListener(this);
        btnEnrollUpdate.addActionListener(this);
        btnEnrollDelete.addActionListener(this);
        btnEnrollView.addActionListener(this);
        btnEnrollReset.addActionListener(this);

        JScrollPane formScroll = new JScrollPane(enrollFormPanel);
        formScroll.setPreferredSize(new Dimension(320, 0));
        formScroll.setBorder(null);
        formContainer.add(formScroll, BorderLayout.CENTER);

        String[] enrollColumns = {"Enroll ID", "Student", "Course", "Semester", "Grade"};
        enrollmentTableModel = new DefaultTableModel(enrollColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrollmentTable = new JTable(enrollmentTableModel);
        enrollmentTable.getTableHeader().setReorderingAllowed(false);
        enrollmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = enrollmentTable.getSelectedRow();
                if (row >= 0) {
                    selectComboItemByName(cmbEnrollStudent, String.valueOf(enrollmentTableModel.getValueAt(row, 1)));
                    selectComboItemByName(cmbEnrollCourse, String.valueOf(enrollmentTableModel.getValueAt(row, 2)));
                    txtEnrollSemester.setText(String.valueOf(enrollmentTableModel.getValueAt(row, 3)));
                    txtEnrollGrade.setText(String.valueOf(enrollmentTableModel.getValueAt(row, 4)));
                }
            }
        });
        enrollmentScrollPane = new JScrollPane(enrollmentTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formContainer, enrollmentScrollPane);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.3);

        enrollmentPanel.add(splitPane, BorderLayout.CENTER);
    }

    private void selectComboItem(JComboBox<ComboItem> comboBox, String name) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem item = comboBox.getItemAt(i);
            if (item != null && item.name.equals(name)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectComboItemByName(JComboBox<ComboItem> comboBox, String name) {
        selectComboItem(comboBox, name);
    }

    private JTextField createFormField(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 25));
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(180, 25));
        panel.add(label);
        panel.add(textField);
        return textField;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);

        // Make text visible on macOS
        button.setForeground(Color.BLACK);
        button.setBackground(color);

        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);

        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(85, 34));
        button.setMinimumSize(new Dimension(85, 34));

        return button;
    }
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void refreshDashboard() {
        try (Connection conn = getConnection()) {
            lblStudentCount.setText(String.valueOf(getCount(conn, "students")));
            lblStaffCount.setText(String.valueOf(getCount(conn, "staff")));
            lblDeptCount.setText(String.valueOf(getCount(conn, "departments")));
            lblCourseCount.setText(String.valueOf(getCount(conn, "courses")));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error refreshing dashboard", e);
        }
    }

    private int getCount(Connection conn, String table) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + table;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private void loadComboBoxData() {
        try (Connection conn = getConnection()) {
            cmbCourseDept.removeAllItems();
            try (PreparedStatement ps = conn.prepareStatement("SELECT dept_id, dept_name FROM departments ORDER BY dept_name");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cmbCourseDept.addItem(new ComboItem(rs.getString("dept_id"), rs.getString("dept_name")));
                }
            }

            cmbEnrollStudent.removeAllItems();
            try (PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM students ORDER BY name");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cmbEnrollStudent.addItem(new ComboItem(String.valueOf(rs.getInt("id")), rs.getString("name")));
                }
            }

            cmbEnrollCourse.removeAllItems();
            try (PreparedStatement ps = conn.prepareStatement("SELECT course_id, course_name FROM courses ORDER BY course_name");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cmbEnrollCourse.addItem(new ComboItem(rs.getString("course_id"), rs.getString("course_name")));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error loading combo box data", e);
        }
    }

    private static class ComboItem {
        private final String id;
        private final String name;

        ComboItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        String getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnStudentSubmit) addStudent();
        else if (source == btnStudentUpdate) updateStudent();
        else if (source == btnStudentDelete) deleteStudent();
        else if (source == btnStudentView || source == btnStudentSearch) viewStudents();
        else if (source == btnStudentReset) resetStudentForm();
        else if (source == btnStaffAdd) addStaff();
        else if (source == btnStaffUpdate) updateStaff();
        else if (source == btnStaffDelete) deleteStaff();
        else if (source == btnStaffView || source == btnStaffSearch) viewStaff();
        else if (source == btnStaffReset) resetStaffForm();
        else if (source == btnDeptAdd) addDepartment();
        else if (source == btnDeptUpdate) updateDepartment();
        else if (source == btnDeptDelete) deleteDepartment();
        else if (source == btnDeptView) viewDepartments();
        else if (source == btnDeptReset) resetDeptForm();
        else if (source == btnCourseAdd) addCourse();
        else if (source == btnCourseUpdate) updateCourse();
        else if (source == btnCourseDelete) deleteCourse();
        else if (source == btnCourseView) viewCourses();
        else if (source == btnCourseReset) resetCourseForm();
        else if (source == btnEnrollAdd) addEnrollment();
        else if (source == btnEnrollUpdate) updateEnrollment();
        else if (source == btnEnrollDelete) deleteEnrollment();
        else if (source == btnEnrollView) viewEnrollments();
        else if (source == btnEnrollReset) resetEnrollmentForm();

        loadComboBoxData();
        refreshDashboard();
    }

    // CRUD Operations
    private void addStudent() {
        String name = txtStudentName.getText().trim();
        String roll = txtStudentRoll.getText().trim();
        String branch = txtStudentBranch.getText().trim();
        String email = txtStudentEmail.getText().trim();
        String phone = txtStudentPhone.getText().trim();

        if (name.isEmpty() || roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Roll No are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!terms.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please accept the Terms and Conditions!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String gender = rbMale.isSelected() ? "Male" : rbFemale.isSelected() ? "Female" : "";

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO students (name, roll_no, branch, gender, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, roll);
                ps.setString(3, branch);
                ps.setString(4, gender);
                ps.setString(5, email);
                ps.setString(6, phone);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student registered successfully!");
                viewStudents();
                resetStudentForm();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error adding student", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to update!");
            return;
        }

        int id = Integer.parseInt(studentTableModel.getValueAt(row, 0).toString());
        String name = txtStudentName.getText().trim();
        String roll = txtStudentRoll.getText().trim();
        String branch = txtStudentBranch.getText().trim();
        String email = txtStudentEmail.getText().trim();
        String phone = txtStudentPhone.getText().trim();
        String gender = rbMale.isSelected() ? "Male" : rbFemale.isSelected() ? "Female" : "";

        try (Connection conn = getConnection()) {
            String sql = "UPDATE students SET name=?, roll_no=?, branch=?, gender=?, email=?, phone=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, roll);
                ps.setString(3, branch);
                ps.setString(4, gender);
                ps.setString(5, email);
                ps.setString(6, phone);
                ps.setInt(7, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
                viewStudents();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating student", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(studentTableModel.getValueAt(row, 0).toString());
            try (Connection conn = getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Student deleted!");
                    viewStudents();
                    resetStudentForm();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error deleting student", ex);
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void viewStudents() {
        studentTableModel.setRowCount(0);
        String search = txtStudentSearch.getText().trim();

        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM students";
            if (!search.isEmpty()) {
                sql += " WHERE name LIKE ? OR roll_no LIKE ? OR branch LIKE ?";
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (!search.isEmpty()) {
                    String searchPattern = "%" + search + "%";
                    ps.setString(1, searchPattern);
                    ps.setString(2, searchPattern);
                    ps.setString(3, searchPattern);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = {
                                rs.getInt("id"), rs.getString("name"), rs.getString("roll_no"),
                                rs.getString("branch"), rs.getString("gender"),
                                rs.getString("email"), rs.getString("phone")
                        };
                        studentTableModel.addRow(row);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error viewing students", ex);
        }
    }

    private void resetStudentForm() {
        txtStudentName.setText("");
        txtStudentRoll.setText("");
        txtStudentBranch.setText("");
        txtStudentEmail.setText("");
        txtStudentPhone.setText("");
        genderGroup.clearSelection();
        terms.setSelected(false);
        studentTable.clearSelection();
    }

    private void addStaff() {
        String id = txtStaffId.getText().trim();
        String name = txtStaffName.getText().trim();
        String dept = txtStaffDept.getText().trim();
        String designation = txtStaffDesignation.getText().trim();
        String email = txtStaffEmail.getText().trim();
        String phone = txtStaffPhone.getText().trim();
        String salary = txtStaffSalary.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Staff ID and Name are required!");
            return;
        }

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO staff (staff_id, name, department, designation, email, phone, salary) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, dept);
                ps.setString(4, designation);
                ps.setString(5, email);
                ps.setString(6, phone);
                ps.setDouble(7, salary.isEmpty() ? 0 : Double.parseDouble(salary));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Staff added successfully!");
                viewStaff();
                resetStaffForm();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error adding staff", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateStaff() {
        int row = staffTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to update!");
            return;
        }

        String id = txtStaffId.getText().trim();
        String name = txtStaffName.getText().trim();
        String dept = txtStaffDept.getText().trim();
        String designation = txtStaffDesignation.getText().trim();
        String email = txtStaffEmail.getText().trim();
        String phone = txtStaffPhone.getText().trim();
        String salary = txtStaffSalary.getText().trim();

        try (Connection conn = getConnection()) {
            String sql = "UPDATE staff SET name=?, department=?, designation=?, email=?, phone=?, salary=? WHERE staff_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, dept);
                ps.setString(3, designation);
                ps.setString(4, email);
                ps.setString(5, phone);
                ps.setDouble(6, salary.isEmpty() ? 0 : Double.parseDouble(salary));
                ps.setString(7, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Staff updated!");
                viewStaff();
                resetStaffForm();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating staff", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteStaff() {
        int row = staffTable.getSelectedRow();
        if (row < 0) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
        if (confirm == JOptionPane.YES_OPTION) {
            String id = staffTableModel.getValueAt(row, 0).toString();
            try (Connection conn = getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM staff WHERE staff_id=?")) {
                    ps.setString(1, id);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Staff deleted!");
                    viewStaff();
                    resetStaffForm();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error deleting staff", ex);
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void viewStaff() {
        staffTableModel.setRowCount(0);
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM staff";
            String searchText = txtStaffSearch.getText().trim();
            if (!searchText.isEmpty()) {
                sql += " WHERE name LIKE ? OR department LIKE ?";
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (!searchText.isEmpty()) {
                    String pattern = "%" + searchText + "%";
                    ps.setString(1, pattern);
                    ps.setString(2, pattern);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = {
                                rs.getString("staff_id"), rs.getString("name"), rs.getString("department"),
                                rs.getString("designation"), rs.getString("email"),
                                rs.getString("phone"), rs.getDouble("salary")
                        };
                        staffTableModel.addRow(row);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error viewing staff", ex);
        }
    }

    private void resetStaffForm() {
        txtStaffId.setText("");
        txtStaffName.setText("");
        txtStaffDept.setText("");
        txtStaffDesignation.setText("");
        txtStaffEmail.setText("");
        txtStaffPhone.setText("");
        txtStaffSalary.setText("");
        staffTable.clearSelection();
    }

    private void addDepartment() {
        String id = txtDeptId.getText().trim();
        String name = txtDeptName.getText().trim();
        String head = txtDeptHead.getText().trim();
        String budget = txtDeptBudget.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Department ID and Name are required!");
            return;
        }

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO departments (dept_id, dept_name, head, budget) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, head);
                ps.setDouble(4, budget.isEmpty() ? 0 : Double.parseDouble(budget));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Department added!");
                viewDepartments();
                resetDeptForm();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error adding department", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateDepartment() {
        int row = deptTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a department!");
            return;
        }

        String id = txtDeptId.getText().trim();
        String name = txtDeptName.getText().trim();
        String head = txtDeptHead.getText().trim();
        String budget = txtDeptBudget.getText().trim();

        try (Connection conn = getConnection()) {
            String sql = "UPDATE departments SET dept_name=?, head=?, budget=? WHERE dept_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, head);
                ps.setDouble(3, budget.isEmpty() ? 0 : Double.parseDouble(budget));
                ps.setString(4, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Department updated!");
                viewDepartments();
                resetDeptForm();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating department", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteDepartment() {
        int row = deptTable.getSelectedRow();
        if (row < 0) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
        if (confirm == JOptionPane.YES_OPTION) {
            String id = deptTableModel.getValueAt(row, 0).toString();
            try (Connection conn = getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM departments WHERE dept_id=?")) {
                    ps.setString(1, id);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Department deleted!");
                    viewDepartments();
                    resetDeptForm();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error deleting department", ex);
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void viewDepartments() {
        deptTableModel.setRowCount(0);
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM departments")) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getString("dept_id"), rs.getString("dept_name"),
                            rs.getString("head"), rs.getDouble("budget")
                    };
                    deptTableModel.addRow(row);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error viewing departments", ex);
        }
    }

    private void resetDeptForm() {
        txtDeptId.setText("");
        txtDeptName.setText("");
        txtDeptHead.setText("");
        txtDeptBudget.setText("");
        deptTable.clearSelection();
    }

    private void addCourse() {
        String id = txtCourseId.getText().trim();
        String name = txtCourseName.getText().trim();
        String dept = cmbCourseDept.getSelectedItem() != null ? cmbCourseDept.getSelectedItem().toString() : "";
        String credits = txtCourseCredits.getText().trim();
        String instructor = txtCourseInstructor.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course ID and Name are required!");
            return;
        }

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO courses (course_id, course_name, department, credits, instructor) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, dept);
                ps.setInt(4, credits.isEmpty() ? 0 : Integer.parseInt(credits));
                ps.setString(5, instructor);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Course added!");
                viewCourses();
                resetCourseForm();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error adding course", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateCourse() {
        int row = courseTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course!");
            return;
        }

        String id = txtCourseId.getText().trim();
        String name = txtCourseName.getText().trim();
        String dept = cmbCourseDept.getSelectedItem() != null ? cmbCourseDept.getSelectedItem().toString() : "";
        String credits = txtCourseCredits.getText().trim();
        String instructor = txtCourseInstructor.getText().trim();

        try (Connection conn = getConnection()) {
            String sql = "UPDATE courses SET course_name=?, department=?, credits=?, instructor=? WHERE course_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, dept);
                ps.setInt(3, credits.isEmpty() ? 0 : Integer.parseInt(credits));
                ps.setString(4, instructor);
                ps.setString(5, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Course updated!");
                viewCourses();
                resetCourseForm();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating course", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteCourse() {
        int row = courseTable.getSelectedRow();
        if (row < 0) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
        if (confirm == JOptionPane.YES_OPTION) {
            String id = courseTableModel.getValueAt(row, 0).toString();
            try (Connection conn = getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM courses WHERE course_id=?")) {
                    ps.setString(1, id);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Course deleted!");
                    viewCourses();
                    resetCourseForm();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error deleting course", ex);
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void viewCourses() {
        courseTableModel.setRowCount(0);
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM courses")) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getString("course_id"), rs.getString("course_name"),
                            rs.getString("department"), rs.getInt("credits"),
                            rs.getString("instructor")
                    };
                    courseTableModel.addRow(row);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error viewing courses", ex);
        }
    }

    private void resetCourseForm() {
        txtCourseId.setText("");
        txtCourseName.setText("");
        txtCourseCredits.setText("");
        txtCourseInstructor.setText("");
        cmbCourseDept.setSelectedIndex(-1);
        courseTable.clearSelection();
    }

    private void addEnrollment() {
        ComboItem studentItem = (ComboItem) cmbEnrollStudent.getSelectedItem();
        ComboItem courseItem = (ComboItem) cmbEnrollCourse.getSelectedItem();
        String student = studentItem != null ? studentItem.getId() : "";
        String course = courseItem != null ? courseItem.getId() : "";
        String semester = txtEnrollSemester.getText().trim();
        String grade = txtEnrollGrade.getText().trim();

        if (student.isEmpty() || course.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select student and course!");
            return;
        }

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO enrollments (student_id, course_id, semester, grade) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, Integer.parseInt(student));
                ps.setString(2, course);
                ps.setString(3, semester);
                ps.setString(4, grade);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Enrollment successful!");
                viewEnrollments();
                resetEnrollmentForm();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error adding enrollment", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateEnrollment() {
        int row = enrollmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment!");
            return;
        }

        int id = Integer.parseInt(enrollmentTableModel.getValueAt(row, 0).toString());
        ComboItem studentItem = (ComboItem) cmbEnrollStudent.getSelectedItem();
        ComboItem courseItem = (ComboItem) cmbEnrollCourse.getSelectedItem();
        String student = studentItem != null ? studentItem.getId() : "";
        String course = courseItem != null ? courseItem.getId() : "";
        String semester = txtEnrollSemester.getText().trim();
        String grade = txtEnrollGrade.getText().trim();

        try (Connection conn = getConnection()) {
            String sql = "UPDATE enrollments SET student_id=?, course_id=?, semester=?, grade=? WHERE enroll_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, Integer.parseInt(student));
                ps.setString(2, course);
                ps.setString(3, semester);
                ps.setString(4, grade);
                ps.setInt(5, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Enrollment updated!");
                viewEnrollments();
                resetEnrollmentForm();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating enrollment", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteEnrollment() {
        int row = enrollmentTable.getSelectedRow();
        if (row < 0) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(enrollmentTableModel.getValueAt(row, 0).toString());
            try (Connection conn = getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM enrollments WHERE enroll_id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Enrollment deleted!");
                    viewEnrollments();
                    resetEnrollmentForm();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error deleting enrollment", ex);
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void viewEnrollments() {
        enrollmentTableModel.setRowCount(0);
        String sql = "SELECT e.enroll_id, s.name AS student_name, c.course_name, " +
                "e.semester, e.grade " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.id " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "ORDER BY e.enroll_id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("enroll_id"), rs.getString("student_name"),
                        rs.getString("course_name"), rs.getString("semester"),
                        rs.getString("grade")
                };
                enrollmentTableModel.addRow(row);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error viewing enrollments", ex);
        }
    }

    private void resetEnrollmentForm() {
        txtEnrollSemester.setText("");
        txtEnrollGrade.setText("");
        cmbEnrollStudent.setSelectedIndex(-1);
        cmbEnrollCourse.setSelectedIndex(-1);
        enrollmentTable.clearSelection();
    }

    static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            LOGGER.log(Level.WARNING, "Could not set system look and feel");
        }

        SwingUtilities.invokeLater(UniversityManagementSystem::new);
    }
}