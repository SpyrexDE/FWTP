package networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


// Class to connect to other clients and send them messages
public class FWSocket {
    final int port = 4444;
    private Socket socket;
    private ServerSocket server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private BufferedReader bIn;
    private BufferedWriter bOut;
    private boolean connected;
    
    public FWSocket() {
        this.connected = false;
    }

    public void connect(String ip) {
        try {
            this.socket = new Socket(ip, this.port);
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.bIn = new BufferedReader(new InputStreamReader(this.in));
            this.bOut = new BufferedWriter(new OutputStreamWriter(this.out));
            this.connected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void host() {
        try {
            System.out.println("Hosting on port: " + this.port);
            try (ServerSocket server = new ServerSocket(this.port)) {
                System.out.println("Hosting on IP: " + InetAddress.getLocalHost().getHostAddress());
                this.socket = server.accept();
            }
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.bIn = new BufferedReader(new InputStreamReader(this.in));
            this.bOut = new BufferedWriter(new OutputStreamWriter(this.out));
            System.out.println("Client connected");
            this.connected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void disconnect() {
        try {
            if(this.server != null)
                this.server.close();
            else
                this.socket.close();
            this.connected = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void send(FWTP packet) {
        try {
            bOut.write(packet.toString() + "\n");
            bOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FWTP receive() {
        if(!this.connected)
            return null;
        try {
            String packet = bIn.readLine();
            String[] parts = packet.split("\\|");
            ActionType type = ActionType.valueOf(parts[0]);
            Object obj = null;
            if(parts.length > 1) {
                if(type == ActionType.HANDSHAKE_INIT || type == ActionType.HANDSHAKE_ACK) {
                    obj = parts[1];
                } else if(type == ActionType.FEHLER) {
                    System.arraycopy(parts, 1, obj, 0, parts.length - 2);
                } else if(type == ActionType.EINWURF) {
                    obj = Integer.parseInt(parts[1]);
                }
            }
            return new FWTP(type, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
