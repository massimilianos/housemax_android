package it.max.android.housemax.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import it.max.android.housemax.utils.InternetUtils;

public class HouseMaxService extends Service {
    public static final Integer NUMERO_DATI = 2;

    public static final String NOTIFICATION = "it.max.android.housemax.services";

//    public static final long NOTIFY_INTERVAL = 1 * 60000; // 60 secondi = 1 minuto
    public static final long NOTIFY_INTERVAL = 1 * 10000; // 10 secondi

    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    private InternetUtils internetUtils = null;

    private Context context = null;

    Properties properties = null;

//    private String URLWebServer = null;
    private String URLArduinoServer = null;

    String[] dati = null;

    private Resources resources = null;
    private AssetManager assetManager = null;

    private Properties apriFileProperties(Context context) {
        try {
            resources = this.getResources();
            assetManager = resources.getAssets();

            InputStream inputStream = assetManager.open("housemax.properties");
            properties = new Properties();
            properties.load(inputStream);
        } catch(Exception e) {
            Toast.makeText(context, "ERRORE LETTURA FILE PROPERTIES (HOUSEMAXSERVICE)!!!", Toast.LENGTH_SHORT).show();
            System.exit(-1);
        }

        return(properties);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        Properties properties = this.apriFileProperties(context);

        dati = new String[Integer.parseInt(properties.getProperty("numeroDati"))];

        internetUtils = new InternetUtils(properties);

//        URLWebServer = internetUtils.creaURLWebServer(properties);
        URLArduinoServer = internetUtils.creaURLArduinoServer(properties);

        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }

        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

        Toast.makeText(context, "SERVIZIO HOUSEMAX ACCESO", Toast.LENGTH_LONG).show();
    }

    private void publishResults(String[] dati) {
        Intent intent = new Intent(NOTIFICATION);

        intent.putExtra("dati", dati);

        sendBroadcast(intent);
    }

    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                String URL = null;

                try {
                    // LEGGO LA TEMPERATURA
//                    URL = URLWebServer + "/dati/ElaboraDati.php?operazione=select&valore=temperatura";
                    URL = URLArduinoServer + "TemperatureRead";
                    Log.d("URL TemperatureRead", URL);
                    dati[0] = internetUtils.internetResult(URL);

                    // LEGGO L'UMIDITA
//                    URL = URLWebServer + "/dati/ElaboraDati.php?operazione=select&valore=umidita";
                    URL = URLArduinoServer + "HumidityRead";
                    Log.d("URL HumidityRead", URL);
                    dati[1] = internetUtils.internetResult(URL);
                } catch(Exception e) {
                }

                publishResults(dati);
                }
            });
        }
    }
}
