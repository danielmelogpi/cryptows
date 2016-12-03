//package br.ufg.danielmelo.androidgpgclient;
////
////import android.util.Log;
////
////import com.fasterxml.jackson.jr.ob.JSON;
////
////import org.java_websocket.WebSocket;
////import org.java_websocket.handshake.ClientHandshake;
////import org.java_websocket.server.WebSocketServer;
////
////import java.io.IOException;
////import java.net.InetSocketAddress;
////
////import br.ufg.danielmelo.androidgpgclient.entity.Message;
////import br.ufg.danielmelo.androidgpgclient.handler.MessageHandler;
//
///**
// * Created by daniel on 26/11/16.
// */
//
//public class WebsocketServer extends WebSocketServer {
//
////    public WebsocketServer(String ip, int port) {
////        super(new InetSocketAddress(ip,port));
////    }
////
////    @Override
////    public void onOpen(WebSocket conn, ClientHandshake handshake) {
////        Log.d("Websocket ", "Estabelecida conexao");
////        conn.send("Conexao estabelecida");
////    }
////
////    @Override
////    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
////
////    }
////
////    @Override
////    public void onMessage(WebSocket conn, String message) {
////        Log.d("Websocket ", "Nova mensagem: " + message);
////        try {
////            Message msg = JSON.std.beanFrom(Message.class, message);
////            Log.d("Websocket", "Mensagem recebida" + msg);
////            new MessageHandler(msg, conn);
////        } catch (IOException e) {
////            e.printStackTrace();
////            conn.send("Erro ao realizar operacao");
////        }
////    }
////
////    @Override
////    public void onError(WebSocket conn, Exception ex) {
////
////    }
//}
