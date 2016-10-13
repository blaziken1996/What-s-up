package whatsup.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import whatsup.common.ClientSocket;

/**
 * Created by trung on 16/09/2016.
 */
public class Client extends ClientSocket {
    public static Client mainClient;
    private ConcurrentHashMap<InetSocketAddress, FileToSend> receiverFileMap;
    private String host;
    private int port;

    public Client(String host, int port, String name) throws IOException {
        super(new Socket(host, port), name);
        this.host = host;
        this.port = port;
        address = (InetSocketAddress) socket.getLocalSocketAddress();
        receiverFileMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<InetSocketAddress, FileToSend> getReceiverFileMap() {
        return receiverFileMap;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
