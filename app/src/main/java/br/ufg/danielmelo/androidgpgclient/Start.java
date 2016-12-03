package br.ufg.danielmelo.androidgpgclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.openintents.openpgp.util.OpenPgpApi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import br.ufg.danielmelo.androidgpgclient.handler.DecryptCallback;
import br.ufg.danielmelo.androidgpgclient.handler.EncryptCallback;
import br.ufg.danielmelo.androidgpgclient.openpgp.OpenPGPService;
import br.ufg.danielmelo.androidgpgclient.util.IPUtil;

public class Start extends AppCompatActivity {

    public static Context elContexto;

    public static Start startThis;

    public static OpenPGPService openPgpService;
    private ListView clearedApplicationsView;

//    public static WebSocketServer wsServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        elContexto = getApplicationContext();
        startThis = Start.this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        openPgpService = new OpenPGPService(this);
        HTTPThread.iniciar(openPgpService);
        String ip = IPUtil.wifiIpAddress(this.getBaseContext(), this);
        TextView t = (TextView) findViewById(R.id.textView);
        t.setText("Meu ip é " + ip + ". Acesse os serviços na porta 8881");
        String senhaSessao = UUID.randomUUID().toString().substring(0,8);
        Auth.setSenhaSessao(senhaSessao);
        TextView senhaSessaoView = (TextView) findViewById(R.id.senhaSessao);
        senhaSessaoView.setText(senhaSessao);
    }

    public ListView getClearedApplicationsView() {
        return clearedApplicationsView;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        openPgpService.unbind();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // try again after user interaction
        if (resultCode == OpenPgpApi.RESULT_CODE_SUCCESS || resultCode == -1) {
            switch (requestCode) {
                case 42: {
                    UUID callbackid = UUID.fromString(data.getStringExtra("callback"));
                    EncryptCallback callback = EncryptCallback.callbackRepo.get(callbackid);
                    if (callback !=null) {
                        openPgpService.encryptAsyncNext(data, callback);
                        Toast.makeText(getApplicationContext(), "Encriptando mensagem", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case 9913: {
                    UUID callbackid = UUID.fromString(data.getStringExtra("callback"));
                    DecryptCallback callback = DecryptCallback.callbackRepo.get(callbackid);
                    if (callback !=null) {
                        openPgpService.decryptAsyncNext(data, callback);
                        Toast.makeText(getApplicationContext(), "Decriptando mensagem", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

            }
        }

        if (resultCode == OpenPgpApi.RESULT_CODE_ERROR) {
            System.err.println("ERRO NA OPERAÇAO");
        }
    }


}
