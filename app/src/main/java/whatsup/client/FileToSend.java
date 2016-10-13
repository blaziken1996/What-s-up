package whatsup.client;

import java.io.InputStream;

/**
 * Created by trung on 10/10/16.
 */

public class FileToSend {
    public final long size;
    public final InputStream fileInputStream;

    public FileToSend(long size, InputStream fileInputStream) {
        this.size = size;
        this.fileInputStream = fileInputStream;
    }
}
