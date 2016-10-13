package whatsup.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by trung on 15/09/2016.
 */
public class ReceiveFile {
    public static void receive(File file, Socket socket) throws IOException {
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            InputStream inputStream = socket.getInputStream();
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            byte[] buffer = new byte[Protocol.BUFFER_SIZE];
            int count;
            while ((count = inputStream.read(buffer)) > 0) {
                bufferedOutputStream.write(buffer, 0, count);
            }
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) fileOutputStream.close();
            if (bufferedOutputStream != null) bufferedOutputStream.close();
        }
    }
}
