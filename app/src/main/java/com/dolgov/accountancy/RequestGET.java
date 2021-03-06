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
 * GET должен корректно работать при запросах до 2kb
 */
public class RequestGET extends AsyncTask<String, Void, JSONObject> {

    private final String TAG = this.getClass().getName();

    @Override
    protected JSONObject doInBackground(String... urls) {
        JSONObject jsonObj;
        try{
            //urls[0] -- обязательно URL строка
            // соответствующим образом закодированная,
            // например с помощью URLEncoder.encode(str, "UTF-8");
            URLConnection conn = (new URL(urls[0])).openConnection();
            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            String json = reader.readLine();
            reader.close();

            Object obj;
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
