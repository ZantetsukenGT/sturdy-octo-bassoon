package com.example.orga.app_orga;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static MediaPlayer mp = null;
    private static MediaPlayer bg = null;
    private static boolean isSetup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isSetup)
        {
            setup();
        }

        Switch s = findViewById(R.id.sDispositivo);
        s.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Graficar(v);
                mp.start();
            }
        });

        s = findViewById(R.id.sLED);
        s.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Graficar(v);
                mp.start();
            }
        });

        Button b = findViewById(R.id.bRecorridoLR);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Graficar(v);
                mp.start();
            }
        });

        b = findViewById(R.id.bRecorridoRL);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Graficar(v);
                mp.start();
            }
        });

        b = findViewById(R.id.bRecorridoDU);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Graficar(v);
                mp.start();
            }
        });

        b = findViewById(R.id.bRecorridoUD);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Graficar(v);
                mp.start();
            }
        });

        b = findViewById(R.id.bDibujar);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Graficar(v);
                mp.start();
            }
        });
    }

    private void setup()
    {
        if(mp == null)
        {
            mp = MediaPlayer.create(this, R.raw.button);
        }

        if(bg == null)
        {
            bg = MediaPlayer.create(this, R.raw.xmas);
            bg.setLooping(true);
            bg.start();
        }
        isSetup = true;
    }

    public void apagarSwitches()
    {
        Switch s = findViewById(R.id.sDispositivo);
        s.setChecked(false);
        s = findViewById(R.id.sLED);
        s.setChecked(false);
    }

    public void apagarLED()
    {
        Switch s = findViewById(R.id.sLED);
        s.setChecked(false);
    }

    public static boolean ping(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    public void Graficar(View view) {
        Switch dispositivo = findViewById(R.id.sDispositivo);
        Switch s = null;

        if(dispositivo.isChecked() || view.getId() == R.id.sDispositivo)
        {
            String ip = "192.168.1.23";
            int port = 8080;

            String request = "";
            switch (view.getId()) {
                case R.id.sLED:
                    s = (Switch)view;
                    if(s.isChecked())
                    {
                        request = "EncenderLED";
                    }
                    else
                    {
                        request = "ApagarLED";
                    }
                    break;
                case R.id.sDispositivo:
                    if(dispositivo.isChecked())
                    {
                        request = "EncenderDispositivo";
                    }
                    else
                    {
                        request = "ApagarDispositivo";
                    }
                    break;
                case R.id.bRecorridoLR:
                    apagarLED();
                    request = "LR";
                    break;
                case R.id.bRecorridoRL:
                    apagarLED();
                    request = "RL";
                    break;
                case R.id.bRecorridoDU:
                    apagarLED();
                    request = "DU";
                    break;
                case R.id.bRecorridoUD:
                    apagarLED();
                    request = "UD";
                    break;
                case R.id.bDibujar:
                    apagarLED();
                    request = "Dibujar";
                    break;
            }
            new MyTask().execute(request, ip, port);
        }
        else
        {
            apagarSwitches();
            Toast.makeText(MainActivity.this, "El dispositivo está apagado", Toast.LENGTH_SHORT).show();
        }
    }

    public int mandarRequest(final String request, final String ip, int port) {
        String urlString = "http://" + ip + ":" + port + "/orga/server/Animacion/" + request;
        int serverResponseCode = 0;

        if(!ping(ip,port,500))
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Error en la conexion, no estamos en la misma red", Toast.LENGTH_SHORT).show();
                }
            });
            return serverResponseCode;
        }
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");

            StringBuffer sb = new StringBuffer();
            InputStream is = null;
            is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            is.close();

            final String serverResponseMessage = sb.toString();
            serverResponseCode = urlConnection.getResponseCode();

            if (serverResponseCode == 200) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "OK: " + serverResponseMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if(serverResponseCode == 409)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Conflicto: " + serverResponseMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error: " + serverResponseMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "URL mal formada!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        }

            return serverResponseCode;
    }

    private class MyTask extends AsyncTask<Object, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Object... params) {
            int code = mandarRequest(params[0].toString(),params[1].toString(), (int)params[2]);

            params = new Object[]{code,params[0]};
            return params;
        }

        @Override
        protected void onPostExecute(Object[] response) {
            Switch dispositivo = findViewById(R.id.sDispositivo);
            if(response[0].equals(200))
            {
                if(response[1].toString().equals("EncenderDispositivo"))
                {
                    dispositivo.setChecked(true);
                }
                else if(response[1].toString().equals("ApagarDispositivo"))
                {
                    apagarSwitches();
                    dispositivo.setChecked(false);
                }
            }
            else
            {
                if(response[1].toString().equals("EncenderDispositivo"))
                {
                    dispositivo.setChecked(!dispositivo.isChecked());
                    apagarSwitches();
                }
                else if(response[1].toString().equals("ApagarDispositivo"))
                {
                    dispositivo.setChecked(!dispositivo.isChecked());
                }
            }
        }
    }
}
