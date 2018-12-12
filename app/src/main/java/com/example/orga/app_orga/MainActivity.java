package com.example.orga.app_orga;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

    public static int port = 0;
    public static String host = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle(R.string.titleMenu);

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

        ImageButton ib = findViewById(R.id.bConfigurar);

        ib.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mp.start();
                Configurar(v);
            }
        });

        Button b = findViewById(R.id.bRecorridoLR);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mp.start();
                Graficar(v);
            }
        });

        b = findViewById(R.id.bRecorridoRL);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mp.start();
                Graficar(v);
            }
        });

        b = findViewById(R.id.bRecorridoDU);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mp.start();
                Graficar(v);
            }
        });

        b = findViewById(R.id.bRecorridoUD);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mp.start();
                Graficar(v);
            }
        });

        b = findViewById(R.id.bDibujar);
        b.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mp.start();
                Graficar(v);
            }
        });
    }

    public void Configurar(View view)
    {
        Intent randomIntent = new Intent(this, Settings.class);

        startActivityForResult(randomIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {//Settings
            if(resultCode == Activity.RESULT_OK)
            {
                String result = data.getStringExtra("result");
                String[] array = result.split(":");
                host = array[0];
                port = Integer.parseInt(array[1]);
                new MyTask().execute("Config");
            }
        }
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
            new MyTask().execute(request);
        }
        else
        {
            apagarSwitches();
            Toast.makeText(MainActivity.this, "El dispositivo está apagado", Toast.LENGTH_SHORT).show();
        }
    }

    public int mandarRequest(final String request) {
        final String urlString = "http://" + host + ":" + port + "/orga/server/Animacion/" + request;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, urlString, Toast.LENGTH_SHORT).show();
            }
        });

        if(ping(host,port,500))
        {
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
                final int serverResponseCode = urlConnection.getResponseCode();

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
                else if(serverResponseCode == 406)
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
                            Toast.makeText(MainActivity.this, "Error: " + serverResponseCode, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return serverResponseCode;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "URL mal formada!", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Error en la conexion, no estamos en la misma red", Toast.LENGTH_SHORT).show();
            }
        });
        return 0;
    }

    public class MyTask extends AsyncTask<Object, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Object... params) {
            int code = mandarRequest(params[0].toString());

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
