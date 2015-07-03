package com.dolgov.accountancy;

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private String json;
    JSONObject jsonObj;

    @Override
    protected JSONObject doInBackground(String... urls) {
        URL url = null;
        try {
            url = new URL(urls[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection connection = null;
        //connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary););

        try {
            connection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setDoOutput(true);
        PrintWriter out = null;
        try {
            out = new PrintWriter(connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.print("file=");
        try {
            out.print(URLEncoder.encode(urls[1], "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        out.close();
        Log.d(TAG, "connection.getContentType() = " + connection.getContentType());


        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            json = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "json = " + json);


        return null;
    }
}
