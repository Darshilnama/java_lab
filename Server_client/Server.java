import java.io.*;
import java.net.*;
public class Server {
    public static void main(String[] args){
        try {
            ServerSocket serverSocket = new ServerSocket(5001);
            System.out.println("Server started");
            System.out.println("waiting for client...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String message = in.readLine();
            System.out.println("Client says: " + message);
            out.println("Hello Client");
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
