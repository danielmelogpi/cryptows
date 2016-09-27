package br.ufg.danielmelo.androidgpgclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import br.ufg.danielmelo.androidgpgclient.util.IPUtil;

public class Start extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        HTTPThread.iniciar();

        TextView t = (TextView) findViewById(R.id.textView);
        String ip = IPUtil.wifiIpAddress(this.getBaseContext(), this);
        System.out.print(ip);
        t.setText("Meu ip é " + ip + ". Acesse os serviços na porta 30001");

    }

}
