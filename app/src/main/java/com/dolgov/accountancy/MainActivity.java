package com.dolgov.accountancy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

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

    private static final String VK_APP_ID = "4902641";

    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onAcceptUserToken " + token);
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            Log.d("VkDemoApp", "onReceiveNewToken " + newToken);
        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onRenewAccessToken " + token);
        }

        @Override
        public void onCaptchaError(VKError captchaError) {
            Log.d("VkDemoApp", "onCaptchaError " + captchaError);
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            Log.d("VkDemoApp", "onTokenExpired " + expiredToken);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            Log.d("VkDemoApp", "onAccessDenied " + authorizationError);
        }
    };

    private static String[] sMyScope = new String[] {
            VKScope.MESSAGES,
            VKScope.DOCS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findMyViews();

        VKUIHelper.onCreate(this);

        //коннектимся к БД, получаем последнюю запись и отображаем ее
        dbAdapter = new DatabaseAdapter(this);
        currentRecord = dbAdapter.getLastRecord();
        showCurrentRecord();

        bCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO сделать пересчет всех последующих записей на основании текущей
                //для этого сделать в DatabaseAdapter метод изменения той или иной записи в БД

                //создаем новую запись по данным из полей ввода
                //и помещаем ее в текущую
                Record newRecord;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    Date currentDate = sdf.parse(tvDate.getText().toString(), new ParsePosition(0));
                    newRecord = new Record(
                            dbAdapter.getLastRecord(),
                            currentDate,
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
                currentRecord = newRecord;

                //отображаем деньги и продукты из толькочто созданной записи
                double product = newRecord.getProduct();
                tvProduct.setText(String.format("%.2f", product));
                double money = newRecord.getMoney();
                tvMoney.setText(String.format("%.2f", money));
            }
        });

        bPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //если текущей записи не существует в БД, т.е. мы на этапе ввода новой записи
                //  тогда скажем что текущей записью теперь будет последняя запись из БД,
                //  отобразим ее и выйдем из метода
                if (currentRecord == null) {
                    currentRecord = dbAdapter.getLastRecord();
                    showCurrentRecord();
                    return;
                }

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
                if (currentRecord == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Пропустить этот день?")
                            .setCancelable(false)
                            .setPositiveButton("Да, пропустить",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            clearEditTexts();

                                            //показываем след. дату
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                            Date currentDate = sdf.parse(tvDate.getText().toString(), new ParsePosition(0));
                                            GregorianCalendar calendar = new GregorianCalendar();
                                            calendar.setTime(currentDate);
                                            calendar.add(Calendar.DATE, 1);
                                            Date nextDate = calendar.getTime();
                                            tvDate.setText(sdf.format(nextDate));

                                            currentRecord = null;

                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton("Нет, не пропускать",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();


                    /*
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Введите данные и нажмите кнопку =\n" +
                                    "чтобы сохранить текущий день\n" +
                                    "и иметь возможность\n" +
                                    "перейти ко вводу нового дня.",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    */

                    return;
                }

                //если в базе данных существует запись со след. датой,
                // тогда загружаем ее
                Record record = dbAdapter.getNextRecord(currentRecord);
                if (record != null) {
                    Log.d(TAG, "next Record Exist");
                    currentRecord = record;
                    showCurrentRecord();
                // иначе
                } else{
                    Log.d(TAG, "next Record not Exist");
                    //если текущая запись не совпадает с последней записью в БД
                    // заносим текущую запись в БД
                    if (!currentRecord.equals(dbAdapter.getLastRecord())){
                        dbAdapter.insert(currentRecord);
                    }
                    //в любом случае очищаем поля
                    clearEditTexts();

                    //и показываем след. дату
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    Date currentDate = sdf.parse(tvDate.getText().toString(), new ParsePosition(0));
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTime(currentDate);
                    calendar.add(Calendar.DATE, 1);
                    Date nextDate = calendar.getTime();
                    tvDate.setText(sdf.format(nextDate));

                    currentRecord = null;
                }
            }
        });

        bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.initialize(sdkListener, VK_APP_ID);
                if (VKSdk.wakeUpSession()) {
                    Log.d(TAG, "работаем с vk");
                    report();

                } else {
                    Log.d(TAG, "авторизуемся в vk");
                    VKSdk.authorize(sMyScope, true, false);
                    Log.d(TAG, "работаем с vk сразу после авторизации");
                    report();
                }
            }
        });
    }

    private void report() {
        //получаем дату последний записи в БД
        Date lastDate = dbAdapter.getLastRecord().getDate();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(lastDate);

        //вычисляем по ней имяМесяца
        int numCurrentMonth = calendar.get(Calendar.MONTH);
        String currentMonth = Util.month(numCurrentMonth);
        Log.d(TAG, "currentMonth = " + currentMonth);

        //вычисляем имяПредМесяца
        String prevMonth = Util.prevMonth(numCurrentMonth);
        Log.d(TAG, "prevMonth = " + prevMonth);

        //показываем пользователю диалог выбора месяца который нужно экспортнуть в xls и отправить



        //выбираем из БД все записи где дата относится к выбранному пользователем месяцу
        //по массиву выбранных записей формируем xls файл
        //и отправляем его

        try{
            sendMessageVK();
        } catch (IOException e){
            showAlertDialog("Ошибка при отправке сообщения", e.toString());
            return;
        }

        //сообщаем о успешной отправке отчета
    }

    private File createXlsReport(){
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(2.71828);

        // Write the output to a file
        String dirPath = getApplicationInfo().dataDir;
        String fileName = "demo.xls";
        File file = new File(dirPath, fileName);
        Log.d(TAG, "xlsFile AbsolutePath = " + file.getAbsolutePath());
        try {
            FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return file;
    }

    private void sendMessageVK() throws IOException {

        VKAccessToken vkAccessToken = VKSdk.getAccessToken();
        String access_token = vkAccessToken.accessToken;
        Log.d(TAG, "access_token = " + access_token);

        //определяем переменные для формирования запросов к API vk.com
        String method_name;
        String userId;
        String message;
        String parameters;
        String request;
        JSONObject jsonObj = null;

        //получаем upload_url куда будем сохранять документ
        method_name = "docs.getUploadServer";
        parameters = "version=5.34";
        request = "https://api.vk.com/method/" + method_name + "?" +
                parameters + "&access_token=" + access_token;
        Log.d(TAG, "request = " + request);
        jsonObj = null;
        try {
            jsonObj = new RequestGET().execute(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            jsonObj = (JSONObject)jsonObj.get("response");
        } catch (Exception e){
            e.printStackTrace();
            showAlertDialog("Ошибка. Убедитесь в наличии доступа в интернет", e.toString());
            return;
        }
        String uploadUrl = (String)jsonObj.get("upload_url");
        Log.d(TAG, "upload_url = " + uploadUrl);

        //загружаем документ на сервер по полученному ранее upload_url
        File fileXlsReport = createXlsReport();
        try {
            jsonObj = new RequestPOST(this, fileXlsReport).execute(uploadUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String file = (String)jsonObj.get("file");
        Log.d(TAG, "file = " + file);

        //сохраняем загруженный документ
        method_name = "docs.save";
        parameters = "file=" + file + "" +
                "&title=" + fileXlsReport.getName() +
                "&version=5.34";
        request = "https://api.vk.com/method/" + method_name + "?" +
                parameters + "&access_token=" + access_token;
        Log.d(TAG, "request = " + request);
        jsonObj = null;
        try {
            jsonObj = new RequestGET().execute(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = (JSONArray) jsonObj.get("response");
        Log.d(TAG, jsonArray.toString());
        jsonObj = (JSONObject) jsonArray.get(0);
        Long did = (Long)jsonObj.get("did");
        Log.d(TAG, "did = " + did);

        //отправляем сообщение с приаттаченым по id ранее загруженным документом
        String type = "doc";
        long owner_id = (Long)jsonObj.get("owner_id");
        long media_id = did;
        String attachment = type + owner_id + "_" + media_id;
        method_name = "messages.send";
        //userId = "170819313";   //идентификатор Евгения Спиридонова
        userId = "12375097";    //идентификатор Александра Долгова
        Log.d(TAG, "идентификатор пользователя которому отправляем сообщение user_id=" + userId);
        message = URLEncoder.encode("бухгалтерия за имяМесяца", "UTF-8");
        parameters = "user_id=" + userId +
                "&message=" + message +
                "&attachment=" + attachment +
                "&version=5.34";
        request = "https://api.vk.com/method/" + method_name + "?" +
                parameters + "&access_token=" + access_token;
        Log.d(TAG, "request = " + request);
        jsonObj = null;
        try {
            jsonObj = new RequestGET().execute(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("ок",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCurrentRecord() {
        Record record = currentRecord;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        tvDate.setText(sdf.format(record.getDate()));

        etReceipt.setText(String.format("%.2f", record.getReceipt()));
        etPrepared.setText(String.format("%.2f", record.getPrepared()));
        etRemainder.setText(String.format("%.2f", record.getRemainder()));
        etSold.setText(String.format("%.2f", record.getSold()));
        etWriteOff.setText(String.format("%.2f", record.getWriteOff()));
        tvProduct.setText(String.format("%.2f", record.getProduct()));
        tvMoney.setText(String.format("%.2f", record.getMoney()));
    }

    @Override
    protected void onResume(){
        super.onResume();
        VKUIHelper.onResume(this);
    }

    //этот метод вызывается перед уничтожением Activity
    //в этом методе мы освобождаем ресерсы
    @Override
    protected void onDestroy(){
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
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
