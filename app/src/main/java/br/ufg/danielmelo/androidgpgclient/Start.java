package br.ufg.danielmelo.androidgpgclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.openintents.openpgp.util.OpenPgpApi;
import org.openintents.openpgp.util.OpenPgpServiceConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import br.ufg.danielmelo.androidgpgclient.util.IPUtil;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Start extends AppCompatActivity {

    public static Context elContexto;

    public static Activity startThis;

    OpenPgpServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        elContexto = getApplicationContext();
        startThis = Start.this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        HTTPThread.iniciar();

        mServiceConnection = new OpenPgpServiceConnection(this, "org.sufficientlysecure.keychain");
        mServiceConnection.bindToService();

        OpenPgpApi api = new OpenPgpApi(this, mServiceConnection.getService());

        TextView t = (TextView) findViewById(R.id.textView);
        String ip = IPUtil.wifiIpAddress(this.getBaseContext(), this);
        System.out.print(ip);
        t.setText("Meu ip é " + ip + ". Acesse os serviços na porta 30001");

    }

    public void signAndEncrypt(Intent data) {
//        data.setAction(OpenPgpApi.ACTION_SIGN_AND_ENCRYPT);
//        data.putExtra(OpenPgpApi.EXTRA_SIGN_KEY_ID, mSignKeyId);
//        if (!TextUtils.isEmpty(mEncryptUserIds.getText().toString())) {
//            data.putExtra(OpenPgpApi.EXTRA_USER_IDS, mEncryptUserIds.getText().toString().split(","));
//        }
//        data.putExtra(OpenPgpApi.EXTRA_REQUEST_ASCII_ARMOR, true);
//
//        InputStream is = getInputstream("Meu texto");
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//
//        OpenPgpApi api = new OpenPgpApi(this, mServiceConnection.getService());
//        api.executeApiAsync(data, is, os, new MyCallback(true, os, REQUEST_CODE_SIGN_AND_ENCRYPT));
    }

    private InputStream getInputstream(String text) {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(Constants.TAG, "UnsupportedEncodingException", e);
        }

        return is;
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, BaseActivity.class);
        startActivity(intent);
    }



}
