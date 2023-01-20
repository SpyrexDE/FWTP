package networking;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import cli.Cli;
import utils.Ansi;

import java.io.BufferedReader;

public class FWConnection {
    FWSocket socket;
    boolean hosting;

    public FWConnection() {
        List<String> hosts = getCachedHosts(5);
        if(!hosts.isEmpty()) {
            Cli.print(Ansi.Yellow.colorize("Previous hosts were:"));
            
            for (int i = 0; i < hosts.size(); i++) {
                Cli.print(Integer.toString(i) + ". " + Ansi.Cyan.colorize(hosts.get(i)));
            }
        }

        try {
            this.socket = new FWSocket();
            Cli.print("Enter a valid ip or its index if you want to connect to someone, press enter to host: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = br.readLine();
            
            if(s.equals("")) {
                this.socket.host();
                this.hosting = true;
            }
            else {
                if (s.startsWith("!")) {
                    removeFromCachedHosts(Integer.valueOf(s.substring(1)));
                    System.out.println("Removed " + s.substring(1) + " from cache");
                    new FWConnection();
                    return;
                }
                try {
                    int index = Integer.parseInt(s);
                    s = getCachedHost(index);
                } catch (Exception e) {
                    // If it's not a number, it's an ip address
                }
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

    public List<String> getCachedHosts(int limit) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        String hosts = prefs.get("HOSTS_CACHE", "");

        if(hosts.equals(""))
            return new ArrayList<String>();
        
        List<String> hostsList = new ArrayList<>(Arrays.asList(hosts.split("\\|")));
     
        if (limit == -1)
            return hostsList;
        return hostsList.subList(Math.max(0, hostsList.size()-limit), hostsList.size());
    }

    public String getCachedHost(int index) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        String[] hosts = prefs.get("HOSTS_CACHE", "").split("\\|");
        List<String> hostsList = new ArrayList<>(Arrays.asList(hosts));
        
        return hostsList.get(index);
    }

    public void addToCachedHosts(String host) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        
        List<String> propertyValue = getCachedHosts(-1);
        
        if(propertyValue.contains(host) || host.isEmpty())
            return;

        propertyValue.add(host);
        String newValue = String.join("|", propertyValue);
        
        prefs.put("HOSTS_CACHE", newValue);

        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    public void removeFromCachedHosts(int index) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        List<String> propertyValue = getCachedHosts(-1);

        propertyValue.remove(index);
        String newValue = String.join("|", propertyValue);
        
        prefs.put("HOSTS_CACHE", newValue);

        try {
            prefs.sync();
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
