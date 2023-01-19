package networking;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import cli.Cli;
import utils.Ansi;

import java.io.BufferedReader;

public class FWConnection {

    FWSocket socket;
    boolean hosting;

    public FWConnection() {
        try {
            this.socket = new FWSocket();

            Cli.print(Ansi.Yellow.colorize("Previous hosts were:"));


            String[] hosts = getCachedHosts();
            for (int i = 0; i < hosts.length; i++) {
                Cli.print(Integer.toString(i) + ". " + Ansi.Cyan.colorize(hosts[i]));
            }

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

            addToCachedHosts(s);

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

    public String[] getCachedHosts() {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        final String PREF_NAME = "HOSTS_CACHE";
        String[] propertyValue = prefs.get(PREF_NAME, "").split("\\|");
    
        return propertyValue;
    }

    public void addToCachedHosts(String host) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        
        final String PREF_NAME = "HOSTS_CACHE";
        String newValue = prefs.get(PREF_NAME, "") + host + "|";
        
        prefs.put(PREF_NAME, newValue);

        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    public FWSocket getSocket() {
        return this.socket;
    }

    public boolean isHosting() {
        return this.hosting;
    }
}
