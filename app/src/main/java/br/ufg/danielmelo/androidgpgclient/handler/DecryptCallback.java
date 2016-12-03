package br.ufg.danielmelo.androidgpgclient.handler;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import org.openintents.openpgp.util.OpenPgpApi;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.ufg.danielmelo.androidgpgclient.Start;
import br.ufg.danielmelo.androidgpgclient.entity.Response;
import br.ufg.danielmelo.androidgpgclient.openpgp.OpenPGPService;
import br.ufg.danielmelo.androidgpgclient.util.ProtocolSet;

public class DecryptCallback implements OpenPgpApi.IOpenPgpCallback {

    private final UUID protocol;
    private ByteArrayOutputStream os;
    private OpenPGPService pgpService;
    public static Map<UUID, DecryptCallback> callbackRepo = new HashMap<>();

    DecryptCallback(UUID protocol) {
        this.protocol = protocol;
        callbackRepo.put(this.protocol, this);
    }

    @Override
    public void onReturn(Intent result) {
        switch (result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {
            case OpenPgpApi.RESULT_CODE_SUCCESS: {
                Response res = new Response();
                res.setCipherContent(os.toString());
                ProtocolSet.setRequestResult(getProtocol(), res);
                callbackRepo.remove(getProtocol());
            }
            case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {
                PendingIntent pi = result.getParcelableExtra(OpenPgpApi.RESULT_INTENT);
                if (pi == null) return;
                try {
                    Start.startThis.startIntentSenderForResult(pi.getIntentSender(), 9913, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(getClass().getName(), "Erro ao lidar com intent pendente " + e);
                }
                break;
            }
            case OpenPgpApi.RESULT_CODE_ERROR: {
                Log.e(getClass().getName(), "Erro ao decriptar");
                callbackRepo.remove(this.getProtocol());
            }
        }
    }

    public UUID getProtocol() {
        return protocol;
    }

    public void setOutput(ByteArrayOutputStream os) {
        this.os = os;
    }

    public void setPGPService(OpenPGPService PGPService) {
        this.pgpService = PGPService;
    }
}
