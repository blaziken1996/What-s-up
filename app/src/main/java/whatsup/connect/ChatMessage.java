package whatsup.connect;

/**
 * Created by trung on 10/12/16.
 */

public class ChatMessage {
    private final String msg;
    private final String name;
    private final boolean isMe;

    public ChatMessage(String msg, String name, boolean isMe) {
        this.msg = msg;
        this.name = name;
        this.isMe = isMe;
    }

    public String getName() {
        return name;
    }

    public boolean isMe() {
        return isMe;
    }

    public String getMsg() {
        return msg;
    }
}
