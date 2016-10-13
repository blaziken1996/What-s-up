package whatsup.connect;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by trung on 10/10/16.
 */

public class MessageListItem {
    public MessageListAdapter.ViewHolder holder;
    private List<ChatMessage> msglist;
    private InetSocketAddress address;
    private String name;

    public MessageListItem(InetSocketAddress address, String name) {
        msglist = Collections.synchronizedList(new ArrayList<ChatMessage>());
        this.address = address;
        this.name = name;
    }

    public List<ChatMessage> getMsglist() {
        return msglist;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}
