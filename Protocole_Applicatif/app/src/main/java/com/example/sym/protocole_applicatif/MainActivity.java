package com.example.sym.protocole_applicatif;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MainActivity extends Activity {
    private static final String LOG_TAG = MainActivity.class.getName();

    private SymComManager basicComManager = new SymComManager();
    private SymComManager jsonComManager = new SymComManager();
    private SymComManager base64ComManager = new SymComManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button send_basic = (Button) findViewById(R.id.send_basic);
        Button send_json = (Button) findViewById(R.id.send_json);
        Button send_zipbase64 = (Button) findViewById(R.id.send_zipbase64);

        send_basic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basicComManager.sendRequest("Super salut", "http://moap.iict.ch:8080/Moap/Basic");
            }
        });

        send_json.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("MyValue", "doge");
                    jsonObject.put("MyValue2", "Super salut");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                jsonComManager.sendRequest(jsonObject.toString(), "http://moap.iict.ch:8080/Moap/Json");
            }
        });

        send_zipbase64.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ZipOutputStream zip;

                try {
                    zip = new ZipOutputStream(out);
                    zip.putNextEntry(new ZipEntry("First"));
                    zip.write("Super salut text".getBytes());
                    zip.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                String base64 = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
                Log.d(LOG_TAG, "Base64 : " + base64);
                base64ComManager.sendRequest(base64, "http://moap.iict.ch:8080/Moap/ZipBase64");
            }
        });

        basicComManager.addCommunicationEventListener(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                Log.d(LOG_TAG, "Response " + response);
                if (response != null) {
                    TextView result = (TextView) findViewById(R.id.textview_result);
                    result.setText(response);
                }
                return true;
            }
        });

        jsonComManager.addCommunicationEventListener(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                Log.d(LOG_TAG, "Response " + response);
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        TextView result = (TextView) findViewById(R.id.textview_result);
                        result.setText(jsonObject.toString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                return true;
            }
        });

        base64ComManager.addCommunicationEventListener(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                Log.d(LOG_TAG, "Response " + response);
                if (response != null) {
                    byte[] zipByteArray = Base64.decode(response, Base64.DEFAULT);
                    ByteArrayInputStream is = new ByteArrayInputStream(zipByteArray);
                    ZipInputStream zipInputStream = new ZipInputStream(is);
                    try {
                        ZipEntry entry = zipInputStream.getNextEntry();
                        if (entry == null) {
                            return false;
                        }
                        BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream, "UTF-8"));
                        String name = entry.getName();
                        StringBuffer sb = new StringBuffer();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }

                        TextView result = (TextView) findViewById(R.id.textview_result);
                        result.setText("Entry name : " + name + "\nContent: \n" + sb.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
