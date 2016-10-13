package firstconnect.client;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import firstconnect.common.Protocol;

import static java.util.Arrays.asList;

/**
 * Created by trung on 10/9/16.
 */

public class LoginTask extends AsyncTask<String, Void, Client> {
    @Override
    protected Client doInBackground(String... params) {
        Client client = null;
        try {
            client = new Client(params[0], Integer.parseInt(params[1]), params[2]);
            client.write(asList(Protocol.intToBytes(Protocol.CHAT_SOCKET), Protocol.stringToBytes(client.getName())));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return client;
        }
    }
}
