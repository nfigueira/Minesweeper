import java.util.Scanner;

// runs a minesweeper game for the user to play
public class Play {

    private static Scanner kb = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("What size grid would you like to play on?");
        // if they don't put an integer, keep asking
        int size = askForAnInteger();
        // if the size is 0 or less, then there's no board
        if (size <= 0) {
            System.out.println("Wow! You managed to create a board without any squares to click! I guess that means you win?");
            return;
        }

        System.out.println("How many mines?");
        // if they don't put an integer, keep asking
        int numMines = askForAnInteger();
        // if the are more mines than the size of the board, then you've already won
        if (numMines >= size * size) {
            System.out.println("Wow! All the squares are mines already! I guess that means you win?");
            return;
        } else if (numMines <= 0) { // if there aren't any mines, you've already won
            System.out.println("Wow! There aren't any mines! I guess that means you win?");
        }

        // Play the game!
        // Constructing the UserGame does everything
        new UserGame(size, size, numMines);
    }

    // keeps asking until the belligerent user puts in an integer
    private static int askForAnInteger() {
        String input = kb.nextLine();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Please enter an INTEGER >:(");
            return askForAnInteger();
        }
    }
}
