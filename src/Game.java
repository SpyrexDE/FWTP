import cli.Cli;
import networking.FWSocket;

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

    public Game(Cli cli) {
        this.cli = cli;

        try {
            this.socket = new FWSocket("localhost", 4444);
            this.socket.connect();
        } catch (Exception e) {
            System.out.println("Failed to connect to peer");
        }

        gameLoop();
    }

    private void gameLoop() {
        while (true) {
            socket.receive();
            cli.redraw(field);
            
            try {
                int input = Integer.parseInt(cli.getInput());
                put(input);
            } catch(Exception e) {
                // pass
            }
        }
    }

    public void put(int position) {
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
        socket.send(position);
    }

    public int getFieldWidth() {
        return field[0].length;
    }

    public int getFieldHeight() {
        return field.length;
    }

}
