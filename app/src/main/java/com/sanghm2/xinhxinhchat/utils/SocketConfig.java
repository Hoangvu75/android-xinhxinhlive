package com.sanghm2.xinhxinhchat.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;


import com.sanghm2.xinhxinhchat.utils.Constants;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import io.socket.engineio.client.transports.WebSocket;

public class SocketConfig {
    private Boolean isConnected = false;
    private Socket mSocket;
    private final String TAG = "Sang";
    private final EventSocket eventSocket;
    private final IO.Options options = new IO.Options();
    private String token = "";
    private final List<String> lstKey;
    private final Handler handler;
    private final Context context;

    public Socket getSocket() {
        return mSocket;
    }

    public SocketConfig(@NotNull EventSocket eventSocket, @NotNull  Context context,@NotNull  List<String> lstKey) {
        this.context = context;
        this.eventSocket = eventSocket;
        handler = new Handler(context.getMainLooper());
        this.lstKey = lstKey;
        token = XinhXinhPref.getStringPreference(context, Constants.TOKEN);
        token = token.replace("Bearer ", "");
        initSocket();
    }

    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }


    public void initSocket() {
        try {
            options.transports = new String[]{WebSocket.NAME};
            options.forceNew = true;
            options.reconnection = false;
            options.timeout = 10000;
            options.query = "authenticate=" + token;
            mSocket = IO.socket(Constants.url_socket, options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.io().on(Manager.EVENT_TRANSPORT, onTransport);
        mSocket.connect();
    }

    private final Emitter.Listener onTransport = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Transport transport = (Transport) args[0];
            transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    @SuppressWarnings("unchecked")
                    Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                    String bearer = XinhXinhPref.getStringPreference(context, Constants.TOKEN);
                    headers.put("authenticate", Collections.singletonList(bearer));
                }
            }).on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.on(Socket.EVENT_CONNECT, onConnect);
                    mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                    mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                    mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                    for (String key : lstKey) {
                        mSocket.on(key, new OnReceiver(key));
                    }
                }
            });
        }
    };

    public class OnReceiver implements Emitter.Listener {
        String key;

        public OnReceiver(String key) {
            this.key = key;
        }

        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();
                    eventSocket.onReceiver(key, data);
                }
            });
        }
    }


    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.emit("authenticate", token);
                    if (!isConnected) {
                        eventSocket.onConnect();
                        isConnected = true;
                    }
                }
            });
        }
    };


    private final Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "disconnected");
                    isConnected = false;
                    eventSocket.onDisconnect();
                }
            });
        }
    };

    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();
                    Log.e(TAG, "Error connecting: " + data);
                    eventSocket.onConnectError();
                }
            });
        }
    };

    public void onDisconnect() {
        if (mSocket != null && mSocket.connected()) {
            Log.e(TAG, "onDisconnect");
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            if (lstKey != null && lstKey.size() > 0) {
                for (String key : lstKey) {
                    OnReceiver onReceiver = new OnReceiver(key);
                    mSocket.off(key, onReceiver);
                }
            }
        }
    }

    public void requestCampaign(String campaignId){
        mSocket.emit(Constants.REQUEST_CAMPAIGN, campaignId);
    }


    public interface EventSocket {
        void onConnect();

        void onDisconnect();

        void onConnectError();

        void onReceiver(String key, String data);
    }
}
