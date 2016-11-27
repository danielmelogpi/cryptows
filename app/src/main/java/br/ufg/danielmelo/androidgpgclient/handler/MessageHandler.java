package br.ufg.danielmelo.androidgpgclient.handler;

import org.java_websocket.WebSocket;

import br.ufg.danielmelo.androidgpgclient.entity.Message;
import br.ufg.danielmelo.androidgpgclient.entity.Response;
import br.ufg.danielmelo.androidgpgclient.openpgp.OpenPGPService;

/**
 * Created by daniel on 26/11/16.
 */

public class MessageHandler {

    private static OpenPGPService pgpService;

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
            EncryptCallback enCall = new EncryptCallback(wsocket);
            pgpService.encryptAsync(message, enCall);
        }
        else if (message.getOperation().equals("decrypt")) {
            DecryptCallback decCall = new DecryptCallback(wsocket);
            pgpService.decryptAsync(message, decCall);
        }
        else if (message.getOperation().equals("get-ids")) {

        }
    }

    public static OpenPGPService getPgpService() {
        return pgpService;
    }

    public static void setPgpService(OpenPGPService pgpService) {
        MessageHandler.pgpService = pgpService;
    }

}
