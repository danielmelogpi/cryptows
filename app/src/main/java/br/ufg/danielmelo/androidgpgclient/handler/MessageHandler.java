package br.ufg.danielmelo.androidgpgclient.handler;

import org.java_websocket.WebSocket;

import br.ufg.danielmelo.androidgpgclient.entity.Message;
import br.ufg.danielmelo.androidgpgclient.entity.Response;

/**
 * Created by daniel on 26/11/16.
 */

public class MessageHandler {

    private final WebSocket wsocket;
    private Message message;

    public MessageHandler(Message message, WebSocket wsocket) {
        this.message = message;
        this.wsocket = wsocket;
        
        handle();
    }

    private void handle() {
        Response res = new Response();
        res.setContent("Conteudo da resposta");
        if (message.getOperation().equals("encrypt")) {
            wsocket.send(res.toJson());
        }
        if (message.getOperation().equals("decrypt")) {
            
        }
        if (message.getOperation().equals("get-ids")) {

        }
    }
}
