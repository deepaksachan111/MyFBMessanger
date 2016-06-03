package com.example.deepak.myfbmessanger;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.deepak.myfbmessanger.Activity.ChatMessageActvity;
import com.example.deepak.myfbmessanger.Activity.MainActivity;
import com.example.deepak.myfbmessanger.Message.MessageIn;
import com.example.deepak.myfbmessanger.Message.MessageOut;
import com.example.deepak.myfbmessanger.connection.Connection;
import com.example.deepak.myfbmessanger.db.DBHandler;
import com.example.deepak.myfbmessanger.db.Data;
import com.example.deepak.myfbmessanger.db.UserProfile;
import com.squareup.otto.Bus;

import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    NotificationManager mNotificationManager;
    private int notificationID = 100;
    private int numMessages = 0;

    private ExecutorService executorService;
    public static boolean isRunning = false;
    private Handler handler = new Handler();
    private static XMPPConnection connection;
    private String messageString;

    private ConnectionConfiguration connConfig;
    public static final Bus BUS = new Bus();

    private ScheduledExecutorService scheduledExecutorService;

    public static  ConcurrentHashMap<String, Data> userIdDataHashMap = new ConcurrentHashMap<String, Data>();

    public static final String TAG = MyService.class.getSimpleName();

    public static XMPPConnection getConnection() {
        return connection;
    }

    private boolean status = false;

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DBHandler.start(getApplicationContext());

        connConfig = new ConnectionConfiguration(
                Connection.HOST, Connection.PORT, Connection.SERVICE);
        connConfig.setReconnectionAllowed(true);
        BUS.register(this);
        executorService = Executors.newFixedThreadPool(3);
        isRunning = true;
        Log.e(TAG, "service started -------------------");
        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        executorService1.execute(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        });
        return START_STICKY;


    }

    public void connect() {
        try {
            Log.e(TAG, "connecting ------------------");

            connection = new XMPPConnection(connConfig);

            connection.connect();
            UserProfile userProfile = DBHandler.dbHandler.getuserProfileList();
            connection.login(userProfile.getUserEmailid(), userProfile.getUserPassword());

            Log.e(TAG, "Connected--------------");

            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            //  setListener();

            Roster roster = connection.getRoster();
            roster.addRosterListener(new RosterListener() {
                @Override
                public void entriesAdded(Collection<String> strings) {
                    String data = strings.toString();
                    Log.i(TAG, strings.toString());
                }

                @Override
                public void entriesUpdated(Collection<String> strings) {
                    String data = strings.toString();
                    Log.i(TAG, strings.toString());
                }

                @Override
                public void entriesDeleted(Collection<String> strings) {
                    String data = strings.toString();
                    Log.i(TAG, strings.toString());
                }

                @Override
                public void presenceChanged(final Presence presence) {
                    // final String data = presence.toString();
                    //  Log.i(TAG, presence.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            postEvent(presence);

                        }
                    });
                }
            });

            Collection<RosterEntry> entries = roster.getEntries();
            for (RosterEntry entry : entries) {

                Presence entryPresence = roster.getPresence(entry.getUser());
                Presence.Type userType = entryPresence.getType();
                Presence.Mode mode = entryPresence.getMode();
                // String status = entryPresence.getStatus();
                presence = roster.getPresence(entry.getUser());
                String name = entry.getName().toString();

                Log.e("USER", entry.getName().toString());
                Presence entryPresence2 = roster.getPresence(entry.getUser());
                Presence.Type type = entryPresence.getType();


                if (type == Presence.Type.available) {
                    status = true;
                } else {
                    status = false;
                }

                if (status) {
                    Log.i("", "");
                }
               /* ProviderManager.getInstance().addIQProvider("vCard","vcard-temp",new VCardProvider());
                VCard card = new VCard();
                card.load(connection,entry.getUser());

                byte[] imgs = card.getAvatar();
*/
                userIdDataHashMap.put(entry.getUser(), new Data(name, entry.getUser(), status));
            }

            synchronized (userIdDataHashMap) {
                Log.e(TAG, "hash map notified --------------------");
                userIdDataHashMap.notifyAll();
            }
            setListener();
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("authentication failed")) {
         //       Thread.currentThread().interrupt();
                synchronized (userIdDataHashMap) {
                    Log.e(TAG, "hash map notified --------------------");
                    userIdDataHashMap.notifyAll();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        postEvent(new AuthFailed());


                    }
                });

            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e.printStackTrace();
                }
                connect();
            }
        }
    }
    private void postEvent(Object o) {
        BUS.post(o);
    }

    private void setListener() {
        if (connection != null) {
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                     Message message = (Message) packet;
                    if (message.getBody() != null) {
                        final String id = message.getFrom();
                        final String name = "From User";
                        messageString = message.getBody();

                        final MessageIn messageIn = new MessageIn(System.currentTimeMillis(), messageString, name, id);
                        DBHandler.dbHandler.saveMessagesIn(messageIn);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                postEvent(messageIn);
                                displayNotification(id);

                            }
                        });
                    }
                }
            }, filter);
            connection.addConnectionListener(new AbstractConnectionListener() {
                @Override
                public void connectionClosed() {
                    super.connectionClosed();
                }

                @Override
                public void connectionClosedOnError(Exception e) {
                    super.connectionClosedOnError(e);
                    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                    scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                connection.connect();
                                scheduledExecutorService.shutdownNow();
                                try {
                                    scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS);
                                } catch (Exception e1) {

                                }
                            } catch (Exception e1) {

                            }
                        }
                    }, 1, 2, TimeUnit.SECONDS);

                }

                @Override
                public void reconnectingIn(int i) {
                    super.reconnectingIn(i);

                }

                @Override
                public void reconnectionFailed(Exception e) {
                    super.reconnectionFailed(e);
                    connect();
                }

                @Override
                public void reconnectionSuccessful() {
                    super.reconnectionSuccessful();
                    ExecutorService executorService1 = Executors.newSingleThreadExecutor();
                    executorService1.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<MessageOut> messageOuts = DBHandler.dbHandler.getMessageOutsNotSent();
                            if (messageOuts != null) {
                                for (MessageOut messageOut : messageOuts) {
                                    Message msg = new Message(messageOut.getToId(), Message.Type.chat);
                                    XMPPConnection connection = MyService.getConnection();
                                    msg.setBody(messageOut.getMessage());
                                    if (connection != null && connection.isConnected()) {
                                        connection.sendPacket(msg);
                                        messageOut.setIsSent(true);
                                        DBHandler.dbHandler.saveMessagesOut(messageOut);
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }
    }


    private boolean skip = false;

    @Override
    public void onDestroy() {
        BUS.unregister(this);
        isRunning = false;
        Log.e(TAG, "Service destroyed ----------------------");
        super.onDestroy();
        connection.disconnect();
    }

    protected void displayNotification(String id) {
        Log.v("start", "notification");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle(id);
        mBuilder.setContentText(messageString);
        mBuilder.setTicker("New Message Alert!");

        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setNumber(++numMessages);

        Intent resultIntent = new Intent(MyService.this, ChatMessageActvity.class);
        resultIntent.putExtra("USER_NAME",userIdDataHashMap.get(id).getUsername());
        resultIntent.putExtra("USER_ID",id);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

}
