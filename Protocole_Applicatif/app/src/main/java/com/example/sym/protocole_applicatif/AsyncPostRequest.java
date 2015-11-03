package com.example.sym.protocole_applicatif;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Domingues on 14.10.2015.
 */
public class AsyncPostRequest extends AsyncTask {

    private String payload;
    private URL url;
    final String charset = "UTF-8";
    final String DEBUG_TAG = "AsyncPostRequest";

    public AsyncPostRequest(String url, String payload) throws MalformedURLException {
        this.url = new URL(url);
        this.payload = payload;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            //connection.setRequestProperty("Content-type", "application/json");
            OutputStream output = connection.getOutputStream();
            output.write(payload.getBytes(charset));
            output.close();

            //connection.setDoInput(true);
            connection.setRequestMethod("POST");

            connection.connect();

            Log.d(DEBUG_TAG, "Connection result : " + connection.getResponseCode() + " content " + connection.getResponseMessage());
            is = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder sb = new StringBuilder();
            sb.append(reader.readLine() + "\n");
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
            if (is != null)
                try {
                    is.close();
                    Log.d(DEBUG_TAG, "IS Closed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        if (result != null) {
            String s = (String)result;
            Log.d(DEBUG_TAG, "Content : " + s);
        }
    }
}
