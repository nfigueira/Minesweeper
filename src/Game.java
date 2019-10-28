public class Game {

    final static private int VALUE = 0;
    final static private int VISIBILITY = 1;
    final static private int COVERED = 0;
    final static private int UNCOVERED = 1;
    final static private int FLAGGED = 2;

    // each spot in the board has 2 numbers:
    // the first one is for its value
    // the second is for its visibility: 0 = covered, 1 = uncovered, 2 = flagged
    // if the number is greater than 9, it's a bomb
    private int[][][] board;
    // number of bombs left (flagging one counts as "finding" a bomb)
    private int numBombs;
    // number of squares needed to uncover (starts with board size - mines)
    private int squaresLeft;

    public Game(int width, int height, int mines) {
        board = new int[height][width][2];
        numBombs = mines;
        squaresLeft = width * height - numBombs;
        initializeBoard(width, height, mines);
    }

    private void initializeBoard(int width, int height, int mines) {
        int numSquaresLeft = width * height;
        for (int row = 0; mines > 0; row++) {
            for (int col = 0; col < width && mines > 0; col++) {
                // make (row, col) a bomb with probability numBombsLeft / numSquaresLeft
                if (numSquaresLeft * Math.random() < mines) {
                    board[row - 1][col - 1][VALUE] = 10;
                    mines--;
                    updateSurroundings(row, col);
                }
                numSquaresLeft--;
            }
        }

    }

    // adds one to all adjacent squares (called after assigning the square to be a mine)
    // it doesn't matter if you add to a mine because >10 means it's a mine
    private void updateSurroundings(int row, int col) {
        // checks for edge cases using min and max
        for (int r = Math.max(row - 1, 0); r < Math.min(row + 1, board.length); r++) {
            for (int c = Math.max(col - 1, 0); c < Math.min(col + 1, board[0].length); c++) {
                board[r][c][VALUE] += 1;
            }
        }
    }

    // Clicks on the square with coordinates given
    // Returns 0 if the game continues, 1 if the player wins, and 2 if the player loses
    public int click(int row, int col) {
        // if there are no bombs around, uncover all surrounding squares
        expand(row, col);
        if (board[row][col][VALUE] > 9) { // if it's a bomb
            return 2;
        }
        // if it's flagged, now it's uncovered so you have an extra supposed mine left
        if (board[row][col][VISIBILITY] == FLAGGED) {
            numBombs++;
        }
        squaresLeft -= 1;
        return checkWin();
    }

    // If you don't have to click any more squares, you win!
    private int checkWin() {
        if (squaresLeft == 0) {
            return 1;
        }
        return 0;
    }

    // Expands around the clicked square until there aren't any 0 squares
    private void expand(int row, int col) {
        // If it's already uncovered, you don't need to expand anymore
        if (board[row][col][VISIBILITY] != UNCOVERED && board[row][col][VALUE] == 0) {
            board[row][col][VISIBILITY] = UNCOVERED;
            // checks for edge cases using min and max
            for (int r = Math.max(row - 1, 0); r < Math.min(row + 1, board.length); r++) {
                for (int c = Math.max(col - 1, 0); c < Math.min(col + 1, board[0].length); c++) {
                    expand(r, c);
                }
            }
        }
    }

    // Flags or unflags the square with coordinates given
    // If it's uncovered, do nothing
    public void flag(int row, int col) {
        if (board[row][col][VISIBILITY] == FLAGGED) {
            numBombs++;
            board[row][col][VISIBILITY] = COVERED;
        } else if (board[row][col][VISIBILITY] == COVERED){
            numBombs--;
            board[row][col][VISIBILITY] = FLAGGED;
        }
    }

    // returns value if uncovered
    // returns -1 if covered
    // returns -2 if flagged
    public int getSquare(int row, int col) {
        int visibility = board[row][col][VISIBILITY];
        if (visibility == COVERED) {
            return -1;
        } else if (visibility == UNCOVERED) {
            return board[row][col][VALUE];
        } else {
            return -2;
        }
    }

    public int getNumBombs() {
        return numBombs;
    }
}
