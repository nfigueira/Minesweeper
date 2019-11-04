// play or solve the game
// by default makes a user game
// if the first argument is "solve" run the solver
// if the first argument is "solve" and the second argument is "view", run the visual solver
public class Minesweeper {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("solve")) {
            Solver.main(args);
        }
        Play.main(args);
    }
}
