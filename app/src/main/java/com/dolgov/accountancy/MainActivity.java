package com.dolgov.accountancy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    TextView date;
    Button prev;
    Button next;
    Button report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date = (TextView) findViewById(R.id.textViewDate);
        prev = (Button) findViewById(R.id.buttonPrev);
        next = (Button) findViewById(R.id.buttonNext);
        report = (Button) findViewById(R.id.buttonReport);

        date.setText("0");

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = date.getText().toString();
                int val = Integer.parseInt(text);
                val--;
                text = String.valueOf(val);
                date.setText(text);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = date.getText().toString();
                int val = Integer.parseInt(text);
                val++;
                text = String.valueOf(val);
                date.setText(text);
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = date.getText().toString();
                int val = Integer.parseInt(text);
                val = 0;
                text = String.valueOf(val);
                date.setText(text);
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
