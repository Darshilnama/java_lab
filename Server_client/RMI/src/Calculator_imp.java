import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Calculator_imp extends UnicastRemoteObject implements Calculator{
    Calculator_imp() throws RemoteException{
        super();
    }

    public int add (int a, int b){
        return a+b;
    }

    public int sub (int a, int b){
        return a-b;
    }
}