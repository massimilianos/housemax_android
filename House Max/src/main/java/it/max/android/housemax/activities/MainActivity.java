package it.max.android.housemax.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import it.max.android.housemax.R;
import it.max.android.housemax.services.HouseMaxService;
import it.max.android.housemax.utils.InternetUtils;

public class MainActivity extends Activity {
    private static final Integer MODALITA_INVERNO = 0;
    private static final Integer MODALITA_ESTATE  = 1;

    Context context = null;

    private Resources resources = null;
    private AssetManager assetManager = null;

    Intent serviceHouseMax = null;

    InternetUtils internetUtils = null;

    Properties properties = null;

    String[] dati = null;

    TextView txtTemperatura;
    TextView txtUmidita;

    ImageButton btnModalita;

    EditText txtTempControllo;
    TextView lblTempControllo;
    Button btnTempControllo;

    Switch swcManualControl;
    Switch swcRelay1;
    Switch swcRelay2;
    Switch swcRelay3;
    Switch swcRelay4;

    Button btnAccensioneProgrammata;

    Button btnBluetoothOnOff;

    private void impostaModalita(String URLArduinoServer, Integer modalita) {
        if (modalita == MODALITA_INVERNO) {
            try {
                internetUtils.internetResult(URLArduinoServer + "SetModalita=" + MODALITA_INVERNO);
                btnModalita.setImageResource(R.drawable.winter);
                lblTempControllo.setText("Min.");

                internetUtils.internetResult(URLArduinoServer + "TemperatureRead");
            } catch(Exception e) {
                Toast.makeText(context, "ERRORE IMPOSTAZIONE MODALITA' INVERNO (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                internetUtils.internetResult(URLArduinoServer + "SetModalita=" + MODALITA_ESTATE);
                btnModalita.setImageResource(R.drawable.summer);
                lblTempControllo.setText("Max.");

                internetUtils.internetResult(URLArduinoServer + "TemperatureRead");
            } catch(Exception e) {
                Toast.makeText(context, "ERRORE IMPOSTAZIONE MODALITA' ESTATE (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void disabilitaInterruttori() {
        swcRelay1.setEnabled(false);
        swcRelay2.setEnabled(false);
        swcRelay3.setEnabled(false);
        swcRelay4.setEnabled(false);
    }

    private void abilitaInterruttori() {
        swcRelay1.setEnabled(true);
        swcRelay2.setEnabled(true);
        swcRelay3.setEnabled(true);
        swcRelay4.setEnabled(true);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String[] dati = bundle.getStringArray("dati");

            txtTemperatura.setText(dati[0] + "°");
            txtUmidita.setText(dati[1] + "%");
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemperatura = (TextView) findViewById(R.id.txtTemperatura);
        txtUmidita = (TextView) findViewById(R.id.txtUmidita);

        btnModalita = (ImageButton) findViewById(R.id.btnModalita);

        txtTempControllo = (EditText) findViewById(R.id.txtTempControllo);
        lblTempControllo = (TextView) findViewById(R.id.lblTempControllo);
        btnTempControllo = (Button) findViewById(R.id.btnTempControllo);

        swcManualControl = (Switch) findViewById(R.id.swcManualControl);
        swcRelay1 = (Switch) findViewById(R.id.swcRelay1);
        swcRelay2 = (Switch) findViewById(R.id.swcRelay2);
        swcRelay3 = (Switch) findViewById(R.id.swcRelay3);
        swcRelay4 = (Switch) findViewById(R.id.swcRelay4);

        btnAccensioneProgrammata = (Button) findViewById(R.id.btnAccensioneProgrammata);

        try {
            context = getApplicationContext();

            resources = this.getResources();
            assetManager = resources.getAssets();

            InputStream inputStream = assetManager.open("housemax.properties");
            properties = new Properties();
            properties.load(inputStream);

            internetUtils = new InternetUtils(properties);
        } catch(Exception e) {
            Toast.makeText(context, "ERRORE LETTURA FILE PROPERTIES (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
            System.exit(-1);
        }

        final String URLArduinoServer = internetUtils.creaURLArduinoServer(properties);

        try {
            txtTempControllo.setText(internetUtils.internetResult(URLArduinoServer + "ReadTempControl"));

            String sModalitaAttuale = internetUtils.internetResult(URLArduinoServer + "ReadModalita");
            Integer iModalitaAttuale = Integer.valueOf(sModalitaAttuale);

            impostaModalita(URLArduinoServer, iModalitaAttuale);
        } catch(Exception e) {
            Toast.makeText(context, "ERRORE LETTURA TEMPERATURA CONTROLLO (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
        }

        this.disabilitaInterruttori();

        dati = new String[Integer.parseInt(properties.getProperty("numeroDati"))];

        Toast.makeText(context, "ACCENDO SERVIZIO HOUSEMAX", Toast.LENGTH_LONG).show();

        serviceHouseMax = new Intent(this, HouseMaxService.class);
        serviceHouseMax.putExtra("dati", dati);
        startService(serviceHouseMax);

        btnModalita.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    String sModalitaAttuale = internetUtils.internetResult(URLArduinoServer + "ReadModalita");
                    Integer iModalitaAttuale = Integer.valueOf(sModalitaAttuale);

                    if (iModalitaAttuale == MODALITA_INVERNO) {
                        impostaModalita(URLArduinoServer, MODALITA_ESTATE);
                    } else {
                        impostaModalita(URLArduinoServer, MODALITA_INVERNO);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "ERRORE LETTURA MODALITA (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        btnTempControllo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            try {
                String temperaturaControlloNuova = txtTempControllo.getText().toString();
                internetUtils.internetResult(URLArduinoServer + "SetTempControl=" + temperaturaControlloNuova);
            } catch(Exception e) {
                Toast.makeText(context, "ERRORE IMPOSTAZIONE TEMPERATURA CONTROLLO (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
            }
            }

        });

        swcManualControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    String URL;

                    if (isChecked) {
                        URL = URLArduinoServer + "ManualControl=ON";
                        Log.d("URL ManualControl ON", URL);
                        URL = internetUtils.internetResult(URL);
                        abilitaInterruttori();
                    } else {
                        URL = URLArduinoServer + "ManualControl=OFF";
                        Log.d("URL ManualControl OFF", URL);
                        URL = internetUtils.internetResult(URL);
                        disabilitaInterruttori();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "ERRORE CAMBIO CONTROLLO MANUALE (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        swcRelay1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                String URL;

                if (isChecked) {
                    URL = URLArduinoServer + "Relay1=ON";
                    Log.d("URL Relay1 ON", URL);
                    URL = internetUtils.internetResult(URL);
                } else {
                    URL = URLArduinoServer + "Relay1=OFF";
                    Log.d("URL Relay1 OFF", URL);
                    URL = internetUtils.internetResult(URL);
                }
            } catch(Exception e) {
                Toast.makeText(context, "ERRORE CAMBIO RELAY 1 (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
            }
            }
        });

        swcRelay2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                String URL;

                if (isChecked) {
                    URL = URLArduinoServer + "Relay2=ON";
                    Log.d("URL Relay2 ON", URL);
                    URL = internetUtils.internetResult(URL);
                } else {
                    URL = URLArduinoServer + "Relay2=OFF";
                    Log.d("URL Relay2 OFF", URL);
                    URL = internetUtils.internetResult(URL);
                }
            } catch(Exception e) {
                Toast.makeText(context, "ERRORE CAMBIO RELAY 2 (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
            }
            }
        });

