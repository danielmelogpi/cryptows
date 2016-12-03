package br.ufg.danielmelo.androidgpgclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufg.danielmelo.androidgpgclient.entity.Response;
import br.ufg.danielmelo.androidgpgclient.handler.HTTPAsyncMessageHandler;
import br.ufg.danielmelo.androidgpgclient.handler.HTTPResource;
import br.ufg.danielmelo.androidgpgclient.openpgp.OpenPGPService;
import fi.iki.elonen.NanoHTTPD;

import static android.media.CamcorderProfile.get;


public class HTTPThread extends NanoHTTPD {

    private static HTTPThread thread;
    private OpenPGPService openPgpService;

    HTTPAsyncMessageHandler handler = new HTTPAsyncMessageHandler();

    public HTTPThread(int port, OpenPGPService service) throws IOException{
        super(port);
        this.openPgpService = service;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public static void iniciar(OpenPGPService service) {
        try {
            thread = new HTTPThread(8881, service);
            HTTPAsyncMessageHandler.pgpService = service;
            System.out.println("Servidor iniciado");
        } catch (IOException e) {
            System.err.println("Couldn't start server:\n" + e);
        }

    }

    @Override
    public NanoHTTPD.Response serve(IHTTPSession session)  {
        Map<String, String> files = new HashMap<>();
        String url = session.getUri();
        Method method = session.getMethod();
        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException e) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
            } catch (ResponseException e) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
            }
        }

        String autorizationHeader = session.getHeaders().get("authorization");
        if (!Auth.getSenhaSessao().equals(autorizationHeader)) {
            return newFixedLengthResponse(Response.Status.UNAUTHORIZED, MIME_PLAINTEXT, "Not authorized. The Authorization header must be present and updated with the mobile device");
        }

        // get the POST body
        String postBody = files.get("postData");
        String protocol = "";
        HTTPResource operation = null;
        if (url.endsWith("encrypt-content") && method == Method.POST) {
            operation = HTTPResource.ENCRYPT_POST_NEW;
        } else if (url.contains("encrypt-content/protocol") && method == Method.GET) {
            operation = HTTPResource.ENCRYPT_GET_PROTOCOL;
            protocol = getProtocolFromString(url);
        } else if (url.endsWith("/decrypt-content") && method == Method.POST) {
            operation = HTTPResource.DECRYPT_POST_NEW;
        } else if (url.contains("decrypt-content/protocol") && method == Method.GET) {
            operation = HTTPResource.DECRYPT_GET_PROTOCOL;
            protocol = getProtocolFromString(url);
        }

        if (operation == null) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT,  "Requested operation is unknown");
        }
        br.ufg.danielmelo.androidgpgclient.entity.Response res = handler.receiveMessage(operation, postBody, protocol);

        return newFixedLengthResponse(Response.Status.lookup(res.getCode()), "application/json", res.toJson());

    }

    private String getProtocolFromString(String urlPath) {
        if (urlPath == null) {
            return null;
        }
        String search = "protocol/";
        int indexOf = urlPath.indexOf(search);
        return urlPath.substring(indexOf + search.length());
    }

}
