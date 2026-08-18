import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5001);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true
            );

            out.println("Hello Server");

            String message = in.readLine();
            System.out.println("Server says: " + message);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}