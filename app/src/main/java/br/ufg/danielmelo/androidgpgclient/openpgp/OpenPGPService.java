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

import br.ufg.danielmelo.androidgpgclient.Constants;

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


    public String encrypt(String targetMail, String content) throws UnsupportedEncodingException {
        Intent data = new Intent();
        data.setAction(OpenPgpApi.ACTION_SIGN_AND_ENCRYPT);
        data.putExtra(OpenPgpApi.EXTRA_USER_IDS, new String[]{ targetMail });
        data.putExtra(OpenPgpApi.EXTRA_SIGN_KEY_ID, mSignKeyId);
        data.putExtra(OpenPgpApi.EXTRA_REQUEST_ASCII_ARMOR, true);

        final InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        OpenPgpApi api = new OpenPgpApi(originalActivity, mServiceConnection.getService());

        try {

            Intent result = api.executeApi(data, is, os);
            int operationStatus = result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR);
            switch(operationStatus) {

                case  OpenPgpApi.RESULT_CODE_SUCCESS: {
                    String encripted = os.toString("UTF-8");
                    System.out.println(encripted);
                    return encripted;
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

            return "";
        }catch (Exception e) {
            Log.e("RESULT_ENCRYPT", "Falhou!" + e.getLocalizedMessage());
        }

        return "";

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == OpenPgpApi.RESULT_CODE_SUCCESS) {
            switch (requestCode) {
                case 42: {
                    try {
                        encrypt("",  "");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
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

    public String decrypt(String criptedMessage) throws UnsupportedEncodingException {

        Intent data = new Intent();
        data.setAction(OpenPgpApi.ACTION_DECRYPT_VERIFY);
        data.putExtra(OpenPgpApi.EXTRA_REQUEST_ASCII_ARMOR, true);

        final InputStream is = new ByteArrayInputStream(criptedMessage.getBytes("UTF-8"));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        OpenPgpApi api = new OpenPgpApi(originalActivity, mServiceConnection.getService());

        try {
            Intent result = api.executeApi(data, is, os);
            int operationStatus = result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR);
            switch(operationStatus) {

                case  OpenPgpApi.RESULT_CODE_SUCCESS: {
                    OpenPgpDecryptionResult decryptionResult
                            = result.getParcelableExtra(OpenPgpApi.RESULT_DECRYPTION);
//                    return decryptionResult.toString();
                    return os.toString();
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
                    System.out.println(error);
                }

            }

            return "";
        }catch (Exception e) {
            Log.e("RESULT_ENCRYPT", "Falhou!" + e.getLocalizedMessage());
        }

        return "";


    }
}
