package br.ufg.danielmelo.androidgpgclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HTTPThread extends NanoHTTPD {

    private static HTTPThread thread;

    public HTTPThread(int port) throws IOException{
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public static void iniciar() {
        try {
            thread = new HTTPThread(8085);
            System.out.println("Servidor iniciado");
        } catch (IOException e) {
            System.err.println("Couldn't start server:\n" + e);
        }

    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");
    }
}
