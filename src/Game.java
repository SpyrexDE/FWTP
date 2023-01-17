import cli.Cli;
import networking.ActionType;
import networking.FWConnection;
import networking.FWError;
import networking.FWErrorType;
import networking.FWTP;
import utils.Ansi;

public class Game {
    
    public int[][] field = {
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
    };

    private Cli cli;
    FWConnection con;

    public Game(Cli cli) {
        this.cli = cli;

        // Establish connection
        if(FourWins.args.length > 0) {
            switch(FourWins.args[0]) {
                case "host":
                    this.con = new FWConnection(cli, true);
                    break;
                case "connect_local":
                    this.con = new FWConnection(cli, false);
                    break;
            }
        } else {
            this.con = new FWConnection(cli);
        }

        gameLoop();
    }

    private void gameLoop() {
        boolean myTurn = con.isHosting();
        boolean turnCompleted = false;
    
        cli.drawField(field, true);
    
        while (true) {
            if(!myTurn) {
                cli.print(Ansi.Red.colorize("◯") + Ansi.Yellow.colorize(" Waiting for opponent..."));

                FWTP received = con.getSocket().receive();
                if(received.type == ActionType.EINWURF) {
                    put((Integer)received.body, true);
                    turnCompleted = true;
                }
                if(received instanceof FWError) {
                    FWError error = (FWError) received;
                    cli.error("Your opponent threw an error", "[" + error.type.toString() + "] " + error.message);
                    break;
                }
            } else {
                try {
                    int input = Integer.parseInt(cli.getInput()) - 1;
                    if(put(input, false)) {
                        turnCompleted = true;
                    }
                } catch(NumberFormatException e) {
                    cli.error("Invalid input", "Please enter a number between 1 and 7");
                }
            }
            if(turnCompleted) {
                int won = checkForWin(field);
                if(won != 0) {
                    applyWinMarkers(field, won);
                    cli.redraw(field, false);
                    if(won == 1) {
                        cli.success("You won!", "Congratulations!");
                    } else {
                        cli.error("You lost!", "Better luck next time!");
                    }
                    break;
                }

                applyWarnings(field);
                cli.redraw(field);
                myTurn = !myTurn;
                turnCompleted = false;
            }
            if(myTurn) {
                turnCompleted = false;
            }
        }
    }
    
    // Mark only the four fields in a row and that made the player win the game
    private void applyWinMarkers(int[][] field, int won) {
        // Check for horizontal win
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length - 3; j++) {
                if(field[i][j] == won && field[i][j+1] == won && field[i][j+2] == won && field[i][j+3] == won) {
                    field[i][j] = won + 4;
                    field[i][j+1] = won + 4;
                    field[i][j+2] = won + 4;
                    field[i][j+3] = won + 4;
                    return;
                }
            }
        }
        // Check for vertical win
        for (int i = 0; i < field.length - 3; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if(field[i][j] == won && field[i+1][j] == won && field[i+2][j] == won && field[i+3][j] == won) {
                    field[i][j] = won + 4;
                    field[i+1][j] = won + 4;
                    field[i+2][j] = won + 4;
                    field[i+3][j] = won + 4;
                    return;
                }
            }
        }
        // Check for diagonal win
        for (int i = 0; i < field.length - 3; i++) {
            for (int j = 0; j < field[i].length - 3; j++) {
                if(field[i][j] == won && field[i+1][j+1] == won && field[i+2][j+2] == won && field[i+3][j+3] == won) {
                    field[i][j] = won + 4;
                    field[i+1][j+1] = won + 4;
                    field[i+2][j+2] = won + 4;
                    field[i+3][j+3] = won + 4;
                    return;
                }
            }
        }
        // Check for diagonal win
        for (int i = 0; i < field.length - 3; i++) {
            for (int j = 3; j < field[i].length; j++) {
                if(field[i][j] == won && field[i+1][j-1] == won && field[i+2][j-2] == won && field[i+3][j-3] == won) {
                    field[i][j] = won + 4;
                    field[i+1][j-1] = won + 4;
                    field[i+2][j-2] = won + 4;
                    field[i+3][j-3] = won + 4;
                    return;
                }
            }
        }
    }

    // If someone would win in the next turn, places a 3 in the field that would make player 1 win and a 4 in the field that would make player 2 win
    private void applyWarnings(int[][] field) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if(field[i][j] == 0) {
                    
                    field[i][j] = 1;
                    if(checkForWin(field) == 1) {
                        field[i][j] = 3;
                        return;
                    }

                    field[i][j] = 2;
                    if(checkForWin(field) == 2) {
                        field[i][j] = 4;
                        return;
                    }
                    
                    // Reset if no one would win
                    field[i][j] = 0;
                }
            }
        }
    }

    // Returns 0 if no one won, 1 if player 1 won and 2 if player 2 won
    private int checkForWin(int[][] field) {
        // check for horizontal win
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length - 3; j++) {
                if(field[i][j] != 0 && field[i][j] == field[i][j + 1] && field[i][j] == field[i][j + 2] && field[i][j] == field[i][j + 3]) {
                    return field[i][j];
                }
            }
        }
    
        // check for vertical win
        for (int i = 0; i < field.length - 3; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if(field[i][j] != 0 && field[i][j] == field[i + 1][j] && field[i][j] == field[i + 2][j] && field[i][j] == field[i + 3][j]) {
                    return field[i][j];
                }
            }
        }
    
        // check for diagonal win
        for (int i = 0; i < field.length - 3; i++) {
            for (int j = 0; j < field[i].length - 3; j++) {
                if(field[i][j] != 0 && field[i][j] == field[i + 1][j + 1] && field[i][j] == field[i + 2][j + 2] && field[i][j] == field[i + 3][j + 3]) {
                    return field[i][j];
                }
            }
        }
    
        // check for diagonal win
        for (int i = 0; i < field.length - 3; i++) {
            for (int j = 3; j < field[i].length; j++) {
                if(field[i][j] != 0 && field[i][j] == field[i + 1][j - 1] && field[i][j] == field[i + 2][j - 2] && field[i][j] == field[i + 3][j - 3]) {
                    return field[i][j];
                }
            }
        }
        return 0;
    }

    public boolean put(int position, boolean enemy) {
        if(position < 0 || position > getFieldWidth() - 1) {
            if(enemy)
                con.getSocket().send(new FWError(FWErrorType.INVALID_MOVE, "Invalid position"));
            else
                cli.error("Invalid position", "The position must be between 1 and " + getFieldWidth());
            return false;
        }

        // put at the bottom
        boolean couldPlace = false;
        for (int i = getFieldHeight() - 1; i >= 0; i--) {
            // if the field is empty (not filled by player 1 or 2)
            if(field[i][position] != 1 && field[i][position] != 2) {
                field[i][position] = enemy ? 2 : 1;
                couldPlace = true;
                break;
            }
        }
        if(!couldPlace) {
            if(enemy)
                con.getSocket().send(new FWError(FWErrorType.INVALID_MOVE, "Column is full"));
            else
                cli.error("Column is full", "The column is full, please choose another one");
            return false;
        }

        // send to clients
        if(!enemy)
            con.getSocket().send(new FWTP(ActionType.EINWURF, position));
        
        return true;
    }

    public int getFieldWidth() {
        return field[0].length;
    }

    public int getFieldHeight() {
        return field.length;
    }

}
