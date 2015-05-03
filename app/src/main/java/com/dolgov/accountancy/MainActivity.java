package com.dolgov.accountancy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

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

    private final String TAG = this.getClass().getName();

    private Record currentRecord;

    private DatabaseAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findMyViews();
        tvDate.setText("0");

        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.selectAll();

        //получаем последнюю запись из БД
        currentRecord = dbAdapter.getLastRecord();
        showCurrentRecord();

        bCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Record newRecord;
                //newRecord = new Record(prevRecord, 990.5, 592.56, 145, 610, 0);
                try {
                    newRecord = new Record(
                            currentRecord,
                            Double.parseDouble(etReceipt.getText().toString()),
                            Double.parseDouble(etPrepared.getText().toString()),
                            Double.parseDouble(etRemainder.getText().toString()),
                            Double.parseDouble(etSold.getText().toString()),
                            Double.parseDouble(etWriteOff.getText().toString())
                    );
                } catch (NumberFormatException e){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Заполните все поля!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                Log.d(TAG, newRecord.toString());

                double product = newRecord.getProduct();
                tvProduct.setText(String.format("%.2f", product));

                double money = newRecord.getMoney();
                tvMoney.setText(String.format("%.2f", money));

                dbAdapter.insert(newRecord);
                dbAdapter.selectAll();

                currentRecord = newRecord;
            }
        });

        bPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //если в базе данных существует запись с пред. датой,
                // тогда загружаем ее
                // иначе говорим что пред. записей нет

                Record record = dbAdapter.getPrevRecord(currentRecord);
                if (record != null){
                    Log.d(TAG, "prev Record Exist");
                    currentRecord = record;
                    showCurrentRecord();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Это самая первая запись!\n" +
                            "Предыдущих записей нет.",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //если в базе данных существует запись со след. датой,
                // тогда загружаем ее
                // инача показываем чистый экран
                Record record = dbAdapter.getNextRecord(currentRecord);
                if (record != null) {
                    Log.d(TAG, "next Record Exist");
                    currentRecord = record;
                    showCurrentRecord();
                } else {
                    Log.d(TAG, "next Record not Exist");
                    clearEditTexts();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                    tvDate.setText(sdf.format(currentRecord.getNextDate()));
                }
            }
        });

        bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Еще не реализовано!",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    private void showCurrentRecord() {
        Record record = currentRecord;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        tvDate.setText(sdf.format(record.getDate()));

        etReceipt.setText(String.format("%.2f", record.getReceipt()));
        etPrepared.setText(String.format("%.2f", record.getPrepared()));
        etRemainder.setText(String.format("%.2f", record.getRemainder()));
        etSold.setText(String.format("%.2f", record.getSold()));
        etWriteOff.setText(String.format("%.2f", record.getWriteOff()));
        tvProduct.setText(String.format("%.2f", record.getProduct()));
        tvMoney.setText(String.format("%.2f", record.getMoney()));
    }

    //этот метод вызывается перед уничтожением Activity
    //в этом методе мы освобождаем ресерсы
    @Override
    protected void onDestroy(){
        super.onDestroy();
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
