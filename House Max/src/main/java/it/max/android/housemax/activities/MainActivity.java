package it.max.android.housemax.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

import java.io.InputStream;
import java.util.Properties;

import it.max.android.housemax.R;
import it.max.android.housemax.services.HouseMaxService;
import it.max.android.housemax.utils.InternetUtils;

public class MainActivity extends Activity {
    Context context = null;

    private Resources resources = null;
    private AssetManager assetManager = null;

    Intent serviceHouseMax = null;

    InternetUtils internetUtils = null;

    Properties properties = null;

    String[] dati = null;

    TextView txtTemperatura;
    TextView txtUmidita;

    Switch swcManualControl;
    Switch swcRelay1;
    Switch swcRelay2;
    Switch swcRelay3;
    Switch swcRelay4;

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

        swcManualControl = (Switch) findViewById(R.id.swcManualControl);
        swcRelay1 = (Switch) findViewById(R.id.swcRelay1);
        swcRelay2 = (Switch) findViewById(R.id.swcRelay2);
        swcRelay3 = (Switch) findViewById(R.id.swcRelay3);
        swcRelay4 = (Switch) findViewById(R.id.swcRelay4);

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

        dati = new String[Integer.parseInt(properties.getProperty("numeroDati"))];

        Toast.makeText(context, "ACCENDO SERVIZIO HOUSEMAX", Toast.LENGTH_LONG).show();

        serviceHouseMax = new Intent(this, HouseMaxService.class);
        serviceHouseMax.putExtra("dati", dati);
        startService(serviceHouseMax);

        swcManualControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    String URL = internetUtils.creaURLArduinoServer(properties);

                    if (isChecked) {
                        URL = URL + "ManualControl=ON";
                        Log.d("URL ManualControl ON", URL);
                        URL = internetUtils.internetResult(URL);
                    } else {
                        URL = URL + "ManualControl=OFF";
                        Log.d("URL ManualControl OFF", URL);
                        URL = internetUtils.internetResult(URL);
                    }
                } catch(Exception e) {
                    Toast.makeText(context, "ERRORE CAMBIO CONTROLLO MANUALE (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        swcRelay1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    String URL = internetUtils.creaURLArduinoServer(properties);

                    if (isChecked) {
                        URL = URL + "Relay1=ON";
                        Log.d("URL Relay1 ON", URL);
                        URL = internetUtils.internetResult(URL);
                    } else {
                        URL = URL + "Relay1=OFF";
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
                    String URL = internetUtils.creaURLArduinoServer(properties);

                    if (isChecked) {
                        URL = URL + "Relay2=ON";
                        Log.d("URL Relay2 ON", URL);
                        URL = internetUtils.internetResult(URL);
                    } else {
                        URL = URL + "Relay2=OFF";
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
                    String URL = internetUtils.creaURLArduinoServer(properties);

                    if (isChecked) {
                        URL = URL + "Relay3=ON";
                        Log.d("URL Relay3 ON", URL);
                        URL = internetUtils.internetResult(URL);
                    } else {
                        URL = URL + "Relay3=OFF";
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
                    String URL = internetUtils.creaURLArduinoServer(properties);

                    if (isChecked) {
                        URL = URL + "Relay4=ON";
                        Log.d("URL Relay4 ON", URL);
                        URL = internetUtils.internetResult(URL);
                    } else {
                        URL = URL + "Relay4=OFF";
                        Log.d("URL Relay4 OFF", URL);
                        URL = internetUtils.internetResult(URL);
                    }
                } catch(Exception e) {
                    Toast.makeText(context, "ERRORE CAMBIO RELAY 4 (MAIN ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
