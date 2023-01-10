package networking;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
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
    
    public void send(FWTP packet) {
        try {
            this.out.writeObject(packet.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FWTP receive() {
        try {
            String packet = (String) this.in.readObject();
            String[] parts = packet.split("\\|");
            ActionType type = ActionType.valueOf(parts[0]);
            // Type can be HANDSHAKE_INIT, HANDSHAKE_ACK, ERROR, PUT
            Object obj = null;
            if(parts.length > 1) {
                if(type == ActionType.HANDSHAKE_INIT || type == ActionType.HANDSHAKE_ACK) {
                    obj = parts[1];
                } else if(type == ActionType.ERROR) {
                    obj = parts[1];
                } else if(type == ActionType.PUT) {
                    obj = Integer.parseInt(parts[1]);
                }
            }
            
            return new FWTP(type, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
