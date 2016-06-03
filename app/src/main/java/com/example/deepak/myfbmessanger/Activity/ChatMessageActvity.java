package com.example.deepak.myfbmessanger.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.deepak.myfbmessanger.Message.MessageCompare;
import com.example.deepak.myfbmessanger.Message.MessageIn;
import com.example.deepak.myfbmessanger.Message.MessageOut;
import com.example.deepak.myfbmessanger.MyService;
import com.example.deepak.myfbmessanger.R;
import com.example.deepak.myfbmessanger.db.DBHandler;
import com.example.deepak.myfbmessanger.db.Data;
import com.squareup.otto.Subscribe;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChatMessageActvity extends ActionBarActivity {

    public static boolean isRunning = false;
    private ArrayList<MessageCompare> messages;
    private Handler handler = new Handler();
    private ListView listView;
    private EditText editText;
    private String userName;
    private String userId;
    private CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActionBar actionBar = getSupportActionBar();
        /*actionBar.setLogo(R.drawable.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);*/
        actionBar.setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        userName = intent.getStringExtra("USER_NAME");
        userId = intent.getStringExtra("USER_ID");

        // final String to = intent.getStringExtra("TO");
        actionBar.setTitle(userName);
        setContentView(R.layout.activity_chat_message_actvity);
        listView = (ListView) findViewById(R.id.listchat);
        editText = (EditText) findViewById(R.id.text);
        Button button = (Button) findViewById(R.id.sendMessage);

        messages = new ArrayList<MessageCompare>();

        List<MessageOut> messageOuts = DBHandler.dbHandler.getMessageOuts(userName);
        final List<MessageIn> messageIns = DBHandler.dbHandler.getMessageIns(userId);

      /*  Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (messageIns != null) {
                    for (MessageIn messageIn : messageIns) {
                        messageIn.setFromUserNameIn(userName);
                        DBHandler.dbHandler.saveMessagesIn(messageIn);
                    }
                }
            }
        });*/

        if (messageIns != null) {
            messages.addAll(messageIns);
        }

        if (messageOuts != null) {
            messages.addAll(messageOuts);
        }

        adapter = new CustomAdapter(this, R.layout.custom_message_adapter_in, messages);
        adapter.sort(new Comparator<MessageCompare>() {
            @Override
            public int compare(MessageCompare lhs, MessageCompare rhs) {
                if(lhs == null || rhs == null){
                    return  0;
                }
                return lhs.compareTo(rhs);
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                editText.setText("");
                Data data = MyService.userIdDataHashMap.get(userId);
                boolean status = MyService.userIdDataHashMap.get(userId).isOnline();
                Log.i("FacebookUsersendMessage", "Sending text " + userName + " to " + data.getId());
                Message msg = new Message(userId, Message.Type.chat);
                XMPPConnection connection = MyService.getConnection();
                msg.setBody(message);


                if (connection != null && connection.isConnected()) {
                    connection.sendPacket(msg);
                    DBHandler.dbHandler.saveMessagesOut(new MessageOut(System.currentTimeMillis(),
                            message, userName, userId, true));
                   /* dbHelper = new DBHelper(getApplicationContext());
                    dbHelper.insertMessages(text);*/
                } else {
                    DBHandler.dbHandler.saveMessagesOut(new MessageOut(System.currentTimeMillis(), message, userName, userId, false));
                }
                adapter.add(new MessageOut(System.currentTimeMillis(),
                        message, userName, userId, true));
                adapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    protected void onResume() {
        MyService.BUS.register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MyService.BUS.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onMessageIn(MessageIn messageIn) {
        if (userId.equals(messageIn.getFromIdIn())) {
            adapter.add(messageIn);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_message_actvity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class CustomAdapter extends ArrayAdapter<MessageCompare> {
        LayoutInflater inflater;


        List lists;

        @SuppressWarnings("unchecked")
        public CustomAdapter(Context context, int resource, List lists) {
            super(context, resource, lists);

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // set the Teacher Details objects to populate the Spinner
            this.lists = lists;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            MessageCompare messageCompare = getItem(position);

            if (messageCompare instanceof MessageIn) {
                MessageIn messageIn = (MessageIn) messageCompare;
                View viewin = inflater.inflate(R.layout.custom_message_adapter_in, parent, false);
                TextView timein = (TextView) viewin.findViewById(R.id.messageincomingdate);
                TextView username = (TextView) viewin.findViewById(R.id.messageincomingusername);
                TextView messagetext = (TextView) viewin.findViewById(R.id.messageincoming_text);
                String date = String.valueOf(messageIn.getTimestamp());
                String usernamein = messageIn.getFromUserNameIn();
                String text = messageIn.getMessageIn();

                timein.setText(date);
                username.setText(usernamein);
                messagetext.setText(text);
                return viewin;
            } else if (messageCompare instanceof MessageOut) {
                MessageOut messageOut = (MessageOut) messageCompare;
                View viewout = inflater.inflate(R.layout.custom_message_adapter_out, parent, false);
                TextView timein = (TextView) viewout.findViewById(R.id.messageoutgoingdate);
                TextView username = (TextView) viewout.findViewById(R.id.messageoutgoinggusername);
                TextView messagetext = (TextView) viewout.findViewById(R.id.messageoutgoing_text);
                String time = String.valueOf(messageOut.getTimestamp());
                String usernameout = messageOut.getToUserName();
                String text = messageOut.getMessage();
                timein.setText(time);
                username.setText(usernameout);
                messagetext.setText(text);
                return viewout;
            }

            return convertView;
        }
    }

}
