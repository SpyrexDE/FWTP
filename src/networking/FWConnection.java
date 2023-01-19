package networking;

import java.io.InputStreamReader;

import cli.Cli;

import java.io.BufferedReader;

public class FWConnection {

    FWSocket socket;
    boolean hosting;

    public FWConnection() {
        try {
            this.socket = new FWSocket();

            Cli.print("Enter a valid ip if you want to connect to someone, press enter to host: ");
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
            Cli.error("Failed to connect to peer", "The connection could not be established.");
        }
    }

    public FWConnection(boolean host) {
        try {
            if(host) {
                this.socket = new FWSocket();
                this.socket.host();
                this.hosting = true;
            }else {
                this.socket = new FWSocket();
                this.socket.connect("localhost");
                this.hosting = false;
            }
        } catch (Exception e) {
            Cli.error("Failed to connect to peer", "The connection could not be established.");
        }
    }

    public FWSocket getSocket() {
        return this.socket;
    }

    public boolean isHosting() {
        return this.hosting;
    }
}
