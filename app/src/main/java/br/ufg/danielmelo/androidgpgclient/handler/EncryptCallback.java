package br.ufg.danielmelo.androidgpgclient.handler;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.openintents.openpgp.util.OpenPgpApi;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.ufg.danielmelo.androidgpgclient.Start;
import br.ufg.danielmelo.androidgpgclient.entity.Response;
import br.ufg.danielmelo.androidgpgclient.openpgp.OpenPGPService;

public class EncryptCallback implements OpenPgpApi.IOpenPgpCallback, Serializable {

    private final WebSocket responseSocket;

    private final UUID uniqueId;
    private ByteArrayOutputStream os;
    private OpenPGPService pgpService;
    public static Map<UUID, EncryptCallback> callbackRepo = new HashMap<>();

    EncryptCallback(WebSocket responseSocket) {
        this.responseSocket = responseSocket;
        this.uniqueId = UUID.randomUUID();
        callbackRepo.put(this.uniqueId, this);
    }

    @Override
    public void onReturn(Intent result) {
        switch (result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {
            case OpenPgpApi.RESULT_CODE_SUCCESS: {
                Response res = new Response();
                res.setContent(os.toString());
                responseSocket.send(res.toJson());
                callbackRepo.remove(this.getUniqueId());
            }
            case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {
                PendingIntent pi = result.getParcelableExtra(OpenPgpApi.RESULT_INTENT);
                if (pi == null) return;
                try {
                    Start.startThis.startIntentSenderForResult(pi.getIntentSender(), 42, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(getClass().getName(), "Erro ao lidar com intent pendente");
                }
                break;
            }
            case OpenPgpApi.RESULT_CODE_ERROR: {
                Log.e(getClass().getName(), "Erro ao encriptar");
                callbackRepo.remove(this.getUniqueId());
            }
        }
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setOutput(ByteArrayOutputStream os) {
        this.os = os;
    }

    public void setPGPService(OpenPGPService PGPService) {
        this.pgpService = PGPService;
    }
}
