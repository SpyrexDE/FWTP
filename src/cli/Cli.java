package cli;

import java.util.Scanner;

public class Cli {

    public Cli() {

    }

    public void redraw(int[][] field) {
        clearConsole();

        drawField(field);
    }

    // Get terminal input
    public String getInput() {
        Scanner in = new Scanner(System.in);
 
        String input = in.nextLine();
        return input;
    }
    

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
    }

    public static void drawField(int[][] field) {
        System.out.println(" 1 2 3 4 5 6 7");
        for (int i = 0; i < 6; i++) {
            System.out.print("|");
            for (int j = 0; j < 7; j++) {
                System.out.print(String.valueOf(field[i][j]) + "|");
            }
            System.out.println();
        }
        System.out.println("---------------");
    }
}
