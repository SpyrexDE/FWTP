package cli;

import java.util.Scanner;

public class Cli {

    Scanner in = new Scanner(System.in);

    public Cli() {

    }

    public void redraw(int[][] field) {
        clearConsole();

        drawField(field);
    }

    // Get terminal input
    public String getInput() {
        print("Please enter a number: ");
        String input = in.nextLine();
        return input;
    }
    

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
    }

    public void drawField(int[][] field) {
        System.out.println("\033[1;36m ╭───┬───┬───┬───┬───┬───┬───╮ \033[0m");
        for (int i = 0; i < 6; i++) {
            System.out.print("\033[1;36m │");
            for (int j = 0; j < 7; j++) {
                if(field[i][j] == 1) {
                    System.out.print(" \033[0;94mO \033[1;36m│");
                } else if(field[i][j] == 2) {
                    System.out.print(" \033[0;91mX \033[1;36m│");
                } else {
                    System.out.print("   │");
                }
            }
            System.out.println();
            if(i!=5)
                System.out.println("\033[1;36m ├───┼───┼───┼───┼───┼───┼───┤ \033[0m");
        }
        System.out.println("\033[1;36m ╰───┴───┴───┴───┴───┴───┴───╯ \033[0m");
        System.out.println("\033[1;96m   1   2   3   4   5   6   7 \033[0m");
    }
    

    public void print(String str) {
        System.out.println(str);
    }

    public void error(String message, String value) {
        System.out.println("\033[1;31m" + message + ":\n\033[0;31m" + value + "\033[0m");
    }
}
