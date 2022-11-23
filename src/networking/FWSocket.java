package networking;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


// Class to connect to other clients and send them messages
public class FWSocket {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String host;
    private int port;
    private boolean connected;
    
    public FWSocket(String host, int port) {
        this.host = host;
        this.port = port;
        this.connected = false;
    }

    public void connect() {
        try {
            this.socket = new Socket(InetAddress.getByName(this.host), this.port);
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.connected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void disconnect() {
        try {
            this.socket.close();
            this.connected = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void send(Object obj) {
        try {
            this.out.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FWTP receive() {
        return new FWTP();
    }
}
