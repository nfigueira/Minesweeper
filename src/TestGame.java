import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TestGame implements ActionListener {

    static JButton hello;
    static JButton hi;
    static JButton greetings;
    static JButton salutations;

    public static void main(String[] args) {
        // Create frame with specific title
        TestGame thing = new TestGame();
    }

    public TestGame() {
        Solver.main(new String[0]);


    }

    public void actionPerformed(ActionEvent e) {
        if ("1".equals(e.getActionCommand())) {
            hello.setEnabled(false);
            hi.setEnabled(false);
            salutations.setEnabled(true);
        } else {
            hi.setEnabled(true);
            greetings.setEnabled(true);
            salutations.setEnabled(false);
        }
    }
}
