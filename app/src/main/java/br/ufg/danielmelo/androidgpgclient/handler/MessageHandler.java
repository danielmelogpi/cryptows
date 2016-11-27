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
            pgpService.encryptAsync(message, new EncryptCallback(wsocket));
        }
        else if (message.getOperation().equals("decrypt")) {
            pgpService.decryptAsync(message, new DecryptCallback(wsocket));
        }
        else if (message.getOperation().equals("get-ids")) {
            pgpService.retrieveIdsAsync(message, new IdRetrieverCallback(wsocket));
        }
    }

    public static OpenPGPService getPgpService() {
        return pgpService;
    }

    public static void setPgpService(OpenPGPService pgpService) {
        MessageHandler.pgpService = pgpService;
    }

}
