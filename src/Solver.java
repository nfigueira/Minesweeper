// attempts to solve 100000 different 10 by 10 minesweeper games or demonstrates the solver for one game
public class Solver {

    private final static int SIZE = 10;
    private final static int MINES = 10;

    private static int trials = 100000;

    // the minesweeper game
    private static Game game;

    public static void main(String[] args) {

        // calculates the time taken by getting the start time and subtracting from the ending time
        long startTime = System.currentTimeMillis();

        // if the user passes in "view" as an argument, make the game visible
        if (args.length > 1 && args[1].equals("view")) {
            game = new UserGame(SIZE, SIZE, MINES);
            trials = 1;
        } else {
            // create the game!
            game = new Game(SIZE, SIZE, MINES);
        }

        // solve trials times
        int wins = runSolver();

        long endTime = System.currentTimeMillis();
        System.out.println("Trials: " + trials);
        System.out.println("Wins: " + wins);
        System.out.println("Total Time Taken: " + (endTime - startTime));

        // close the window if it made a UserGame
        System.exit(0);
    }

    // returns the number of wins
    private static int runSolver() {
        // start the win count at 0
        int wins = 0;

        // play the game and count wins
        for (int i = 0; i < trials; i++) {
            if (playGame()) {
                wins++;
            }
            game.reset();
        }

        return wins;
    }

    // plays one game
    private static boolean playGame() {

        // just keep going until the games ends, at which point return
        while (true) {
            // we'll use these for which squares to click
            int row;
            int col;

            // if you can't find a deterministic move, just click one randomly that's covered and not a mine
            do {
                row = (int) (Math.random() * SIZE);
                col = (int) (Math.random() * SIZE);
            } while (game.isUncovered(row, col) || game.isFlagged(row, col));

            // check for the end of the game
            if (click(row, col)) {
                return game.checkWin();
            }

            // whether or not we've made a click in the loop
            boolean clicked;
            // only stop looking for deterministic moves once we've iterated over all squares and found nothing
            do {
                clicked = false;

                // iterate over all squares
                for (row = 0; row < SIZE; row++) {
                    for (col = 0; col < SIZE; col++) {
                        // if it's been clicked and is not 0, see if we can click its neighbors
                        if (game.getSquare(row, col) > 0) {
                            // clicked is now true if anything was clicked
                            clicked = clicked | findMove(row, col);
                        }
                    }
                }
                if (game.checkWin()) {
                    return true;
                }
            } while(clicked);
        }
    }

    // solves one square based on its neighbors
    // returns true if it clicked anything
    private static boolean findMove(int row, int col) {
        // fins the value of the current square (only called on an uncovered square)
        int value = game.getSquare(row, col);

        // counter for the number of mines surrounding this square
        int minesAround = getSurroundingMines(row, col);

        // counter for the number of non-flagged covered neighbors
        int coveredNeighbors = getCoveredNeighbors(row, col);

        // if there's nothing to click around it, don't
        if (coveredNeighbors == 0) {
            return false;
        }

        boolean clicked = false;
        // if the number of mines around it is equal to its number, click the unopened neighbors
        if (minesAround == value) {
            for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, SIZE); r++) {
                for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, SIZE); c++) {
                    if (!game.isUncovered(r, c) && !game.isFlagged(r, c)) {
                        click(r, c);
                        clicked = true;
                    }
                }
            }
        } else if (coveredNeighbors + minesAround == value) {
            // if flagging all uncovered neighbors satisfies the value of the square, flag them all
            for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, SIZE); r++) {
                for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, SIZE); c++) {
                    if (!game.isUncovered(r, c) && !game.isFlagged(r, c)) {
                        flag(r, c);
                        clicked = true;
                    }
                }
            }
        }
        // see if we can click anything else
        return clicked || checkPairs(row, col, value - minesAround);

    }

    // checks if we can click anything based on information the neighbors of (row, col)
    private static boolean checkPairs(int row, int col, int minesLeft) {
        // boolean for if we've clicked anything
        boolean clicked = false;
        // iterate over neighbors
        for (int neighborRow = Math.max(row - 1, 0); neighborRow < Math.min(row + 2, SIZE); neighborRow++) {
            for (int neighborCol = Math.max(col - 1, 0); neighborCol < Math.min(col + 2, SIZE); neighborCol++) {

                // 3 conditions:
                // it's not the original cell
                // it's uncovered
                // the original cell's uncovered neighbors are also it's neighbors
                if (!(row == neighborRow && col == neighborCol) && game.getSquare(neighborRow, neighborCol) > 0
                    && sameNeighbors(row, col, neighborRow, neighborCol)) {

                    // number of mines currently around the neighbor
                    int neighborMines = getSurroundingMines(neighborRow, neighborCol);

                    // the neighbors value
                    int neighborValue = game.getSquare(neighborRow, neighborCol);

                    if (minesLeft == neighborValue - neighborMines) {
                        // click neighbor's neighbors that don't touch the original cell
                        for (int r = Math.max(neighborRow - 1, 0); r < Math.min(neighborRow + 2, SIZE); r++) {
                            for (int c = Math.max(neighborCol - 1, 0); c < Math.min(neighborCol + 2, SIZE); c++) {
                                // check that it's covered and not a neighbor of original cell
                                if (!game.isFlagged(r, c) && !game.isUncovered(r, c)
                                        && (Math.abs(r - row) > 1 || Math.abs(c - col) > 1)) {
                                    click(r, c);
                                    clicked = true;
                                }
                            }
                        }
                    } else if (neighborValue - neighborMines - minesLeft == getCoveredNeighbors(neighborRow, neighborCol)
                        - getCoveredNeighbors(row, col)) {
                        // if the difference between the mines left of the two squares equals the number of unique
                        // adjacent cells (not adjacent to original cell) the neighbor has, then flag the unique cells
                        for (int r = Math.max(neighborRow - 1, 0); r < Math.min(neighborRow + 2, SIZE); r++) {
                            for (int c = Math.max(neighborCol - 1, 0); c < Math.min(neighborCol + 2, SIZE); c++) {
                                if (!game.isFlagged(r, c) && !game.isUncovered(r, c)
                                    && (Math.abs(r - row) > 1 || Math.abs(c - col) > 1)) {
                                    flag(r, c);
                                    clicked = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return clicked;
    }

    // returns true if all of (row, cols) uncovered neighbors are also neighbors of (nRow, nCol)
    private static boolean sameNeighbors(int row, int col, int nRow, int nCol) {
        for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, SIZE); r++) {
            for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, SIZE); c++) {
                if (!game.isUncovered(r, c) && !game.isFlagged(r, c) && (Math.abs(r - nRow) > 1 || Math.abs(c - nCol) > 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    // returns the number of flags surrounding a square
    private static int getSurroundingMines(int row, int col) {
        int mines = 0;
        for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, SIZE); r++) {
            for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, SIZE); c++) {
                if (game.isFlagged(r, c)) {
                    mines++;
                }
            }
        }
        return mines;
    }

    // returns the number of covered squares surrounding an uncovered square
    private static int getCoveredNeighbors(int row, int col) {
        int covered = 0;
        for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, SIZE); r++) {
            for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, SIZE); c++) {
                if (!game.isUncovered(r, c) && !game.isFlagged(r, c)) {
                    covered++;
                }
            }
        }
        return covered;
    }

    // click a square
    private static boolean click(int row, int col){
        if (trials == 1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        return game.click(row, col);
    }

    // flag a square
    private static void flag(int row, int col) {
        if (trials == 1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        game.flag(row, col);
    }
}
