package it.max.android.housemax.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Properties;

import it.max.android.housemax.R;
import it.max.android.housemax.services.HouseMaxService;
import it.max.android.housemax.utils.InternetUtils;

public class MainActivity extends ActionBarActivity {
    Context context = null;

    private Resources resources = null;
    private AssetManager assetManager = null;

    Intent serviceHouseMax = null;

    InternetUtils internetUtils = null;

    Properties properties = null;

    String[] dati = null;

    private TextView txtTemperatura = (TextView)findViewById(R.id.txtTemperatura);
    private TextView txtUmidita = (TextView)findViewById(R.id.txtUmidita);

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String[] dati = bundle.getStringArray("dati");

            txtTemperatura.setText(dati[0]);
            txtUmidita.setText(dati[1]);
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
}
