package whatsup.connect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by trung on 10/9/16.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private HashMap<InetSocketAddress, MessageListItem> hashMap;
    private List<MessageListItem> list;
    private MessageListFragment.MsgListListener mListener;

    public MessageListAdapter(MessageListFragment.MsgListListener mListener) {
        hashMap = new HashMap<>();
        list = new ArrayList<>();
        this.mListener = mListener;
    }

    public HashMap<InetSocketAddress, MessageListItem> getHashMap() {
        return hashMap;
    }

    public List<MessageListItem> getList() {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_msglist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.message = list.get(position);
        holder.message.holder = holder;
        List<ChatMessage> msglist = holder.message.getMsglist();
        ChatMessage displayMsg = msglist.get(msglist.size() - 1);
        holder.mNameView.setText(displayMsg.getName());
        holder.mMsgView.setText(displayMsg.getMsg());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.msgListInteraction(holder.message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hashMap == null ? 0 : hashMap.size();
    }

    public void addMsg(MessageListItem msg) {
        hashMap.put(msg.getAddress(), msg);
        list.add(msg);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mMsgView;
        public final TextView mNameView;
        public MessageListItem message;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMsgView = (TextView) view.findViewById(R.id.tvMsg);
            mNameView = (TextView) view.findViewById(R.id.tvName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMsgView.getText() + "'";
        }
    }
}
