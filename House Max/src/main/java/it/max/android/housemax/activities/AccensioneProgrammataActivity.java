package it.max.android.housemax.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import it.max.android.housemax.R;
import it.max.android.housemax.utils.InternetUtils;

public class AccensioneProgrammataActivity extends ActionBarActivity {
//    private Timer mTimer = null;
//    private Handler mHandler = new Handler();

    Context context = null;

    private Resources resources = null;
    private AssetManager assetManager = null;

    InternetUtils internetUtils = null;

    Properties properties = null;

    TextView txtOraAttuale;
    TimePicker timePickerOraSpegnimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accensione_programmata);

        txtOraAttuale = (TextView) findViewById(R.id.txtOraAttuale);
        timePickerOraSpegnimento = (TimePicker) findViewById(R.id.timePickerOraSpegnimento);

        timePickerOraSpegnimento.setIs24HourView(true);
/*
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }

        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, 1000);
*/
        try {
            context = getApplicationContext();

            resources = this.getResources();
            assetManager = resources.getAssets();

            InputStream inputStream = assetManager.open("housemax.properties");
            properties = new Properties();
            properties.load(inputStream);

            internetUtils = new InternetUtils(properties);
        } catch(Exception e) {
            Toast.makeText(context, "ERRORE LETTURA FILE PROPERTIES (ACCENSIONE PROGRAMMATA ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
            System.exit(-1);
        }

        final String URLArduinoServer = internetUtils.creaURLArduinoServer(properties);

        try {
            txtOraAttuale.setText(internetUtils.internetResult(URLArduinoServer + "ReadHour"));
        } catch(Exception e) {
            Toast.makeText(context, "ERRORE LETTURA ORA ATTUALE (ACCENSIONE PROGRAMMATA ACTIVITY)!!!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accensione_programmata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
/*
    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    Calendar cal = Calendar.getInstance();

                    Calendar calendar = Calendar.getInstance();
                    int ora = calendar.get(Calendar.HOUR_OF_DAY);
                    int minuti = calendar.get(Calendar.MINUTE);
                    int secondi = calendar.get(Calendar.SECOND);
                    String orario = ora + ":" + minuti + ":" + secondi;

                    txtOraAttuale.setText(dateFormat.format(cal.getTime()));
                }
            });
        }
    }
*/
}
