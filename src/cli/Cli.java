package cli;

public class Cli {
    public static void drawField(String[][] field) {
        System.out.println("  1 2 3 4 5 6 7");
        for (int i = 0; i < 6; i++) {
            System.out.print("|");
            for (int j = 0; j < 7; j++) {
                System.out.print(field[i][j] + "|");
            }
            System.out.println();
        }
        System.out.println("---------------");
    }
}
