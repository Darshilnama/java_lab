

public class Main {
    public static void main(String[] args) {
        CModel cm = new CModel();
        Cview cv = new Cview();
        new Ccontroller(cm, cv);
        System.out.println("CALCULATOR");
    }
}
