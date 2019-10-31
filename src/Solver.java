public class Solver {

    private final static int SIZE = 10;
    private final static int MINES = 10;

    private final static int TRIALS = 100000;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        int wins = runSolver(TRIALS);
        long endTime = System.currentTimeMillis();
        System.out.println("Trials: " + TRIALS);
        System.out.println("Wins: " + wins);
        System.out.println("Total Time Taken: " + (endTime - startTime));
    }

    // returns the number of wins
    private static int runSolver(int numTrials) {
        // create the game!
        Game game = new Game(SIZE, SIZE, MINES);

        // start the win count at 0
        int wins = 0;

        // play the game and count wins
        for (int i = 0; i < numTrials; i++) {
            if (playGame(game)) {
                wins++;
            }
            game.reset();
        }

        return wins;
    }

    private static boolean playGame(Game game) {

        // just keep going until the games ends, at which point return
        while (true) {
            // we'll use these for which squares to click
            int row;
            int col;
            // if you can't find a deterministic move, just click one randomly
            do {
                row = (int) (Math.random() * SIZE);
                col = (int) (Math.random() * SIZE);
            } while (game.isUncovered(row, col) || game.isFlagged(row, col));
            // check for the end of the game
            if (game.click(row, col)) {
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
                            // clicked is now true if it clicked its neighbors
                            clicked = clicked | findMove(game, row, col);
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
    private static boolean findMove(Game game, int row, int col) {
        int value = game.getSquare(row, col);

        // counter for the number of mines surrounding this square
        int minesAround = 0;

        // counter for the number of covered neighbors that are not flagged
        int coveredNeighbors = 0;
        for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, SIZE); r++) {
            for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, SIZE); c++) {
                if (game.isFlagged(r, c)) {
                    minesAround++;
                } else if (!game.isUncovered(r, c)) {
                    coveredNeighbors++;
                }
            }
        }

        // if there's nothing to click around it, don't
        if (coveredNeighbors == 0) {
            return false;
        }

        // if the number of mines around it is equal to its number, click the unopened neighbors
        if (minesAround == value) {
            for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, SIZE); r++) {
                for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, SIZE); c++) {
                    if (!game.isUncovered(r, c) && !game.isFlagged(r, c)) {
                        game.click(r, c);
                    }
                }
            }
            return true;
        } else if (coveredNeighbors + minesAround == value) {
            // if flagging all uncovered neighbors satisfies the value of the square, flag them all
            for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, SIZE); r++) {
                for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, SIZE); c++) {
                    if (!game.isUncovered(r, c) && !game.isFlagged(r, c)) {
                        game.flag(r, c);
                    }
                }
            }
            return true;
        } else {
            return checkNeighbors(game, row, col, value - minesAround);
        }
    }

    private static boolean checkNeighbors(Game game, int row, int col, int minesLeft) {
        boolean clicked = false;
        for (int neighborRow = Math.max(row - 1, 0); neighborRow < Math.min(row + 2, SIZE); neighborRow++) {
            for (int neighborCol = Math.max(col - 1, 0); neighborCol < Math.min(col + 2, SIZE); neighborCol++) {
                if (!(row == neighborRow && col == neighborCol) && game.getSquare(neighborRow, neighborCol) > 0) {
                    if (sameNeighbors(game, row, col, neighborRow, neighborCol)) {
                        int neighborMines = getSurroundingMines(game, neighborRow, neighborCol);
                        int neighborValue = game.getSquare(neighborRow, neighborCol);
                        if (minesLeft == neighborValue - neighborMines) {
                            // click neighbor's neighbors that don't touch the original cell
                            for (int r = Math.max(neighborRow - 1, 0); r < Math.min(neighborRow + 2, SIZE); r++) {
                                for (int c = Math.max(neighborCol - 1, 0); c < Math.min(neighborCol + 2, SIZE); c++) {
                                    if (!game.isFlagged(r, c) && !game.isUncovered(r, c) && (Math.abs(r - row) > 1 || Math.abs(c - col) > 1)) {
                                        game.click(r, c);
                                        clicked = true;
                                    }
                                }
                            }
                        } else if (minesLeft - neighborValue + neighborMines == getCoveredNeighbors(game, row, col) - getCoveredNeighbors(game, neighborRow, neighborCol)) {
                            // flag them instead
                            for (int r = Math.max(neighborRow - 1, 0); r < Math.min(neighborRow + 2, SIZE); r++) {
                                for (int c = Math.max(neighborCol - 1, 0); c < Math.min(neighborCol + 2, SIZE); c++) {
                                    if (!game.isFlagged(r, c) && !game.isUncovered(r, c) && (Math.abs(r - row) > 1 || Math.abs(c - col) > 1)) {
                                        game.flag(r, c);
                                        clicked = true;
                                    }
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
    private static boolean sameNeighbors(Game game, int row, int col, int nRow, int nCol) {
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
    private static int getSurroundingMines(Game game, int row, int col) {
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
    private static int getCoveredNeighbors(Game game, int row, int col) {
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
}
