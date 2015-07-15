package com.dolgov.accountancy;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Alexandr on 29.06.2015.
 */
public class RequestPOST extends AsyncTask<String, Void, JSONObject> {
    private final String TAG = this.getClass().getName();

    private Activity activity;
    private File file;

    public RequestPOST(Activity activity, File file){
        this.activity = activity;
        this.file = file;
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
            String delimiter = "--";
            os.write( (delimiter + boundary + "\r\n").getBytes() );
            String fileName = file.getName();
            String contentDisposition = "Content-Disposition: form-data; " +
                    "name=\"file\"; " +
                    "filename=\"" + fileName + "\"" +
                    "\r\n";
            os.write( contentDisposition.getBytes() );
            os.write("Content-Type: application/vnd.ms-excel\r\n\r\n".getBytes());

            InputStream is = new FileInputStream(file);
            byte[] b = new byte[1024];
            while ( is.read(b) != -1) {
                os.write(b);
            }

            os.write("\r\n".getBytes());
            os.write( (delimiter + boundary + delimiter + "\r\n").getBytes() );

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
