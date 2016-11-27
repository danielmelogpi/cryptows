package br.ufg.danielmelo.androidgpgclient.openpgp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.openintents.openpgp.IOpenPgpService2;
import org.openintents.openpgp.OpenPgpDecryptionResult;
import org.openintents.openpgp.OpenPgpError;
import org.openintents.openpgp.util.OpenPgpApi;
import org.openintents.openpgp.util.OpenPgpAppPreference;
import org.openintents.openpgp.util.OpenPgpKeyPreference;
import org.openintents.openpgp.util.OpenPgpServiceConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import br.ufg.danielmelo.androidgpgclient.Constants;
import br.ufg.danielmelo.androidgpgclient.entity.Message;
import br.ufg.danielmelo.androidgpgclient.handler.DecryptCallback;
import br.ufg.danielmelo.androidgpgclient.handler.EncryptCallback;

public class OpenPGPService extends Activity {

    private final SharedPreferences settings;
    private final long mSignKeyId;
    private final String providerPackageName;
    private static OpenPgpServiceConnection mServiceConnection;
    private Activity originalActivity;
    public boolean ready = false;

    public OpenPGPService(Activity actv) {
        originalActivity = actv;
        settings = PreferenceManager.getDefaultSharedPreferences(originalActivity);
        providerPackageName = settings.getString("openpgp_provider_list", "");
        mSignKeyId = settings.getLong("openpgp_key", 0);

        bind();
    }

    public long getUserId(String mail) throws UnsupportedEncodingException {
        Intent data = new Intent();
        data.setAction(OpenPgpApi.ACTION_GET_KEY);
        data.putExtra(OpenPgpApi.EXTRA_KEY_ID, mail);

        final InputStream is = new ByteArrayInputStream("".getBytes("UTF-8"));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        OpenPgpApi api = new OpenPgpApi(originalActivity.getApplicationContext(), mServiceConnection.getService());
        try {
            Intent result = api.executeApi(data, is, os);
            int operationStatus = result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR);
            switch(operationStatus) {
                case OpenPgpApi.RESULT_CODE_SUCCESS: {
                    long keyId = result.getLongExtra(OpenPgpApi.EXTRA_USER_IDS, 0l);
                    return keyId;
                }
                case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {

                    PendingIntent pi = result.getParcelableExtra(OpenPgpApi.RESULT_INTENT);
                    try {
                        originalActivity.startIntentSenderForResult(pi.getIntentSender(), 42, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(Constants.TAG, "SendIntentException", e);
                    }
                    break;

                }
                case  OpenPgpApi.RESULT_CODE_ERROR: {
                    OpenPgpError error = result.getParcelableExtra(OpenPgpApi.RESULT_ERROR);
                    System.out.println(error.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return 0l;
    }


    public void encryptAsync(Message msg, EncryptCallback callback) {
        Intent data = new Intent();
        data.setAction(OpenPgpApi.ACTION_SIGN_AND_ENCRYPT);
        data.putExtra(OpenPgpApi.EXTRA_USER_IDS, new String[]{ msg.getReceiver() });
        data.putExtra(OpenPgpApi.EXTRA_SIGN_KEY_ID, mSignKeyId);
        data.putExtra(OpenPgpApi.EXTRA_REQUEST_ASCII_ARMOR, true);
        data.putExtra("content", msg.getContent());
        data.putExtra("callback", callback.getUniqueId().toString());

        encryptAsyncNext(data, callback);
    }

    /** Reutilizado quando a senha e dada pelo usario e uma nova tentativa e feita */
    public void encryptAsyncNext(Intent data, EncryptCallback callback) {
        final InputStream is = new ByteArrayInputStream(data.getStringExtra("content").getBytes());
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        OpenPgpApi api = new OpenPgpApi(originalActivity, mServiceConnection.getService());
        callback.setOutput(os);
        callback.setPGPService(this);
        try {
            api.executeApiAsync(data, is, os, callback);
        }catch (Exception e) {
            Log.e(getClass().getName(), "Erro ao executar api para criptografar");
        }
    }


    public void decryptAsync(Message msg, DecryptCallback callback) {
        Intent data = new Intent();
        data.setAction(OpenPgpApi.ACTION_DECRYPT_VERIFY);
        data.putExtra(OpenPgpApi.EXTRA_REQUEST_ASCII_ARMOR, true);
        data.putExtra("content", msg.getContent());
        data.putExtra("callback", callback.getUniqueId().toString());

        decryptAsyncNext(data, callback);
    }

    public void decryptAsyncNext(Intent data, DecryptCallback callback) {
        final InputStream is = new ByteArrayInputStream(data.getStringExtra("content").getBytes());
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        callback.setOutput(os);
        callback.setPGPService(this);
        OpenPgpApi api = new OpenPgpApi(originalActivity, mServiceConnection.getService());
        try {
            api.executeApiAsync(data, is, os, callback);
        }catch (Exception e) {
            Log.e(getClass().getName(), "Erro ao executar api para criptografar");
        }
    }

    private void bind() {
        mServiceConnection = new OpenPgpServiceConnection(
                originalActivity.getApplicationContext(),
                providerPackageName,
                new OpenPgpServiceConnection.OnBound() {
                    @Override
                    public void onBound(IOpenPgpService2 service) {
                        Log.d(OpenPgpApi.TAG, "Bound to Provider service!");
                        ready = true;
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(OpenPgpApi.TAG, "exception when binding!", e);
                    }
                }
        );
        mServiceConnection.bindToService();
    }

    public void unbind() {
        if (mServiceConnection.isBound()) {
            mServiceConnection.unbindFromService();
        }
    }

}
