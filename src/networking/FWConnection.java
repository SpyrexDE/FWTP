package networking;

import java.io.InputStreamReader;

import cli.Cli;

import java.io.BufferedReader;

public class FWConnection {

    FWSocket socket;
    boolean hosting;

    public FWConnection(Cli cli) {
        try {
            this.socket = new FWSocket();

            cli.print("Enter a valid ip if you want to connect to someone, press enter to host: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = br.readLine();
            
            if(s.equals("")) {
                this.socket.host();
                this.hosting = true;
            }
            else {
                this.socket.connect(s);
                this.hosting = false;
            }

        } catch (Exception e) {
            cli.print("Failed to connect to peer");
        }
    }

    public FWSocket getSocket() {
        return this.socket;
    }

    public boolean isHosting() {
        return this.hosting;
    }
}
