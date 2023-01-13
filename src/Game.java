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
    
        cli.drawField(field);
    
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
                cli.redraw(field);
                myTurn = !myTurn;
                turnCompleted = false;
            }
            if(myTurn) {
                turnCompleted = false;
            }
        }
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
            if(field[i][position - 1] == 0) {
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
