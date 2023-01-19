package networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import cli.Cli;


// Class to connect to other clients and send them messages
public class FWSocket {
    final int port = 4444;
    final int FWTP_VERSION = 1;

    private Socket socket;
    private ServerSocket server;
    private OutputStream out;
    private InputStream in;
    private BufferedReader bIn;
    private BufferedWriter bOut;
    private boolean connected;
    
    public FWSocket() {
        this.connected = false;
    }

    public void connect(String ip) {
        try {
            // Create connection
            this.socket = new Socket(ip, this.port);

            // Setup streams
            this.out = this.socket.getOutputStream();
            this.in = this.socket.getInputStream();
            this.bIn = new BufferedReader(new InputStreamReader(this.in));
            this.bOut = new BufferedWriter(new OutputStreamWriter(this.out));

            // Aggree on protocol version
            FWTP packet = this.receive();
            if(packet.type != ActionType.HANDSHAKE_INIT) {
                send(new FWError(FWErrorType.UNSUPPORTED_VERSION, "You havent sent a handshake init"));
                this.connected = false;
                return;
            }
            String version_list = (String) packet.body;
            if(Arrays.stream(version_list.split(",")).anyMatch(String.valueOf(FWTP_VERSION)::equals)) {
                this.send(new FWTP(ActionType.HANDSHAKE_ACK, this.FWTP_VERSION));
            } else {
                this.send(new FWError(FWErrorType.UNSUPPORTED_VERSION, "Unsupported versions, this client only supports FWTP V" + String.valueOf(FWTP_VERSION)));
                this.connected = false;
                return;
            }

            this.connected = true;
        } catch (UnknownHostException e) {
            Cli.error("Unknown host", "The host you tried to connect to is unknown");
        } catch (IOException e) {
            Cli.error("Connection failed", "The connection to the host failed");
        }
    }

    public void host() {
        try {
            // Create connection
            System.out.println("Hosting on port: " + this.port);
            try (ServerSocket server = new ServerSocket(this.port)) {
                System.out.println("Hosting on IP: " + InetAddress.getLocalHost().getHostAddress());
                this.socket = server.accept();
            }
            // Setup streams
            this.out = this.socket.getOutputStream();
            this.in = this.socket.getInputStream();
            this.bIn = new BufferedReader(new InputStreamReader(this.in));
            this.bOut = new BufferedWriter(new OutputStreamWriter(this.out));

            // Aggree on protocol version
            this.send(new FWTP(ActionType.HANDSHAKE_INIT, this.FWTP_VERSION));
            FWTP packet = this.receive();
            if(packet.type == ActionType.HANDSHAKE_ACK) {
                int version = Integer.valueOf(packet.body.toString());
                if(version != this.FWTP_VERSION) {
                    this.send(new FWError(FWErrorType.UNSUPPORTED_VERSION, "Unsupported FWTP version"));
                    this.disconnect();
                    return;
                }
            } else {
                this.send(new FWError(FWErrorType.UNSUPPORTED_VERSION, "Unsupported FWTP version"));
                this.disconnect();
                return;
            }

            System.out.println("Client connected using FWTP version: V" + this.FWTP_VERSION);
            this.connected = true;
        } catch (Exception e) {
            Cli.error("Connection failed", "The connection to the host failed");
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
            Cli.error("Disconnect failed", "The disconnect failed");
        }
    }
    
    public void send(FWTP packet) {
        try {
            bOut.write(packet.toString() + "\n");
            bOut.flush();
        } catch (IOException e) {
            Cli.error("Failed sending package", "The package could not be sent");
        }
    }

    public FWTP receive() {
        try {
            String packet = bIn.readLine();
            String[] parts = packet.split("\\|");
            ActionType type = ActionType.values()[Integer.valueOf(parts[0])];
            Object obj = null;
            if(parts.length > 1) {
                if(type == ActionType.HANDSHAKE_INIT || type == ActionType.HANDSHAKE_ACK) {
                    obj = parts[1];
                } else if(type == ActionType.ERROR) {
                    obj = new String[parts.length - 1];
                    System.arraycopy(parts, 1, obj, 0, parts.length - 1);
                    return new FWError(obj);
                } else if(type == ActionType.PUT) {
                    obj = Integer.parseInt(parts[1]);
                }
            }
            return new FWTP(type, obj);
        } catch (IOException e) {
            Cli.error("Failed receiving package", "The package could not be received");
        }
        return null;
    }
}
