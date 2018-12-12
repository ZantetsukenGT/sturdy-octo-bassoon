package com.example.orga.app_orga;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private static MediaPlayer mp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if(mp == null)
        {
            mp = MediaPlayer.create(this, R.raw.button);
        }

        Button b = findViewById(R.id.bGuardar);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mp.start();
                guardarConfig(v);
            }
        });
    }

    public void guardarConfig(View view)
    {
        TextView txtHost = findViewById(R.id.txtHost);
        TextView txtPort = findViewById(R.id.txtPort);

        String host = txtHost.getText().toString();
        String port = txtPort.getText().toString();

        if(host.length() > 0 && port.length() > 0)
        {
            if(host.split("\\.").length == 4)
            {
                String result = host + ":" + port;

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",result);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
            else
            {
                Toast myToast = Toast.makeText(this, "IP mal construida", Toast.LENGTH_SHORT);
                myToast.show();
            }
        }
        else
        {
            Toast myToast = Toast.makeText(this, "No deje los campos vacios", Toast.LENGTH_SHORT);
            myToast.show();
        }
    }
}
