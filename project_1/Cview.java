
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;



public class Cview extends JFrame{

    JLabel a, b, res;
    JTextField txtA, txtB, txtRes;
    JButton add, sub, mul, div;

    public Cview() {
        setTitle("Calculator");
        setSize(500, 500);
        setLayout(null);

        a = new JLabel("ENTER A:");
        a.setBounds(50, 50, 100, 30);
        add(a);

        b = new JLabel("ENTER B:");
        b.setBounds(50, 100, 100, 30);
        add(b);

        res = new JLabel("Result:");
        res.setBounds(50, 150, 100, 30);
        add(res);

        txtA = new JTextField();
        txtA.setBounds(150, 50, 200, 30);
        add(txtA);

        txtB = new JTextField();
        txtB.setBounds(150, 100, 200, 30);
        add(txtB);

        txtRes = new JTextField();
        txtRes.setBounds(150, 150, 200, 30);
        txtRes.setEditable(false);
        add(txtRes);

        add = new JButton("+");
        add.setBounds(50, 200, 80, 30);
        add(add);

        sub = new JButton("-");
        sub.setBounds(150, 200, 100, 30);
        add(sub);

        mul = new JButton("*");
        mul.setBounds(270, 200, 100, 30);
        add(mul);

        div = new JButton("/");
        div.setBounds(390, 200, 100, 30);
        add(div);

        setVisible(true);
    }

    
}

