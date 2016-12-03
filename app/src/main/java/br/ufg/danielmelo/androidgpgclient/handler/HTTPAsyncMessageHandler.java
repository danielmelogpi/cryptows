package br.ufg.danielmelo.androidgpgclient.handler;

import android.util.Log;

import com.fasterxml.jackson.jr.ob.JSON;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import br.ufg.danielmelo.androidgpgclient.entity.Message;
import br.ufg.danielmelo.androidgpgclient.entity.Response;
import br.ufg.danielmelo.androidgpgclient.openpgp.OpenPGPService;
import br.ufg.danielmelo.androidgpgclient.util.ProtocolSet;

/**
 * Default handler for http requests.
 * Routes the messages based on the requested operation.
 * For async operations it will provide a protocol UUID to
 * allow the client to retrieve the result later
 * If the result is available, this will return it
 */

public class HTTPAsyncMessageHandler {

    public static OpenPGPService pgpService;
    Logger log = Logger.getAnonymousLogger();

    public Response receiveMessage(HTTPResource operation, String message, String protocol) {
        log.info("New message received to " + operation + " :\n" + message);
        try {
            Message msg = null;
            if (message!=null) {
                msg = JSON.std.beanFrom(Message.class, message);
            }

            log.info("Serialized as :\n" + msg);
            return handle(operation, msg, protocol);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response();
        }
    }

    /**
     * Main handler
     * @param operation The requested operation, used to route the executtion, choose the callback
     *                  or return the oppropiated resource
     * @param message The unserialized message
     * @return
     */
    private Response handle(HTTPResource operation, Message message, String givenProtocol) {
        UUID protocol;

        if (HTTPResource.ENCRYPT_POST_NEW.equals(operation)) {
            Response res = new Response();
            protocol  = ProtocolSet.addPendingRequest();
            res.setProtocol(protocol.toString());
            pgpService.encryptAsync(message, new EncryptCallback(protocol));
            res.setCode(202);
            return res;
        }
        else if (HTTPResource.DECRYPT_POST_NEW.equals(operation)) {
            Response res = new Response();
            protocol  = ProtocolSet.addPendingRequest();
            res.setProtocol(protocol.toString());
            res.setCode(202);
            pgpService.decryptAsync(message, new DecryptCallback(protocol));
            return res;
        }
        else if (HTTPResource.ENCRYPT_GET_PROTOCOL.equals(operation) ||
                HTTPResource.DECRYPT_GET_PROTOCOL.equals(operation)) {

            try {
                protocol = UUID.fromString(givenProtocol);
            } catch (Exception e) {
                Response res = new Response();
                res.setCode(404);
                res.setStatusMessage("The given protocol was null or invalid. It must comply with UUID format");
                return res;
            }

            Response res = new Response();

            if (protocol == null) {
                res.setStatusMessage("The informed protocol was null. No resource found for that");
                res.setCode(404);
            }

            if (ProtocolSet.getPendingRequest(protocol, false) !=null) {
                res = ProtocolSet.getPendingRequest(protocol, true);
                res.setCode(200);
            } else {
                res.setStatusMessage("Your request could not be found. Try again later");
                res.setCode(404);
            }

            return res;
        }

        Response defaultRes = new Response();
        defaultRes.setCode(405);
        defaultRes.setStatusMessage("Could not understand the message");
        return defaultRes;
    }

}
