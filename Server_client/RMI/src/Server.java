
import java.rmi.Naming;


public class Server{
    public static void main(String[] args) {
        try {
            Calculator_imp obj = new Calculator_imp();
            Naming.rebind("rmi://localhost/Calculator", obj);
            System.err.println("RMI Server Started....");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}