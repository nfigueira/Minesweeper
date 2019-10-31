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
        GridLayout grid = new GridLayout(2,2);
        JFrame minesweeper = new JFrame("Minesweeper");
        minesweeper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        minesweeper.setLayout(grid);
        hello = new JButton("hello");
        hi = new JButton("hi");
        greetings = new JButton("greetings");
        salutations = new JButton("salutations");
        hello.setActionCommand("1");
        hi.setActionCommand("2");
        greetings.setActionCommand("3");
        salutations.setActionCommand("4");
        hello.addActionListener(this);
        minesweeper.add(hello);
        minesweeper.add(hi);
        minesweeper.add(greetings);
        minesweeper.add(salutations);





        minesweeper.pack();
        minesweeper.setVisible(true);
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
