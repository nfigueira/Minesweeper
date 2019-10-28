import java.util.Scanner;

public class Minesweeper {

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
        } else if (numMines < 0) { // Negative mines is 0, so just make it 0
            System.out.println("That's not very many mines :/");
            numMines = 0;
        }

        updatePrintBoard(0, size, size);
    }



    // reprint the board starting from startRow
    private static void updatePrintBoard(int startRow, int rows, int columns) {
        if (startRow == 0) {
            printHorizontalLine(columns);
        }
        System.out.println("|")
        for (int r = startRow; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                printSquare(r, c);
            }
            printHorizontalLine(columns);
        }
    }

    // prints one square plus its right border
    private static void printHorizontalLine(int columns) {
        System.out.print("-");
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
