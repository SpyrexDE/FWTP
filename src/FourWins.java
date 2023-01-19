import java.util.Arrays;

import cli.Cli;

public class FourWins {

    public static Game game;
    public static Cli cli;
    public static String[] args;

    public static void main(String[] args) {
        FourWins.args = args;
        game = new Game();
    }
}
