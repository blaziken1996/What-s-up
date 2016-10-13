package whatsup.client;


import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import whatsup.common.Protocol;
import whatsup.common.ReceiveFile;
import whatsup.connect.MainActivity;

import static whatsup.connect.MainActivity.currentChatWindow;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientReceiveFile extends AsyncTask<Void, Void, Void> {
    private InetSocketAddress socketAddress;
    private File saveFile;
    private Socket socketReceive;
    private String name;
    private InetSocketAddress address;

    public ClientReceiveFile(File filePath, String name, InetSocketAddress address, String host, int port) {
        try {
            this.saveFile = filePath;
            this.name = name;
            this.address = address;
            socketReceive = new Socket(host, port);
            socketAddress = (InetSocketAddress) socketReceive.getLocalSocketAddress();
            socketReceive.getOutputStream().write(Protocol.intToBytes(Protocol.RECEIVE_FILE_SOCKET));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            ReceiveFile.receive(saveFile, socketReceive);
            socketReceive.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(currentChatWindow == null ? MainActivity.currentActivity : currentChatWindow,
                "Received file " + saveFile.getName() + " from " + name + address, Toast.LENGTH_SHORT).show();
    }
}
