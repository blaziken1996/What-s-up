package firstconnect.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import firstconnect.client.FileToSend;

/**
 * Created by trung on 15/09/2016.
 */
public class SendFile {

    public static void send(FileToSend file, Socket socket) throws IOException {
        InputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            OutputStream outputStream = socket.getOutputStream();
            fileInputStream = file.fileInputStream;
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            int count;
            byte[] buffer = new byte[Protocol.BUFFER_SIZE];
            while ((count = bufferedInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) fileInputStream.close();
            if (bufferedInputStream != null) bufferedInputStream.close();
        }
    }
}
