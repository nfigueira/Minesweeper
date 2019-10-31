public class Game {

    private Cell[][] board;
    // number of bombs left (flagging one counts as "finding" a bomb)
    private int numBombs;
    // total number of mines
    private int totalBombs;
    // number of squares needed to uncover (starts with board size - mines)
    private int squaresLeft;

    private int width;
    private int height;

    public Game(int width, int height, int mines) {
        board = new Cell[height][width];
        this.width = width;
        this.height = height;
        numBombs = mines;
        totalBombs = mines;
        squaresLeft = width * height - numBombs;
        initializeBoard(width, height, mines);
    }

    private void initializeBoard(int width, int height, int mines) {
        int numSquaresLeft = width * height;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // if it doesn't already exist, create a new cell
                if (board[row][col] == null) {
                    board[row][col] = new Cell();
                }
                // make (row, col) a bomb with probability numBombsLeft / numSquaresLeft
                if (numSquaresLeft * Math.random() < mines) {
                    board[row][col].addValue(10);
                    mines--;
                    updateMineSurroundings(row, col);
                }
                numSquaresLeft--;
            }
        }
    }

    // adds one to all adjacent squares (called after assigning the square to be a mine)
    // it doesn't matter if you add to a mine because >9 means it's a mine
    private void updateMineSurroundings(int row, int col) {
        // checks for edge cases using min and max
        for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, board.length); r++) {
            for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, board[0].length); c++) {
                // if it doesn't already exist, then create a new cell
                if (board[r][c] == null) {
                    board[r][c] = new Cell();
                }
                board[r][c].addValue(1);
            }
        }
    }

    // Clicks on the square with coordinates given
    // Returns false if the game continues, true otherwise
    public boolean click(int row, int col) {
        // if it's flagged, now it's uncovered so you have an extra supposed mine left
        if (board[row][col].isFlagged()) {
            numBombs++;
        }
        // if there are no bombs around, uncover all surrounding squares
        expand(row, col);
        // it's already uncovered, so don't need to worry about that
        if (board[row][col].getValue() > 9) { // if it's a bomb
            return true;
        }
        return checkWin();
    }

    // If you don't have to click any more squares, you win!
    public boolean checkWin() {
        if (squaresLeft == 0) {
            return true;
        }
        return false;
    }

    // Expands around the clicked square until there aren't any 0 squares
    public void expand(int row, int col) {
        Cell cell = board[row][col];
        // If it's already uncovered, you don't need to expand anymore
        if (!cell.isUncovered()) {
            squaresLeft -= 1;
            cell.click();
            // If its value is 0, expand!
            if (cell.getValue() == 0) {
                // checks for edge cases using min and max
                for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, board.length); r++) {
                    for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, board[0].length); c++) {
                        expand(r, c);
                    }
                }
            }
        }
    }

    // Flags or unflags the square with coordinates given
    // If it's uncovered, do nothing
    public void flag(int row, int col) {
        if (board[row][col].isFlagged()) {
            numBombs++;
        } else {
            numBombs--;
        }
        board[row][col].flag();
    }

    // returns value if uncovered
    // returns -1 otherwise
    public int getSquare(int row, int col) {
        return board[row][col].getValue();
    }

    // checks whether a specific cell is uncovered
    public boolean isUncovered(int row, int col){
        return board[row][col].isUncovered();
    }

    // checks whether a specific cell is flagged
    public boolean isFlagged(int row, int col) {
        return board[row][col].isFlagged();
    }

    public int getNumBombs() {
        return numBombs;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void reset() {
        numBombs = totalBombs;
        squaresLeft = width * height - numBombs;
        board = new Cell[height][width];
        initializeBoard(width, height, totalBombs);
    }
}
