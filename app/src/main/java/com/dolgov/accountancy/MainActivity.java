package com.dolgov.accountancy;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

    TextView tvDate;

    EditText etReceipt;     //приход
    EditText etPrepared;    //приготовила
    EditText etRemainder;   //остаток
    EditText etSold;        //продала
    EditText etWriteOff;    //хоз. нужды

    TextView tvProduct;
    Button bCalc;
    TextView tvMoney;

    Button bPrev;
    Button bNext;
    Button bReport;

    private static final String TAG = "Accountancy";

    private Record prevRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findMyViews();
        tvDate.setText("0");

        //получаем пред. запись. Пока в тестовом режиме это первая запись,
        //потом тут должа браться последняя запись из БД
        prevRecord = Record.getFirst();

        //TODO
        //если база данных не существует, то
        //  создадим ее
        //  поместим туда Record.getFirst()

        bCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Record currentRecord;
                //currentRecord = new Record(prevRecord, 990.5, 592.56, 145, 610, 0);
                currentRecord = new Record(
                        prevRecord,
                        Double.parseDouble(etReceipt.getText().toString()),
                        Double.parseDouble(etPrepared.getText().toString()),
                        Double.parseDouble(etRemainder.getText().toString()),
                        Double.parseDouble(etSold.getText().toString()),
                        Double.parseDouble(etWriteOff.getText().toString())
                );
                Log.d(TAG, currentRecord.toString());

                double product = currentRecord.getProduct();
                tvProduct.setText(String.format("%.2f", product));

                double money = currentRecord.getMoney();
                tvMoney.setText(String.format("%.2f", money));

                prevRecord = currentRecord;
            }
        });

        bPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tvDate.getText().toString();
                int val = Integer.parseInt(text);
                val--;
                text = String.valueOf(val);
                tvDate.setText(text);
            }
        });

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tvDate.getText().toString();
                int val = Integer.parseInt(text);
                val++;
                text = String.valueOf(val);
                tvDate.setText(text);


                //prevRecord.insertIntoDB();

                clearEditTexts();
            }
        });

        bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tvDate.getText().toString();
                int val = Integer.parseInt(text);
                val = 0;
                text = String.valueOf(val);
                tvDate.setText(text);
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

    protected void clearEditTexts(){
        etReceipt.setText("");
        etPrepared.setText("");
        etRemainder.setText("");
        etSold.setText("");
        etWriteOff.setText("");

        tvProduct.setText("?");
        tvMoney.setText("?");
    }

    protected void findMyViews(){
        tvDate = (TextView) findViewById(R.id.textViewDate);

        etReceipt = (EditText) findViewById(R.id.editTextReceipt);
        etPrepared = (EditText) findViewById(R.id.editTextPrepared);
        etRemainder = (EditText) findViewById(R.id.editTextRemainder);
        etSold = (EditText) findViewById(R.id.editTextSold);
        etWriteOff = (EditText) findViewById(R.id.editTextWriteOff);

        tvProduct = (TextView) findViewById(R.id.textViewProductValue);
        bCalc = (Button) findViewById(R.id.buttonCalc);
        tvMoney = (TextView) findViewById(R.id.textViewMoneyValue);

        bPrev = (Button) findViewById(R.id.buttonPrev);
        bNext = (Button) findViewById(R.id.buttonNext);
        bReport = (Button) findViewById(R.id.buttonReport);
    }
}
