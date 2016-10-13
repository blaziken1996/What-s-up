package whatsup.client;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import whatsup.common.Protocol;
import whatsup.connect.ChatMessage;
import whatsup.connect.ChatWindow;
import whatsup.connect.MainActivity;
import whatsup.connect.MessageListAdapter;
import whatsup.connect.MessageListFragment;
import whatsup.connect.MessageListItem;
import whatsup.connect.OnlineListFragment;
import whatsup.connect.OnlineListItem;

import static java.util.Arrays.asList;
import static whatsup.connect.MainActivity.currentActivity;
import static whatsup.connect.MainActivity.currentChatWindow;

/**
 * Created by trung on 21/09/2016.
 */
public class ClientReadSocketInput extends Thread {
    private static final String LOG_TAG = "clientreadsocketinput";
    private InputStream in;
    private Client client;
    //private ConcurrentHashMap<InetSocketAddress, ChatWindowController> currentChatWindow;
    private MainActivity activity;
    private Handler handler;

    public ClientReadSocketInput(Client client, MainActivity activity) {
        this.in = client.getInputStream();
        this.client = client;
        this.activity = activity;
        handler = new Handler();
    }

    private void responseFileRequest(final String filename, final String name, final InetSocketAddress address) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.currentChatWindow == null ? MainActivity.currentActivity : MainActivity.currentChatWindow).create();
                        alertDialog.setTitle("Send File Request");
                        alertDialog.setMessage("Do you want to receive file " + filename + " from " + name + address + " ?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                                try {
                                    if (currentActivity.isExternalStorageWritable()) {
                                        MainActivity.currentActivity.askWriteExternalPermission();
                                        final File saveFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
                                        /*if (!saveFile.mkdirs()) {
                                            Log.e(LOG_TAG, "Directory not created "+saveFile.getAbsolutePath());
                                            return;
                                        }*/
                                        Log.e(LOG_TAG, saveFile.getAbsolutePath());
                                        if (saveFile != null) {
                                            Log.e(LOG_TAG, "start receive server");
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ClientReceiveFile server = new ClientReceiveFile(saveFile, name, address, client.getHost(), client.getPort());
                                                    server.execute();
                                                    try {
                                                        client.write(asList(Protocol.intToBytes(Protocol.ACCEPT_FILE),
                                                                Protocol.inetAddressToBytes(address),
                                                                Protocol.stringToBytes(client.getName()),
                                                                Protocol.inetAddressToBytes(client.getAddress()),
                                                                Protocol.inetAddressToBytes(server.getSocketAddress())));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
                                        } else
                                            client.write(asList(Protocol.intToBytes(Protocol.DENY_FILE),
                                                    Protocol.inetAddressToBytes(address)));
                                    } else {
                                        client.write(asList(Protocol.intToBytes(Protocol.DENY_FILE),
                                                Protocol.inetAddressToBytes(address)));
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                                try {
                                    client.write(asList(Protocol.intToBytes(Protocol.DENY_FILE),
                                            Protocol.inetAddressToBytes(address)));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        alertDialog.show();
                    }
                });
            }
        }).start();

    }

    @Override
    public void run() {
        boolean isRunning = true;
        try {
            while (isRunning) {
                switch (Protocol.readInt(in)) {
                    case Protocol.ONLINE_LIST_CODE:
                        int num = Protocol.readInt(in);
                        final List list = new ArrayList<OnlineListItem>(num);
                        for (int i = 0; i < num; i++) {
                            list.add(new OnlineListItem(Protocol.readInetAddress(in), Protocol.readString(in)));
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((OnlineListFragment) activity.getFragments()[0]).getAdapter().setmValues(list);
                                    }
                                });
                            }
                        }).start();
                        break;
                    case Protocol.SEND_MSG_CODE:
                        final String name = Protocol.readString(in);
                        final InetSocketAddress address = Protocol.readInetAddress(in);
                        final String message = Protocol.readString(in);
                        final ChatWindow window = currentChatWindow;
                        if (window != null) {
                            if (window.getAddress().equals(address)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                window.getAdapter().addMsg(new ChatMessage(message, name + address, false));
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }
                        MessageListFragment fragment = (MessageListFragment) activity.getFragments()[1];
                        final MessageListAdapter adapter = fragment.getAdapter();
                        final MessageListItem item = adapter.getHashMap().get(address);
                        if (item != null) {
                            if (window == null)
                                item.getMsglist().add(new ChatMessage(message, name + address, false));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }).start();

                        } else {
                            final MessageListItem item2 = new MessageListItem(address, name);
                            item2.getMsglist().add(new ChatMessage(message, name + address, false));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.addMsg(item2);
                                        }
                                    });
                                }
                            }).start();
                        }
                        break;
                    case Protocol.FILE_REQ_CODE:
                        String name0 = Protocol.readString(in);
                        InetSocketAddress address0 = Protocol.readInetAddress(in);
                        String filename = Protocol.readString(in);
                        responseFileRequest(filename, name0, address0);
                        break;
                    case Protocol.ACCEPT_FILE:
                        final String name1 = Protocol.readString(in);
                        final InetSocketAddress address1 = Protocol.readInetAddress(in);
                        InetSocketAddress host = Protocol.readInetAddress(in);
                        System.out.println("From readsocket: " + client.getReceiverFileMap().get(address1) + " " + address1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Context context = currentChatWindow == null ? MainActivity.currentActivity : currentChatWindow;
                                        Toast.makeText(context, name1 + address1 + " has accepted your request. Sending file...", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).start();
                        new ClientSendFile(client.getHost(), client.getPort(), client.getReceiverFileMap().get(address1), host, address1, name1).execute();
                        client.getReceiverFileMap().remove(address1);
                        break;
                    case Protocol.DENY_FILE:
                        final String name2 = Protocol.readString(in);
                        final InetSocketAddress address2 = Protocol.readInetAddress(in);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Context context = currentChatWindow == null ? MainActivity.currentActivity : currentChatWindow;
                                        Toast.makeText(context, name2 + address2 + " has denied your request.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).start();
                        client.getReceiverFileMap().remove(address2);
                        break;
                    case Protocol.NOT_AVAIL:
                        InetSocketAddress addr = Protocol.readInetAddress(in);
                        if (currentChatWindow != null)
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            currentChatWindow.getAdapter().addMsg(new ChatMessage("This person is offline.", "From server", false));
                                        }
                                    });
                                }
                            }).start();
                        //client.getReceiverFileMap().remove(address);
                        break;
                    case Protocol.END_CONNECT_CODE:
                        isRunning = false;
                        break;
                }
            }
            client.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
