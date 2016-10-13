package firstconnect.connect;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Arrays;

import firstconnect.client.Client;
import firstconnect.client.FileToSend;
import firstconnect.common.Protocol;

public class ChatWindow extends AppCompatActivity {
    public static final String ADDRESS = "firstconnect.connect.ChatWindowAddress";
    public static final String NAME = "firstconnect.connect.ChatWindowName";
    public static final int FILE_SELECT_CODE = 10;
    private InetSocketAddress address;
    private String name;
    private ChatListAdapter adapter;
    private EditText txtMsg;
    private RecyclerView msgList;
    private MessageListAdapter msglistadapter;

    public InetSocketAddress getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    @Override
    public void onBackPressed() {
        MainActivity.currentChatWindow = null;
        MainActivity.currentListItem = null;
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        address = (InetSocketAddress) getIntent().getSerializableExtra(ADDRESS);
        name = getIntent().getStringExtra(NAME);
        setTitle(name + address.toString());
        MainActivity.currentChatWindow = this;
        msgList = (RecyclerView) findViewById(R.id.msglist);
        adapter = new ChatListAdapter(msgList);
        if (MainActivity.currentListItem != null)
            adapter.setmValues(MainActivity.currentListItem.getMsglist());
        msgList.setAdapter(adapter);
        txtMsg = (EditText) findViewById(R.id.msgEdt);
        msglistadapter = ((MessageListFragment) MainActivity.currentActivity.getFragments()[1]).getAdapter();
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = txtMsg.getText().toString();
                txtMsg.setText(null);
                try {
                    Client.mainClient.write(Arrays.asList(Protocol.intToBytes(Protocol.SEND_MSG_CODE), Protocol.inetAddressToBytes(address), Protocol.stringToBytes(message)));
                    MessageListItem item = msglistadapter.getHashMap().get(address);
                    if (item != null) {
                        adapter.addMsg(new ChatMessage(message, "Me", true));
                        msglistadapter.notifyDataSetChanged();
                    } else {
                        item = new MessageListItem(address, name);
                        adapter.setmValues(item.getMsglist());
                        adapter.addMsg(new ChatMessage(message, "Me", true));
                        msglistadapter.addMsg(item);
                    }
                    //msgList.smoothScrollToPosition(getAdapter().getItemCount() - 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.btnSendFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");      //all files
        //intent.setType("text/xml");   //XML file only
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to send"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri returnUri = data.getData();
                        Cursor returnCursor =
                                getContentResolver().query(returnUri, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        Client.mainClient.getReceiverFileMap().put(address, new FileToSend(returnCursor.getLong(sizeIndex), getContentResolver().openInputStream(returnUri)));
                        Client.mainClient.write(Arrays.asList(Protocol.intToBytes(Protocol.FILE_REQ_CODE), Protocol.inetAddressToBytes(address),
                                Protocol.stringToBytes(returnCursor.getString(nameIndex))));
                        returnCursor.close();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public ChatListAdapter getAdapter() {
        return adapter;
    }
}
