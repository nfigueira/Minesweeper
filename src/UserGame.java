import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserGame extends Game implements ActionListener {

    // the buttons in a 2d grid
    private JButton[][] cells;
    // the window they're located in
    private JFrame minesweeper;

    public UserGame(int width, int height, int mines) {
        super(width, height, mines);

        // create the minesweeper window
        minesweeper = new JFrame("Minesweeper");
        // exit the program if the user closes the window
        minesweeper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // make the layout of the window into a grid for the buttons
        minesweeper.setLayout(new GridLayout(height, width));

        // initialize the array of buttons
        cells = new JButton[height][width];

        // initialize all of them to covered cells
        initializeCells();

        // set the dimensions of the window and show it
        // M is bigger than X so setting one to M before packing makes the squares big enough for all values
        cells[0][0].setText("M");
        minesweeper.pack();
        cells[0][0].setText("X");
        minesweeper.setVisible(true);
    }

    private void initializeCells() {
        for (int r = 0; r < getHeight(); r++) {
            for (int c = 0; c < getWidth(); c++) {
                cells[r][c] = new JButton("X");
                cells[r][c].addActionListener(this);
                // we can recover r by dividing by width and c by modding by width
                cells[r][c].setActionCommand(Integer.toString(r * getWidth() + c));
                minesweeper.add(cells[r][c]);
            }
        }
    }

    // determine which square is pressed and click it
    @Override
    public void actionPerformed(ActionEvent e) {
        int cell = Integer.parseInt(e.getActionCommand());
        int row = cell / getWidth();
        int col = cell % getWidth();
        // checks if the game continues
        if (click(row, col)) {
            endGame();
        }
    }

    private void endGame() {
        String message, title;
        if (checkWin()) {
            message = "You win! Play again?";
            title = "Congratulations!";
        } else {
            message = "BOOM! You lose! Try again?";
            title = "Game Over";
        }

        int n = JOptionPane.showConfirmDialog(minesweeper, message, title, JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {
            reset();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void expand(int row, int col) {
        super.expand(row, col);
        // the square after being clicked will always be uncovered
        int squareVal = getSquare(row, col);
        if (squareVal == 0) {
            cells[row][col].setText(".");
        } else if (squareVal < 10) {
            cells[row][col].setText(Integer.toString(squareVal));
        } else {
            cells[row][col].setText("M");
        }

        // you can't click that cell anymore
        cells[row][col].setEnabled(false);
    }

    // make the cell an F if flagged (only for Solver)
    @Override
    public void flag(int row, int col) {
        super.flag(row, col);
        cells[row][col].setText("F");
    }

    @Override
    public void reset() {
        super.reset();
        // reset the layout and reinitialize
        minesweeper.setContentPane(new JPanel(new GridLayout(getHeight(), getWidth())));
        initializeCells();
        cells[0][0].setText("M");
        minesweeper.pack();
        cells[0][0].setText("X");
        minesweeper.setVisible(true);
    }
}
