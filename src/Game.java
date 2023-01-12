import java.io.BufferedReader;
import java.io.InputStreamReader;

import cli.Cli;
import networking.ActionType;
import networking.FWSocket;
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
    private FWSocket socket;
    private boolean hosting;

    public Game(Cli cli) {
        this.cli = cli;

        try {
            System.out.println("Enter a valid ip if you want to connect to someone, press enter to host: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = br.readLine();
            this.socket = new FWSocket();

            if(s.equals("")) {
                this.socket.host();
                this.hosting = true;
            }
            else {
                this.socket.connect(s);
                this.hosting = false;
            }
        } catch (Exception e) {
            System.out.println("Failed to connect to peer");
        }

        gameLoop();
    }

    private void gameLoop() {

        boolean myTurn = this.hosting;

        while (true) {
            cli.redraw(field);
            
            if(!myTurn) {
                FWTP received = socket.receive();
                if(received.type == ActionType.EINWURF)
                    put((Integer)received.obj, false);
            } else {
                try {
                    int input = Integer.parseInt(cli.getInput());
                    put(input, true);
                } catch(Exception e) {
                    // pass
                }
            }
            myTurn = !myTurn;
        }
    }

    public void put(int position, boolean sync) {
        if(position < 1 || position > getFieldWidth()) {
            return;
        }

        // put at the bottom
        for (int i = getFieldHeight() - 1; i >= 0; i--) {
            if(field[i][position - 1] == 0) {
                field[i][position - 1] = 1;
                break;
            }
        }

        // send to clients
        if(sync)
            socket.send(new FWTP(ActionType.EINWURF, position));
    }

    public int getFieldWidth() {
        return field[0].length;
    }

    public int getFieldHeight() {
        return field.length;
    }

}
