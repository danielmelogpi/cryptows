package br.ufg.danielmelo.androidgpgclient;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Created by daniel on 26/11/16.
 */

public class WebsocketServer extends WebSocketServer {

    public WebsocketServer(String ip, int port) {
        super(new InetSocketAddress(ip,port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d("Websocket ", "Estabelecida conexao");
        conn.send("Conexao estabelecida");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d("Websocket ", "Nova mensagem: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }
}
