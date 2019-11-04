// the game of minesweeper
public class Game {

    // an array of cells representing the board
    private Cell[][] board;
    
    // number of bombs left (flagging one counts as "finding" a bomb)
    private int bombsLeft;
    
    // total number of mines
    private int totalBombs;
    
    // number of squares needed to uncover (starts with board size - mines)
    private int squaresLeft;

    // the dimensions of the board
    private int width;
    private int height;

    public Game(int width, int height, int mines) {
        // create an array for the board
        board = new Cell[height][width];
        
        // set the dimensions
        this.width = width;
        this.height = height;
        totalBombs = mines;
        
        // initialize everything
        reset();
    }

    // create all the cells on the board
    private void createBoard(int width, int height) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                //create new cells
                board[row][col] = new Cell();
            }
        }
    }

    // initialize a game without a mine at r, c
    // create numMines mines at random locations
    // make all other cells the correct number
    private void initializeBoard(int numMines, int r, int c) {
        // the number of squares left to iterate over (used for placing mines)
        int numSquaresLeft = width * height;

        // if we haven't passed the (r, c) cell, we need to subtract 1 from numSquaresLeft, since we can't put a mine there
        int beforeFirstClick = 1;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // make (row, col) a bomb with probability bombs left / numSquaresLeft, unless it's the first click
                if (row == r && col == c) {
                    beforeFirstClick = 0;
                } else if ((numSquaresLeft - beforeFirstClick) * Math.random() < numMines) {
                    board[row][col].addValue(10);
                    numMines--;
                    // add 1 to adjacent squares of the mine
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
                board[r][c].addValue(1);
            }
        }
    }

    // Clicks on the square with coordinates given
    // Returns false if the game continues, true otherwise
    public boolean click(int row, int col) {
        // initialize if it's the first click (so the first square isn't a mine)
        if (squaresLeft == width * height - totalBombs) {
            initializeBoard(totalBombs, row, col);
        }

        // if it's flagged, unflag it
        if (board[row][col].isFlagged()) {
            flag(row, col);
        }

        // if there are no bombs around, uncover all surrounding squares
        expand(row, col);

        // it's already uncovered, so don't need to worry about that
        if (board[row][col].getValue() > 9) { // if it's a bomb, game over
            // make sure that checkWin is false after hitting a bomb
            squaresLeft += 1;
            return true;
        }

        // if you won, game over
        return checkWin();
    }

    // If you don't have to click any more squares, you win!
    public boolean checkWin() {
        return squaresLeft == 0;
    }

    // Expands around the clicked square until there aren't any 0 squares
    public void expand(int row, int col) {
        Cell cell = board[row][col];
        // If it's already uncovered, you don't need to expand anymore
        // If it's flagged, you can't expand on it
        if (!cell.isUncovered() && !cell.isFlagged()) {
            // opening a square means one left needed to uncover
            squaresLeft -= 1;

            // make the square opened
            cell.click();

            // If its value is 0, expand!
            if (cell.getValue() == 0) {
                // checks for edge cases using min and max
                for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, board.length); r++) {
                    for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, board[0].length); c++) {
                        // repeats for its neighbors
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
            bombsLeft++;
        } else {
            bombsLeft--;
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

    public int getbombsLeft() {
        return bombsLeft;
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    // resets the game to start over
    public void reset() {
        // reset the current number of bombs left
        bombsLeft = totalBombs;
        
        // the number of squares left is the number of squares total minus the number of mines
        squaresLeft = width * height - bombsLeft;
        
        // initializes the cells in the array
        createBoard(width, height);
    }
}
