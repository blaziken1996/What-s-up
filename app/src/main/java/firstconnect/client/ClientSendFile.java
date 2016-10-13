package firstconnect.client;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import firstconnect.common.Protocol;
import firstconnect.common.SendFile;
import firstconnect.connect.MainActivity;

import static firstconnect.connect.MainActivity.currentChatWindow;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientSendFile extends AsyncTask<Void, Void, Void> {
    private Socket client;
    private FileToSend file;
    private InetSocketAddress address;
    private String name;

    public ClientSendFile(String host, int port, FileToSend file, InetSocketAddress receiveSocket, InetSocketAddress address, String name) {
        try {
            client = new Socket(host, port);
            this.address = address;
            this.name = name;
            client.getOutputStream().write(Protocol.intToBytes(Protocol.SEND_FILE_SOCKET));
            client.getOutputStream().write(Protocol.inetAddressToBytes(receiveSocket));
            this.file = file;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            SendFile.send(file, client);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(currentChatWindow == null ? MainActivity.currentActivity : currentChatWindow,
                "Finished send file to " + name + address, Toast.LENGTH_SHORT).show();
    }
}
