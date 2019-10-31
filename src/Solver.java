import java.util.HashSet;
import java.util.Set;

public class Solver {

    private final static int SIZE = 10;
    private final static int MINES = 10;

    private final static int TRIALS = 100000;

    // needed for the hash set so that we can compare and remove the same object
    private static int[][][] cellID;

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


        for (int i = 0; i < numTrials; i++) {
            if (playGame(game)) {
                wins++;
            }
            game.reset();
        }

        return wins;
    }

    private static Set<Integer[]> borderClicked;

    private static boolean playGame(Game game) {
        // we will start from the middle and expand outward
        // the cells that rim the clicked area
        borderClicked = new HashSet<>();
        // the cells bordering the clicked area on the outside
        Set<Integer[]> borderUnclicked = new HashSet<>();

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
            if (game.click(row, col)) {
                return game.checkWin();
            }
            boolean clicked;
            do {
                clicked = false;
                /*for (Integer[] square : borderClicked) {
                    row = square[0];
                    col = square[1];
                    */
                for (row = 0; row < SIZE; row++) {
                    for (col = 0; col < SIZE; col++) {
                        if (game.isUncovered(row, col)) {
                            boolean change = oneSquare(game, row, col);
                            clicked = clicked | change;
                        }
                    }
                }
                if (game.checkWin()) {
                    return true;
                }
            } while(clicked);
        }
    }

    // solves one square based on it's neighbors
    // returns true if it clicked anything
    private static boolean oneSquare(Game game, int row, int col) {
        int value = game.getSquare(row, col);

        // counter for the number of mines surrounding this square
        int minesAround = 0;

        // set containing all the covered, unflagged neighbors
        Set<Integer[]> coveredNeighbors = new HashSet<>();
        for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, SIZE); r++) {
            for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, SIZE); c++) {
                if (game.isFlagged(r, c)) {
                    minesAround++;
                } else if (!game.isUncovered(r, c)) {
                    coveredNeighbors.add(new Integer[]{r, c});
                }
            }
        }

        // if there's nothing to click around it, don't
        if (coveredNeighbors.size() == 0) {
            return false;
        }

        // if the number of mines around it is equal to it's number, click the unopened neighbors
        if (minesAround == value) {
            for (Integer[] square : coveredNeighbors) {
                game.click(square[0], square[1]);
            }
            return true;
        } else if (coveredNeighbors.size() + minesAround == value) {
            // if flagging all uncovered neighbors satisfies the value of the square, flag them all
            for (Integer[] square : coveredNeighbors) {
                game.flag(square[0], square[1]);
            }
            return true;
        }
        // if neither happened, we didn't make a click
        return false;
    }

    // solves pairs of squares
    private static void twoSquare(Game game, int row, int col) {
        int value = game.getSquare(row, col);

        // counter for the number of mines surrounding this square
        int minesAround = 0;

        // set containing all the uncovered, unflagged neighbors
        Set<Integer[]> uncoveredNeighbors = new HashSet<>();
        for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, SIZE); r++) {
            for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, SIZE); c++) {
                if (game.isFlagged(r, c)) {
                    minesAround++;
                } else if (game.isUncovered(r, c)) {
                    uncoveredNeighbors.add(new Integer[]{r, c});
                }
            }
        }

        // if the number of mines around it is equal to it's number, click the unopened neighbors
        if (minesAround == value) {
            for (Integer[] square : uncoveredNeighbors) {
                game.click(square[0], square[1]);
            }
        } else if (uncoveredNeighbors.size() + minesAround == value) {
            // if flagging all uncovered neighbors satisfies the value of the square, flag them all
            for (Integer[] square : uncoveredNeighbors) {
                game.flag(square[0], square[1]);
            }
        }
    }
}
