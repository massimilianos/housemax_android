package it.max.android.housemax.utils;

import android.os.StrictMode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;

public class InternetUtils {
    private Properties properties = null;

    public InternetUtils() {}

    public InternetUtils(Properties properties) {
        this.properties = properties;
    }

    public String creaURLWebServer(Properties properties) {
        String URLWebServer = "http://" + properties.getProperty("webserverAddress")
                              + ":"     + properties.getProperty("webserverPort")
                              + "/"     + properties.getProperty("webserverSitePath");

        return(URLWebServer);
    }

    public String creaURLArduinoServer(Properties properties) {
        String URLArduinoServer = "http://" + properties.getProperty("arduinoAddress")
                                  + ":"     + properties.getProperty("arduinoPort")
                                  + "/index.htm?";

        return(URLArduinoServer);
    }

    private String leggiResponse (InputStream response) {
        BufferedReader r = new BufferedReader(new InputStreamReader(response));
        StringBuilder total = new StringBuilder();
        String line;

        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch(Exception e) {
        }

        return(total.toString());
    }

    public String internetResult(String indirizzo) throws Exception {
        String result = null;

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            DefaultHttpClient http = new DefaultHttpClient();
            HttpGet httpMethod = new HttpGet();
            httpMethod.setURI(new URI(indirizzo));
            HttpResponse response = http.execute(httpMethod);
            int responseCode = response.getStatusLine().getStatusCode();
            switch(responseCode)
            {
                case 200:
                    HttpEntity entity = response.getEntity();
                    if (entity != null)
                    {
                        result = EntityUtils.toString(entity);
                    }
                break;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }

        return(result);
    }
}
