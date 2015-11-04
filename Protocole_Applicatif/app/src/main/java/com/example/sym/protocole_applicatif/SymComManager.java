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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zoruk on 04.11.15.
 */
public class SymComManager {
    private static final String LOG_TAG = SymComManager.class.getName();

    public SymComManager() {

    }

    private List<CommunicationEventListener> mListeners = new ArrayList<>();

    public void sendRequest(String request, String link) {
        new AsyncHttpPostRequest().execute(link, request);
    }

    public void addCommunicationEventListener(CommunicationEventListener listener) {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    private class AsyncHttpPostRequest extends AsyncTask<String, Void, String> {

        private static final String ENCODING = "UTF-8";
        @Override
        protected String doInBackground(String... params) {
            if (params.length != 2) {
                return null;
            }
            URL url;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            String payload = params[1];

            HttpURLConnection connection;
            InputStream is = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            try {
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept-Charset", ENCODING);
                //connection.setRequestProperty("Content-type", "application/json");
                OutputStream output = connection.getOutputStream();
                output.write(payload.getBytes(ENCODING));
                output.close();

                //connection.setDoInput(true);
                connection.setRequestMethod("POST");

                connection.connect();

                Log.d(LOG_TAG, "Connection result : " + connection.getResponseCode() + " content " + connection.getResponseMessage());
                is = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line;
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
                        Log.d(LOG_TAG, "IS Closed");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null)
                return;

            synchronized (mListeners) {
                for (CommunicationEventListener e : mListeners) {
                    if (e.handleServerResponse(s))
                        return;
                }
            }
        }
    }
}
