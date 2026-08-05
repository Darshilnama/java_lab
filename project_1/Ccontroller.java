
import java.awt.event.*;



public class Ccontroller implements ActionListener{
    CModel cm;
    Cview cv;

    public Ccontroller(CModel cm, Cview cv) {
        this.cm = cm;
        this.cv = cv;

        cv.add.addActionListener(this);
        cv.sub.addActionListener(this);
        cv.mul.addActionListener(this);
        cv.div.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        double a = Double.parseDouble(cv.txtA.getText());
        double b = Double.parseDouble(cv.txtB.getText());
        double res = 0;

        if (e.getSource() == cv.add) {
            res = cm.add(a, b);
        } else if (e.getSource() == cv.sub) {
            res = cm.sub(a, b);
        } else if (e.getSource() == cv.mul) {
            res = cm.mul(a, b);
        } else if (e.getSource() == cv.div) {
            res = cm.div(a, b);
        }

        cv.txtRes.setText(String.valueOf(res));
    }

}
