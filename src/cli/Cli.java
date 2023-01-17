package cli;

import java.util.Scanner;

import utils.Ansi;

public class Cli {

    Scanner in = new Scanner(System.in);

    public Cli() {
        
    }

    public void redraw(int[][] field) {
        redraw(field, true);
    }

    public void redraw(int[][] field, boolean draw_warning) {
        clearConsole();

        drawField(field, draw_warning);
    }

    // Get terminal input
    public String getInput() {
        // ◌
        System.out.print(Ansi.Green.colorize("◉") + Ansi.Cyan.colorize(" Your turn 🔢 ➡ "));
        String input = in.nextLine();
        return input;
    }
    

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
    }

    public void drawField(int[][] field, boolean draw_warning) {
        System.out.println("\033[1;36m    ╰•★★ 𝓒𝓸𝓷𝓷𝓮𝓬𝓽 𝓕𝓸𝓾𝓻 ★★•╯    \033[0m");
        System.out.println("\033[1;36m ╭───┬───┬───┬───┬───┬───┬───╮ \033[0m");
        for (int i = 0; i < 6; i++) {
            System.out.print("\033[1;36m │");
            for (int j = 0; j < 7; j++) {
                if(field[i][j] == 1) {
                    System.out.print(" \033[0;94mO \033[1;36m│");
                } else if(field[i][j] == 2) {
                    System.out.print(" \033[0;91mX \033[1;36m│");
                } else if(field[i][j] == 3 && draw_warning) {
                    System.out.print(" \033[0;32m⚠ \033[1;36m│");
                } else if(field[i][j] == 4 && draw_warning) {
                    System.out.print(" \033[0;33m⚠ \033[1;36m│");
                } else if(field[i][j] == 5) {
                    System.out.print(" \033[0;94m\033[9;1;3mO\033[0m \033[1;36m│");
                } else if(field[i][j] == 6) {
                    System.out.print(" \033[0;91m\033[9;1;3mX\033[0m \033[1;36m│");
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

    public void success(String message, String value) {
        System.out.println("\033[1;32m" + message + ":\n\033[0;32m" + value + "\033[0m");
    }
}
