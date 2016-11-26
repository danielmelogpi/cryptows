package br.ufg.danielmelo.androidgpgclient;

import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

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
        String operation = session.getHeaders().get("x-operation");
        String url = session.getUri();
        Log.v("http-server", "Respondendo requisicao para " + url);
        if (url.contains("favicon")) return null;

        switch (url) {
            case "/api/gpg/encrypt-and-sign":
                return newFixedLengthResponse(NanoHTTPD.Response.Status.OK
                        , "application/json", "you have it encripted");
            case "/api/gpg/sign":
                return newFixedLengthResponse(NanoHTTPD.Response.Status.OK
                        , "application/json", "you have it signed");
            case "/api/gpg/decrypt":
                return newFixedLengthResponse(NanoHTTPD.Response.Status.OK
                        , "application/json", "you have it decripted");
            case "/api/gpg/get-ids":
                return newFixedLengthResponse(NanoHTTPD.Response.Status.OK
                        , "application/json", "here are your ids");
        }

//        operation = operation == null? "":operation;

        switch (operation) {
            case "encrypt-and-sign": {
                String testEncryption = null;
                String destination = session.getHeaders().get("x-destination-mail");

                try {
//                  String content = IOUtils.toString(session.getInputStream());
                    final HashMap<String, String> map = new HashMap<>();
                    session.parseBody(map);
                    final String content = map.get("postData");
                    testEncryption = openPgpService.encrypt(destination, content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                }
                return newFixedLengthResponse(testEncryption);

            }
            case "decrypt-message": {
                String criptedMessage = getMockCriptedMessage();
                String decriptedMessage = "";
                try {
                    final HashMap<String, String> map = new HashMap<String, String>();
                    session.parseBody(map);
                    final String content = map.get("postData");
                    decriptedMessage = openPgpService.decrypt(content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return newFixedLengthResponse(decriptedMessage);

            }
            case "get-known-keys": {
                return newFixedLengthResponse("");
            }
            default: {
                return newFixedLengthResponse("Este e um servico");
            }
        }

    }

    private String getMockCriptedMessage() {
        return "-----BEGIN PGP MESSAGE-----\n" +
                "\n" +
                "hQGMA9D5LfzOMHdWAQv/dcztFPzWVDTRqOHI8kB2Yu6yrGpqUTyqnUTO8mR0cLuL\n" +
                "/RWeSfl5bVtQLpmEImpMbRSo6+IjHZoFj2/DTmPFrUfi4a8Piada7pZgbLhbkS1U\n" +
                "HCFXYT3fTY900tACrY/RLZrFvtrfSAxvoVeImmoIsQDLG6RCl915cHBypj3KQDtP\n" +
                "oiin4Hn4Nb52+iAhGip73qh6g1P+Hl8HNBhH6Eo2hqazKpEpyPeDaQfkPp+wM/+M\n" +
                "5++nmP5OyK9Q3D3leO4AT1KvHfyUGgohO7lpJrbgVbqKKPmDJdqH6CjsH8FtiFiW\n" +
                "bIcm4ydm7mAeUSw1T3ZKAD4t73cBSyLb1tiuWmH+sKPH1x+Zc9j05id8fdEoUmu9\n" +
                "fuBMeRFW48iJIH4Yb/chiMX7iHal2ikqvbqJHri12EgC9WEx2J672HTYawFV6eCZ\n" +
                "zpT6FTdcZRkOefmVP1e2XmRZh8VLG7R2Pg9kbKmvHBD/H1L0DGDaarLQTORzr7ER\n" +
                "8shds3JyKsxeihtICyF+0kwBZzMTOOQ1iJSDV/IpFcp1H94ceXaNvNh1k4lGLC6g\n" +
                "2hp/KkUQnszK90tK7sDm1e08UCkqKKK/efhiAKuoS/8CXhq4HjmhHT7qeVY/\n" +
                "=XUtj\n" +
                "-----END PGP MESSAGE-----\n";
    }
}
