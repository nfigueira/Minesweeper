public class Cell {

    private int value;
    private boolean flagged;
    private boolean uncovered;

    public Cell() {
        value = 0;
        flagged = false;
        uncovered = false;
    }

    // returns the value if it's uncovered, >9 if it's a mine; otherwise -1
    public int getValue() {
        if (uncovered) {
            return value;
        } else {
            return -1;
        }
    }

    // returns whether it's flagged
    public boolean isFlagged() {
        return flagged;
    }

    // returns whether it's covered
    public boolean isUncovered() {
        return uncovered;
    }

    // toggles the flag if it's covered
    public void flag() {
        if (!uncovered) {
            flagged = !flagged;
        }
    }

    // uncovers the cell
    public void click() {
        flagged = false;
        uncovered = true;
    }

    // adds 1 to the value (for initialization in Game)
    public void addValue(int add) {
        value += add;
    }
}
