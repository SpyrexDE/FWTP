import cli.Cli;
import networking.ActionType;
import networking.FWConnection;
import networking.FWError;
import networking.FWErrorType;
import networking.FWTP;

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

        this.con = new FWConnection(cli);

        gameLoop();
    }

    private void gameLoop() {
        boolean myTurn = con.isHosting();
        boolean turnCompleted = false;
    
        cli.drawField(field, true);
    
        while (true) {
            if(!myTurn) {
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
                    int input = Integer.parseInt(cli.getInput());
                    if(put(input, false)) {
                        turnCompleted = true;
                    }
                } catch(Exception e) {
                    // pass
                }
            }
            if(turnCompleted) {
                int won = checkForWin(field);
                if(won != 0) {
                    applyWinMarkers(field, won);
                    cli.drawField(field, true);
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
        if(position < 1 || position > getFieldWidth()) {
            if(enemy)
                con.getSocket().send(new FWError(FWErrorType.INVALID_MOVE, "Invalid position"));
            else
                cli.error("Invalid position", "The position must be between 1 and " + getFieldWidth());
            return false;
        }

        // put at the bottom
        for (int i = getFieldHeight() - 1; i >= 0; i--) {
            // if the field is empty (not filled by player 1 or 2)
            if(field[i][position - 1] != 1 && field[i][position - 1] != 2) {
                field[i][position - 1] = enemy ? 2 : 1;
                break;
            }
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