        swcRelay3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                String URL;

                if (isChecked) {
                    URL = URLArduinoServer + "Relay3=ON";
                    Log.d("URL Relay3 ON", URL);
                    URL = internetUtils.internetResult(URL);
                } else {
                    URL = URLArduinoServer + "Relay3=OFF";
                    Log.d("URL Relay3 OFF", URL);
                    URL = internetUtils.internetResult(URL);
                }
            } catch(Exception e) {
                Toast.makeText(context, "ERRORE CAMBIO RELAY 3 (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
            }
            }
        });

        swcRelay4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                String URL;

                if (isChecked) {
                    URL = URLArduinoServer + "Relay4=ON";
                    Log.d("URL Relay4 ON", URL);
                    URL = internetUtils.internetResult(URL);
                } else {
                    URL = URLArduinoServer + "Relay4=OFF";
                    Log.d("URL Relay4 OFF", URL);
                    URL = internetUtils.internetResult(URL);
                }
            } catch(Exception e) {
                Toast.makeText(context, "ERRORE CAMBIO RELAY 4 (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
            }
            }
        });
/*
        btnAccensioneProgrammata.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    Intent intentAccensioneProgrammata = new Intent(MainActivity.this, AccensioneProgrammataActivity.class);
                    startActivity(intentAccensioneProgrammata);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(context, "ERRORE CHIAMATA ACCENSIONE PROGRAMMATA (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        btnBluetoothOnOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    Intent intentAccensioneProgrammata = new Intent(MainActivity.this, AccensioneProgrammataActivity.class);
                    startActivity(intentAccensioneProgrammata);
                    finish();
                } catch(Exception e) {
                    Toast.makeText(context, "ERRORE BLUETOOTH ON/OFF (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
                }
            }

        });
*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, new IntentFilter(HouseMaxService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Toast.makeText(context, "SPENGO SERVIZIO HOUSEMAX", Toast.LENGTH_LONG).show();

        stopService(serviceHouseMax);

        System.exit(0);
    }
}
