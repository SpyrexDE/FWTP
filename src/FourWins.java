import cli.Cli;

public class FourWins {

    public static Game game;
    public static Cli cli; 

    public static void main(String[] args) {
        cli = new Cli();
        game = new Game(cli);
    }
}
