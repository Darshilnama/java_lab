import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/College";
        String username = "root";
        String password = "MyNewPassword123!"; // Replace with your MySQL password

        try {
            // Optional for JDBC 4.0+
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(url, username, password);

            System.out.println("Connected to the database!");
            System.out.println(con);

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}