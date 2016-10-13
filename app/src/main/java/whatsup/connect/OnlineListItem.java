package whatsup.connect;

import java.net.InetSocketAddress;

/**
 * Created by trung on 10/9/16.
 */

public class OnlineListItem {
    private InetSocketAddress address;
    private String name;

    public OnlineListItem(InetSocketAddress address, String name) {
        this.address = address;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
