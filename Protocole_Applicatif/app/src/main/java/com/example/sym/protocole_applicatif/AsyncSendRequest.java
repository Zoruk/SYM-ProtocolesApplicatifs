package com.example.sym.protocole_applicatif;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Domingues on 14.10.2015.
 */
public class AsyncSendRequest extends AsyncTask {
    private URI uri;
    private final String CRLF = "\r\n";

    public void sendRequest(String request, String link){
        try {
            uri = new URI(link);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        int port = uri.getPort();
        if (port == -1) {
            port = 80;
        }
        try (Socket connection = new Socket(uri.getHost(), port)) {
            HttpURLConnection conn;
            connection.getOutputStream().write(new StringBuilder("POST " + link + " HTTP/1.0" + CRLF).toString().getBytes());

        } catch (IOException ex) {

        }
    }

    public void addCommunicationEventListener(CommunicationEventListener listener){

    }

    @Override
    protected Object doInBackground(Object[] params) {
        Log.d("test", (String) params[0]);
        sendRequest((String) params[0],(String) params[1]);
        return null;
    }
}
