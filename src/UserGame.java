import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// a minesweeper game that creates a window with buttons for a user to click
// gives an option to replay if the game ends
// ends the program if the window is closed
public class UserGame extends Game implements ActionListener {

    // the buttons in a 2d grid
    private JButton[][] cells;
    // the window they're located in
    private JFrame minesweeper;

    public UserGame(int width, int height, int mines) {
        super(width, height, mines);
    }

    // create the array of buttons that represent the cells
    private void initializeCells() {
        for (int r = 0; r < getHeight(); r++) {
            for (int c = 0; c < getWidth(); c++) {
                // create each (initially covered) cell
                cells[r][c] = new JButton("X");
                cells[r][c].addActionListener(this);

                // give each cell a unique string for its action method
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

    // give win message if you win and lose message if you lose
    // start a new game if
    private void endGame() {
        String message, title;
        if (checkWin()) {
            message = "You win! Play again?";
            title = "Congratulations!";
        } else { // otherwise you lost :(
            message = "BOOM! You lose! Try again?";
            title = "Game Over";
        }

        // create a popup with yes or no answers
        int n = JOptionPane.showConfirmDialog(minesweeper, message, title, JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {
            // restart the game
            reset();
        } else {
            // quit
            minesweeper.dispose();
        }
    }

    // uncover everything that's been expanded
    @Override
    public void expand(int row, int col) {
        super.expand(row, col);
        // the square after being clicked will always be uncovered
        int squareVal = getSquare(row, col);
        // check if it's a 0
        if (squareVal == 0) {
            cells[row][col].setText(".");
        } else if (squareVal < 10) { // check if it's any other number
            cells[row][col].setText(Integer.toString(squareVal));
        } else { // check if it's a mine
            cells[row][col].setText("M");
        }

        // you can't click that cell anymore
        cells[row][col].setEnabled(false);
    }

    // make the cell an F if flagged
    @Override
    public void flag(int row, int col) {
        super.flag(row, col);
        cells[row][col].setText("F");
    }

    // reset the Game and the window
    @Override
    public void reset() {
        super.reset();
        if (minesweeper == null) {
            // create the minesweeper window
            minesweeper = new JFrame("Minesweeper");
            // exit the program if the user closes the window
            minesweeper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // initialize the array of buttons
            cells = new JButton[getHeight()][getWidth()];
        }

        // reset the layout and reinitialize
        // make the layout of the window into a grid for buttons
        minesweeper.setContentPane(new JPanel(new GridLayout(getHeight(), getWidth())));

        // reset the array of buttons to their covered label
        initializeCells();

        // make one cell an M so that it fits properly on the cells when opened
        cells[0][0].setText("M");
        minesweeper.pack();
        cells[0][0].setText("X");
        // show the window
        minesweeper.setVisible(true);
    }
}
