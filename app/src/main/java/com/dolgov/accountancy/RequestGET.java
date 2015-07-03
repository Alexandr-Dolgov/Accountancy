package com.dolgov.accountancy;

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Alexandr on 28.06.2015.
 */
public class RequestGET extends AsyncTask<String, Void, JSONObject> {

    private final String TAG = this.getClass().getName();

    private String json;
    JSONObject jsonObj;

    @Override
    protected JSONObject doInBackground(String... urls) {
        try{
            URLConnection conn = (new URL(urls[0])).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            json = reader.readLine();
            reader.close();

            Object obj = null;
            try {
                obj = new JSONParser().parse(json);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
            jsonObj = (JSONObject) obj;

        } catch (IOException e){
            Log.d(TAG, e.toString());
            return null;
        }
        return jsonObj;
    }
}
