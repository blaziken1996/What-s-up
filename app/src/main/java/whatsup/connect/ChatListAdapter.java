package whatsup.connect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trung on 10/9/16.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<ChatMessage> mValues;
    private RecyclerView view;

    public ChatListAdapter(RecyclerView view) {
        mValues = new ArrayList<>(0);
        this.view = view;
    }

    public void setmValues(List<ChatMessage> mValues) {
        this.mValues = mValues;
    }

    @Override
    public int getItemViewType(int position) {
        if (mValues.get(position).isMe()) return 0;
        return 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType == 1 ? R.layout.fragment_item_chatlist_you : R.layout.fragment_item_chatlist_me, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.message = mValues.get(position);
        holder.mMsgView.setText(holder.message.getMsg());
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    synchronized public void addMsg(ChatMessage msg) {
        mValues.add(msg);
        notifyDataSetChanged();
        view.smoothScrollToPosition(mValues.size() - 1);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mMsgView;
        public ChatMessage message;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMsgView = (TextView) view.findViewById(R.id.msgTv);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMsgView.getText() + "'";
        }
    }
}
