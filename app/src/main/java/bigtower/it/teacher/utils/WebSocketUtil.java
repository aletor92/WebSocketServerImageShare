package bigtower.it.teacher.utils;

import android.util.Log;

import com.koushikdutta.async.AsyncNetworkSocket;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alex on 30/01/2018.
 */

public class WebSocketUtil {

    private WebSocketUtil(){

    }

    private static WebSocketUtil instance = null;

    public static WebSocketUtil getInstance(){
        if(instance == null){
            synchronized (WebSocketUtil.class) {
                if(instance == null){
                    instance = new WebSocketUtil();
                }
            }
        }
        return instance;
    }

    private ArrayList<HashMap<String, WebSocket>> clients;

    public ArrayList<HashMap<String, WebSocket>> getClients() {
        return clients;
    }

    public void initWss() {
        final AsyncHttpServer server = new AsyncHttpServer();
        clients = new ArrayList<HashMap<String, WebSocket>>();
        server.websocket("/", new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
                AsyncNetworkSocket socketConverted = Util.getWrappedSocket(webSocket.getSocket(), AsyncNetworkSocket.class);
                String address = socketConverted.getRemoteAddress().getAddress().getHostAddress();
                final HashMap<String, WebSocket> map = new HashMap<>();
                map.put(socketConverted.getRemoteAddress().getAddress().getHostAddress(), webSocket);
                clients.add(map);
                Log.d("Clients IP", address);
                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        try {
                            if (ex != null)
                                Log.e("WebSocket", "Error");
                        } finally {
                            clients.remove(map);
                        }
                    }
                });

            }
        });
        server.listen(5000);
    }

    public void broadcastMessage(String string){
        for (HashMap<String, WebSocket> map: clients) {
            for ( String key : map.keySet()) {
                map.get(key).send(string);
                Log.d("Message", key);

            }
        }
    }


}
