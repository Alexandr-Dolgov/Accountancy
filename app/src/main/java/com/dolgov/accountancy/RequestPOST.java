package com.dolgov.accountancy;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Alexandr on 29.06.2015.
 */
public class RequestPOST extends AsyncTask<String, Void, JSONObject> {
    private final String TAG = this.getClass().getName();

    private Activity activity;

    private String json;
    JSONObject jsonObj;

    public RequestPOST(Activity activity){
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonObj = null;
        try {
            String url = params[0];
            URLConnection urlConnection = (new URL(url)).openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            String boundary = Long.toString(System.currentTimeMillis());
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.connect();
            OutputStream os = connection.getOutputStream();
            String delimetr = "--";
            os.write( (delimetr + boundary + "\r\n").getBytes() );
            String filename = "f1.pdf";
            os.write( ("Content-Disposition: form-data; " +
                    "name=\"file\"; " +
                    "filename=\"" + filename + "\"\r\n").getBytes() );
            os.write("Content-Type: application/pdf".getBytes());

            Resources r = activity.getResources();
            InputStream is = r.openRawResource(R.raw.f1);
            byte[] b = new byte[1024];
            while ( is.read(b) != -1) {
                os.write(b);
            }

            os.write("\r\n".getBytes());
            os.write( (delimetr + boundary + delimetr + "\r\n").getBytes() );

            //считываем ответ
            is = connection.getInputStream();
            byte[] b1 = new byte[1];
            StringBuffer buffer = new StringBuffer();
            while ( is.read(b1) != -1)
                buffer.append(new String(b1));
            connection.disconnect();

            String jsonString = buffer.toString();
            Log.d(TAG, "jsonString = " + jsonString);

            Object obj = new JSONParser().parse(jsonString);
            jsonObj = (JSONObject) obj;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }
}
