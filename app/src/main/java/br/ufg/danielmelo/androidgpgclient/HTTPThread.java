package br.ufg.danielmelo.androidgpgclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import br.ufg.danielmelo.androidgpgclient.openpgp.OpenPGPService;
import fi.iki.elonen.NanoHTTPD;


public class HTTPThread extends NanoHTTPD {

    private static HTTPThread thread;
    private OpenPGPService openPgpService;

    public HTTPThread(int port, OpenPGPService service) throws IOException{
        super(port);
        this.openPgpService = service;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public static void iniciar(OpenPGPService service) {
        try {
            thread = new HTTPThread(30001, service);
            System.out.println("Servidor iniciado");
        } catch (IOException e) {
            System.err.println("Couldn't start server:\n" + e);
        }

    }

    @Override
    public Response serve(IHTTPSession session)  {

        String testEncryption = null;
        try {
            testEncryption = openPgpService.encrypt("danielmelogpi1@gmail.com", "uma mensagem encriptada");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse(testEncryption);
    }
}
