package br.ufg.danielmelo.androidgpgclient;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;
import android.widget.Toast;

import org.openintents.openpgp.OpenPgpDecryptionResult;
import org.openintents.openpgp.OpenPgpError;
import org.openintents.openpgp.OpenPgpSignatureResult;
import org.openintents.openpgp.util.OpenPgpApi;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by daniel on 26/09/16.
 */

public class MyCallback implements OpenPgpApi.IOpenPgpCallback {

    public static final int REQUEST_CODE_CLEARTEXT_SIGN = 9910;
    public static final int REQUEST_CODE_ENCRYPT = 9911;
    public static final int REQUEST_CODE_SIGN_AND_ENCRYPT = 9912;
    public static final int REQUEST_CODE_DECRYPT_AND_VERIFY = 9913;
    public static final int REQUEST_CODE_GET_KEY = 9914;
    public static final int REQUEST_CODE_GET_KEY_IDS = 9915;
    public static final int REQUEST_CODE_DETACHED_SIGN = 9916;
    public static final int REQUEST_CODE_DECRYPT_AND_VERIFY_DETACHED = 9917;
    public static final int REQUEST_CODE_BACKUP = 9918;


    boolean returnToCiphertextField;
    ByteArrayOutputStream os;
    int requestCode;

    private MyCallback(boolean returnToCiphertextField, ByteArrayOutputStream os, int requestCode) {
        this.returnToCiphertextField = returnToCiphertextField;
        this.os = os;
        this.requestCode = requestCode;
    }

    private void showToast(final String message) {
        Toast.makeText(Start.elContexto,
                message,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReturn(Intent result) {
        switch (result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {
            case OpenPgpApi.RESULT_CODE_SUCCESS: {
//                showToast("RESULT_CODE_SUCCESS");

                // encrypt/decrypt/sign/verify
                if (os != null) {
                    try {
                        Log.d(OpenPgpApi.TAG, "result: " + os.toByteArray().length
                                + " str=" + os.toString("UTF-8"));

                        Toast.makeText(Start.elContexto, "Sucesso" + os.toString("UTF-8"), Toast.LENGTH_LONG);

//                        if (returnToCiphertextField) {
//                            mCiphertext.setText(os.toString("UTF-8"));
//                        } else {
//                            mMessage.setText(os.toString("UTF-8"));
//                        }
                    } catch (UnsupportedEncodingException e) {
                        Log.e(Constants.TAG, "UnsupportedEncodingException", e);
                    }
                }

                switch (requestCode) {
                    case REQUEST_CODE_DECRYPT_AND_VERIFY:
                    case REQUEST_CODE_DECRYPT_AND_VERIFY_DETACHED: {
                        // RESULT_SIGNATURE and RESULT_DECRYPTION are never null!

                        OpenPgpSignatureResult signatureResult
                                = result.getParcelableExtra(OpenPgpApi.RESULT_SIGNATURE);
                        showToast(signatureResult.toString());
                        OpenPgpDecryptionResult decryptionResult
                                = result.getParcelableExtra(OpenPgpApi.RESULT_DECRYPTION);
                        showToast(decryptionResult.toString());

                        break;
                    }
                    case REQUEST_CODE_DETACHED_SIGN: {
                        byte[] detachedSig
                                = result.getByteArrayExtra(OpenPgpApi.RESULT_DETACHED_SIGNATURE);
                        Log.d(OpenPgpApi.TAG, "RESULT_DETACHED_SIGNATURE: " + detachedSig.length
                                + " str=" + new String(detachedSig));
//                        mDetachedSignature.setText(new String(detachedSig));

                        break;
                    }
                    case REQUEST_CODE_GET_KEY_IDS: {
                        long[] keyIds = result.getLongArrayExtra(OpenPgpApi.RESULT_KEY_IDS);
                        String out = "keyIds: ";
//                        for (long keyId : keyIds) {
//                            out += OpenPgpUtils.convertKeyIdToHex(keyId) + ", ";
//                        }
                        showToast(out);

                        break;
                    }
                    default: {

                    }
                }

                break;
            }
            case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {
                showToast("RESULT_CODE_USER_INTERACTION_REQUIRED");

                PendingIntent pi = result.getParcelableExtra(OpenPgpApi.RESULT_INTENT);
                try {
                    Start.startThis.startIntentSenderFromChild(
                            Start.startThis, pi.getIntentSender(),
                            requestCode, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(Constants.TAG, "SendIntentException", e);
                }
                break;
            }
            case OpenPgpApi.RESULT_CODE_ERROR: {
                showToast("RESULT_CODE_ERROR");

                OpenPgpError error = result.getParcelableExtra(OpenPgpApi.RESULT_ERROR);
                handleError(error);
                break;
            }
        }
    }

    private void handleError(OpenPgpError error) {
        System.err.print(error);
    }
}