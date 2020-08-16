package com.example.stockmarketapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "HomeActivity";

    Context mContext;
    Button b_logOut, b_notification;
    FirebaseAuth mFirebaseAuth;
    TextView tv_offered_price, tv_today_low, tv_today_high, tv_previous_close, tv_52_low, tv_52_high, tv_open_price;
    TextView tv_lu_low, tv_lu_high, tv_ytd, tv_1_week, tv_1_month, tv_3_month, tv_6_month, tv_1_year;
    TextView tv_2_year, tv_3_year, tv_30_day_avg, tv_50_day_avg, tv_150_day_avg, tv_200_day_avg;
    ProgressBar pb_todays, pb_52_weeks, pb_lu_band_price;
    Button b_1D, b_5D, b_1M, b_3M, b_6M, real_time_data;

    LineChart mLineChart;
    ProgressBar progressBar;
    Spinner drop_down_spinner;
    TextView tv_loading, tv_stockName, tv_stockVolume, tv_stockOpen;
    private FirebaseAuth.AuthStateListener authStateListener;
    private MyDatabaseHelper mydb;
    private ArrayList<StockData> stockDataList;


    int days = 50;
    String time = " 50 days.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drop_down_spinner = findViewById(R.id.dropdown_spinner);
        tv_loading = findViewById(R.id.tv_loading);


        mLineChart = findViewById(R.id.line_graph);
        mContext = getApplicationContext();
        b_logOut = findViewById(R.id.button_logOut);
        mydb = new MyDatabaseHelper(mContext);
        b_notification = findViewById(R.id.notification_button);
        tv_stockName = findViewById(R.id.tv_stock_name);
        tv_stockVolume = findViewById(R.id.tv_stock_volume);
        tv_stockOpen = findViewById(R.id.tv_stock_open);

        tv_offered_price = findViewById(R.id.tv_offered_price);
        tv_today_low = findViewById(R.id.tv_today_low);
        tv_today_high = findViewById(R.id.tv_today_high);
        tv_previous_close = findViewById(R.id.tv_previous_close);
        tv_52_low = findViewById(R.id.tv_52_week_low);
        tv_52_high = findViewById(R.id.tv_52_high);
        tv_open_price = findViewById(R.id.tv_openig_price);
        tv_lu_low = findViewById(R.id.tv_LU_low);
        tv_lu_high = findViewById(R.id.tv_lu_high);
        tv_ytd = findViewById(R.id.tv_ytd);
        tv_1_week = findViewById(R.id.tv_1_week);
        tv_1_month = findViewById(R.id.tv_1_month);
        tv_3_month = findViewById(R.id.tv_3_month);
        tv_6_month = findViewById(R.id.tv_6_month);
        tv_1_year = findViewById(R.id.tv_1_year);
        tv_2_year = findViewById(R.id.tv_2_year);
        tv_3_year = findViewById(R.id.tv_3_year);
        tv_30_day_avg = findViewById(R.id.tv_30_day_avg);
        tv_50_day_avg = findViewById(R.id.tv_50_day_avg);
        tv_150_day_avg = findViewById(R.id.tv_150_day_avg);
        tv_200_day_avg = findViewById(R.id.tv_200_day_avg);

        pb_52_weeks = findViewById(R.id.progressBar_52_week);
        pb_lu_band_price = findViewById(R.id.progressBar_lu_band_price);
        pb_todays = findViewById(R.id.progressBar_todays);

        b_1D = findViewById(R.id.butt_1D);
        b_5D = findViewById(R.id.butt_5D);
        b_1M = findViewById(R.id.butt_1M);
        b_3M = findViewById(R.id.butt_3M);
        b_6M = findViewById(R.id.butt_6M);
        real_time_data = findViewById(R.id.butt_real_time_data);


        stockDataList = new ArrayList<>();
        mFirebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.drop_down_menu, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drop_down_spinner.setAdapter(adapter);
        drop_down_spinner.setOnItemSelectedListener(this);


        b_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                toastMessage("logging out");
                finish();

            }
        });
        checkIfDataExistInDatabase("BSE");

        b_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification(tv_stockName.getText().toString(),
                        tv_open_price.getText().toString(), tv_previous_close.getText().toString());
            }
        });


        createGraphButtons();

    }

    public void toastMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private void createGraphButtons() {


        b_1D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = 365;
                time = " last 1 Year.";
                setup();
            }
        });

        b_5D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = 5;
                time = " last 5 Days.";
                setup();
            }
        });

        b_1M.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = 30;
                time = " last Month.";
                setup();
            }
        });
        b_3M.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = 90;
                time = " last 3 Month.";
                setup();
            }
        });


        b_6M.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days = 180;
                time = " last 6 Month.";
                setup();
            }
        });

       real_time_data.setVisibility(View.GONE);


    }

    public String getTableName(String companyName) {
        String table_name = null;
        switch (companyName) {
            case "BSE":
                table_name = mydb.TABLE_NAME_BSE;
                break;
            case "NSE":
                table_name = mydb.TABLE_NAME_NSE;
                break;
            case "RELIANCE":
                table_name = mydb.TABLE_NAME_RELIANCE;
                break;
            case "ASHOKLEY":
                table_name = mydb.TABLE_NAME_ASHOKELEY;
                break;
            case "TATA STEELS":
                table_name = mydb.TABLE_NAME_TATA_STEEL;
                break;
            case "NSIE":
                table_name = mydb.TABLE_NAME_BSE;
                break;
            case "^NSEI":
                table_name = mydb.TABLE_NAME_NSEI;
                break;
            case "EICHERMOT.NS":
                table_name = mydb.TABLE_NAME_EICHER;
                break;
            case "^BSESN":
                table_name = mydb.TABLE_NAME_BSESN;
                break;
            case "CIPLA.NS":
                table_name = mydb.TABLE_NAME_CIPLA;
                break;
        }
        return table_name;
    }

    public void checkIfDataExistInDatabase(String selectedCompany) {

        String table_name = getTableName(selectedCompany);
        Cursor cursor = mydb.getAllData(table_name);
        cursor.moveToFirst();
        if (!stockDataList.isEmpty()) {
            stockDataList.clear();
        }

        while (!cursor.isAfterLast()) {

            StockData data = new StockData();
            data.setDate(cursor.getString(1).toString());
            data.setOpen(cursor.getFloat(2));
            data.setHigh(cursor.getFloat(3));
            data.setLow(cursor.getFloat(4));
            data.setClose(cursor.getFloat(5));
            data.setAdj_close(cursor.getFloat(6));
            data.setVolume(cursor.getInt(7));

            stockDataList.add(data);
            cursor.moveToNext();
        }
        if (stockDataList.size() == 0) {
            Log.d(TAG, "checkIfDataExistInDatabase: Data does not exist in database.");
            ReadAsyncTask task = new ReadAsyncTask(this);
            task.execute(selectedCompany, "NSE", "RELIANCE", "ASHOKLEY", "TATA STEELS");
        } else {
            Log.d(TAG, "checkIfDataExistInDatabase: Data already  existed.");
            setUpGraph();
            setUpDataOnView();
        }
    }


    private void readDataFormCSV(String companyName) {

        Log.d(TAG, "readDataFormCSV: Called");
        String tableName = null;
        InputStream inputStream;
        switch (companyName) {
            case "BSE":
                tableName = mydb.TABLE_NAME_BSE;
                inputStream = getResources().openRawResource(R.raw.bse_sensex);
                break;
            case "NSE":
                tableName = mydb.TABLE_NAME_NSE;
                inputStream = getResources().openRawResource(R.raw.nse_nifty);
                break;
            case "RELIANCE":
                tableName = mydb.TABLE_NAME_RELIANCE;
                inputStream = getResources().openRawResource(R.raw.reliance_ns);
                break;
            case "ASHOKLEY":
                tableName = mydb.TABLE_NAME_ASHOKELEY;
                inputStream = getResources().openRawResource(R.raw.ashokley_ns);
                break;
            case "TATA STEELS":
                tableName = mydb.TABLE_NAME_TATA_STEEL;
                inputStream = getResources().openRawResource(R.raw.tatasteel_ns);
                break;

            case "^NSEI":
                tableName = mydb.TABLE_NAME_NSEI;
                inputStream = getResources().openRawResource(R.raw.nsie);
                break;
            case "EICHERMOT.NS":

                inputStream = getResources().openRawResource(R.raw.eichermot_ns);
                break;
            case "^BSESN":
                inputStream = getResources().openRawResource(R.raw.bsesn);
                break;
            case "CIPLA.NS":
                inputStream = getResources().openRawResource(R.raw.cipla_ns);
                break;
            default:
                inputStream = getResources().openRawResource(R.raw.bse_sensex);

        }

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName("UTF-8"))
        );

        String line = null;


        try {
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {


                // Split line
                String[] tokens = line.split(",");


                // Read Line
                StockData data = new StockData();
                data.setDate(tokens[0]);

                if (tokens[0].equals("null") || tokens[1].equals("null")) {
                    data.setOpen(0);
                } else {
                    data.setOpen(Float.parseFloat(tokens[1]));
                }

                if (tokens[0].equals("null") || tokens[2].equals("null")) {
                    data.setHigh(0);
                } else {
                    data.setHigh(Float.parseFloat(tokens[2]));
                }

                if (tokens[0].equals("null") || tokens[3].equals("null")) {
                    data.setLow(0);
                } else {
                    data.setLow(Float.parseFloat(tokens[3]));
                }
                if (tokens[0].equals("null") || tokens[4].equals("null")) {
                    data.setClose(0);
                } else {
                    data.setClose(Float.parseFloat(tokens[4]));
                }
                if (tokens[0].equals("null") || tokens[5].equals("null")) {
                    data.setAdj_close(0);
                } else {
                    data.setAdj_close(Float.parseFloat(tokens[5]));
                }
                if (tokens[0].equals("null") || tokens[6].equals("null")) {
                    data.setVolume(0);
                } else {
                    data.setVolume(Integer.parseInt(tokens[6]));
                }

                //Store data in list
                stockDataList.add(data);

                boolean result = mydb.insertData(tableName, data.getDate(), data.getOpen(), data.getHigh(),
                        data.getLow(), data.getClose(), data.getAdj_close(), data.getVolume());
               /* if(result){
                    toastMessage("Insert Successful.");
                }else{
                    toastMessage("Error : Insert Unsuccessful");
                }*/
            }

        } catch (IOException e) {
            Log.w(TAG, "readDataFormCSV: Error in reading file : " + line, e);
            e.printStackTrace();
        }

        Log.d(TAG, "readDataFormCSV: Completed.");

    }

    public void setUpGraph() {

//    mLineChart.setOnChartGestureListener(HomeActivity.this);
//    mLineChart.setOnChartValueSelectedListener(HomeActivity.this);


        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);

        ArrayList<Entry> yValues = new ArrayList<>();
        if (yValues.isEmpty()) {
            yValues = getOpeningValuesFromCSV();
        }


        LineDataSet set1 = new LineDataSet(yValues, tv_stockName.getText().toString() + time);
        set1.setFillAlpha(110);
        set1.setColor(Color.RED);
        set1.setLineWidth(3f);

        set1.setValueTextSize(10f);

        set1.setCircleColor(Color.RED);
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setDrawValues(false);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData lineData = new LineData(dataSets);
        mLineChart.setData(lineData);

        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.setGridBackgroundColor(Color.WHITE);
        mLineChart.refreshDrawableState();


    }

    private ArrayList<Entry> getOpeningValuesFromCSV() {
        Log.d(TAG, "getOpeningValuesFromCSV: Called.");
        ArrayList<Entry> result = new ArrayList<>();
        if (!result.isEmpty()) {
            result.clear();
        }
        Log.d(TAG, "getOpeningValuesFromCSV: stockDataList : " + stockDataList.size());
        for (int i = stockDataList.size() - days; i < stockDataList.size(); i++) {
            StockData data = stockDataList.get(i);
            float openingPrice = data.getOpen();
            result.add(new Entry(i - (stockDataList.size() - days), openingPrice));
        }
        return result;

    }

    public void setUpDataOnView() {
        StockData data = stockDataList.get(stockDataList.size() - 1);

        tv_stockOpen.setText(Float.toString(data.getOpen()));
        tv_stockVolume.setText(Integer.toString(data.getVolume()));
    }

    public void updateStockList(String selectedCompany) {
        String table_name = getTableName(selectedCompany);
        Cursor cursor = mydb.getAllData(table_name);
        cursor.moveToFirst();
        if (!stockDataList.isEmpty()) {
            stockDataList.clear();
        }

        while (!cursor.isAfterLast()) {

            StockData data = new StockData();
            data.setDate(cursor.getString(1).toString());
            data.setOpen(cursor.getFloat(2));
            data.setHigh(cursor.getFloat(3));
            data.setLow(cursor.getFloat(4));
            data.setClose(cursor.getFloat(5));
            data.setAdj_close(cursor.getFloat(6));
            data.setVolume(cursor.getInt(7));

            stockDataList.add(data);
            cursor.moveToNext();
        }
    }

    public void setup() {
        Log.d(TAG, "setup: before setup called list size -- " + stockDataList.size());

        Log.d(TAG, "setup: called");
        stockDataList.clear();
        updateStockList(tv_stockName.getText().toString());
        Log.d(TAG, "setup: update done -- " + stockDataList.size());


        setUpGraph();
        setUpDataOnView();


        int total = stockDataList.size() - 1;


        mLineChart.setVisibility(View.GONE);
        mLineChart.setVisibility(View.VISIBLE);
        tv_stockVolume.setVisibility(View.GONE);
        tv_stockVolume.setVisibility(View.VISIBLE);


        //previous closed price
        StockData data_previous_day = stockDataList.get(total - 1);
        float prev_day_open = data_previous_day.getClose();
        tv_previous_close.setText(Float.toString(prev_day_open));

        // setup here
        StockData dataToday = stockDataList.get(stockDataList.size() - 1);
        tv_open_price.setText(Float.toString(dataToday.getOpen()));
        tv_today_low.setText(Float.toString(dataToday.getLow()));
        tv_today_high.setText(Float.toString(dataToday.getHigh()));

        //progress bar todays high low
        pb_todays.setMax(Float.floatToIntBits(dataToday.getHigh()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pb_todays.setMin(Float.floatToIntBits(dataToday.getLow()));
        }
        int progress = Float.floatToIntBits(dataToday.getOpen());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pb_todays.setProgress(progress, true);
        } else {
            pb_todays.setProgress(progress);
        }


        // for 52 week low/high
        float highest = 0;
        float lowest = 10000000;
        for (int i = total - 365; i < total + 1; i++) {
            StockData data = stockDataList.get(i);
            float high = data.getHigh();
            float low = data.getLow();
            if (high > highest) {
                highest = high;
            }
            if (low < lowest) {
                if (low != 0) {
                    lowest = low;
                }

            }
        }
        tv_52_low.setText(Float.toString(lowest));
        tv_52_high.setText(Float.toString(highest));

        // progress bar 52 week
        pb_52_weeks.setMax((int) highest);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pb_52_weeks.setMin((int) lowest);
        }
        int progress_today = Float.floatToIntBits(dataToday.getOpen());
        pb_52_weeks.setProgress(progress_today);


        // for L U band
        float highBand = data_previous_day.getClose() + (float) (data_previous_day.getClose() * 0.10);
        float lowBand = data_previous_day.getClose() - (float) (data_previous_day.getClose() * 0.10);
        tv_lu_high.setText(Float.toString(highBand));
        tv_lu_low.setText(Float.toString(lowBand));
        pb_lu_band_price.setProgress((int) dataToday.getOpen());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pb_lu_band_price.setMin((int) lowBand);
        }
        pb_lu_band_price.setMax((int) highBand);


        //for YTD
        if (!tv_stockName.getText().toString().equals("") || tv_stockName.getText().toString().equals("")) {
            String tableName = getTableName(tv_stockName.getText().toString());
            StockData data_jan_1 = mydb.getData(tableName, "2020-01-01");
            float jan_open = data_jan_1.getOpen();
            float ytd = (dataToday.getOpen() - jan_open) / jan_open;
            tv_ytd.setText(Float.toString(ytd * 100) + "%");

            if (ytd < 0) {
                tv_ytd.setTextColor(Color.RED);
            } else {
                tv_ytd.setTextColor(Color.GREEN);
            }


            // for 1 week
            StockData data_1_week = stockDataList.get(total - 7);
            StockData data_previous_week = stockDataList.get(total - 14);
            float week_open = (data_1_week.getAdj_close() / data_previous_week.getAdj_close()) - 1;
            float week = week_open * 100;

            tv_1_week.setText(String.format("%.2f", week) + "%");


            if (week < 0) {
                tv_1_week.setTextColor(Color.RED);
            } else {
                tv_1_week.setTextColor(Color.GREEN);
            }

            //for 1 month
            StockData data_1_month = stockDataList.get(total - 30);
            StockData data_prev_month = stockDataList.get(total - 60);
            float mont_open = (data_1_month.getAdj_close() / data_prev_month.getAdj_close()) - 1;
            float month = mont_open * 100;
            tv_1_month.setText(String.format("%.2f", month) + "%");
            if (month < 0) {
                tv_1_month.setTextColor(Color.RED);
            } else {
                tv_1_month.setTextColor(Color.GREEN);
            }

            //for 3 month
            StockData data_3_month = stockDataList.get(total - 90);
            StockData data_prev_3_month = stockDataList.get(total - 180);
            float mont3_open = (data_3_month.getAdj_close() / data_prev_3_month.getAdj_close()) - 1;
            float month3 = mont3_open * 100;

            tv_3_month.setText(String.format("%.2f", month3) + "%");
            if (month3 < 0) {
                tv_3_month.setTextColor(Color.RED);
            } else {
                tv_3_month.setTextColor(Color.GREEN);
            }

            // for 6 month
            StockData data_6_month = stockDataList.get(total - 180);
            StockData data_prev_6_month = stockDataList.get(total - 365);
            float month_6_open = (data_6_month.getAdj_close() / data_prev_6_month.getAdj_close()) - 1;
            float month6 = month_6_open * 100;

            tv_6_month.setText(String.format("%.2f", month6) + "%");
            if (month6 < 0) {
                tv_6_month.setTextColor(Color.RED);
            } else {
                tv_6_month.setTextColor(Color.GREEN);
            }

            // for 1 year
            StockData data_1_year = stockDataList.get(total - 365);
            float year_opem = data_1_year.getOpen();
            float year = (dataToday.getOpen() - year_opem) / year_opem;

            tv_1_year.setText(String.format("%.2f", year * 100) + "%");
            if (year < 0) {
                tv_1_year.setTextColor(Color.RED);
            } else {
                tv_1_year.setTextColor(Color.GREEN);
            }

            // for 2 year
            StockData data_2_year = mydb.getData(tableName, "2018-08-13");

            StockData data_prev_2_year = mydb.getData(tableName, "2016-08-16");
            float year2_open = (data_2_year.getAdj_close() / data_prev_2_year.getAdj_close()) - 1;
            float year2 = year2_open * 100;

            tv_2_year.setText(String.format("%.2f", year2) + "%");
            if (year2 < 0) {
                tv_2_year.setTextColor(Color.RED);
            } else {
                tv_2_year.setTextColor(Color.GREEN);
            }

            // for 3 Year
            StockData data_3_year = mydb.getData(tableName, "2017-08-11");
            StockData data_prev_3_year = null;
            if (stockDataList.size() > 2190) {
                data_prev_3_year = stockDataList.get(total - 2190);
            } else {
                data_prev_3_year = stockDataList.get(total - 800);
            }
            Log.d(TAG, "setup: for 3 year: data 3 = " + data_3_year.getAdj_close());
            Log.d(TAG, "setup: for 3 year: data 3 = " + data_3_year.getDate());

            float year3_open = (data_3_year.getAdj_close() / data_prev_3_year.getAdj_close()) - 1;

            float year3 = year3_open * 100;

            tv_3_year.setText(String.format("%.2f", year3) + "%");
            if (year2 < 0) {
                tv_3_year.setTextColor(Color.RED);
            } else {
                tv_3_year.setTextColor(Color.GREEN);
            }


        }

        //for simple moving avg
        //30days
        float sum30 = 0;
        for (int i = total - 30; i < total - 1; i++) {
            StockData data1 = stockDataList.get(i);
            sum30 += data1.getOpen();

        }
        tv_30_day_avg.setText(Float.toString((sum30 / 30)));

        //for 50 days
        float sum50 = 0;
        for (int i = total - 50; i < total - 1; i++) {
            StockData data1 = stockDataList.get(i);
            sum50 += data1.getOpen();

        }
        tv_50_day_avg.setText(Float.toString((sum50 / 50)));

        //for 150 days
        float sum150 = 0;
        for (int i = total - 150; i < total - 1; i++) {
            StockData data1 = stockDataList.get(i);
            sum150 += data1.getOpen();

        }
        tv_150_day_avg.setText(Float.toString((sum150 / 150)));

        //for 200 days
        float sum200 = 0;
        for (int i = total - 200; i < total - 1; i++) {
            StockData data1 = stockDataList.get(i);
            sum200 += data1.getOpen();

        }
        tv_200_day_avg.setText(Float.toString((sum200 / 200)));


    }


    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(authStateListener);
    }


    private void showNotification(String title, String message, String prev_Open) {
        NotificationHelper helper = new NotificationHelper(mContext);
        NotificationCompat.Builder nb = helper.getChannel1Notification(title, message, prev_Open);
        helper.getManager().notify(2, nb.build());
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        toastMessage(text);
        tv_stockName.setText(text);
        checkIfDataExistInDatabase(text);

        setup();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private static class ReadAsyncTask extends AsyncTask<String, Void, String> {
        private WeakReference<HomeActivity> activityWeakReference;
        private static final String TAG = "ReadAsyncTask";

        ReadAsyncTask(HomeActivity activity) {
            activityWeakReference = new WeakReference<HomeActivity>(activity);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: Started.");
            HomeActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.tv_loading.setVisibility(View.VISIBLE);

        }


        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Running....");
            //read csv data and store in database
            HomeActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return "AsyncTask incomplete";
            }

            activity.readDataFormCSV(strings[1]);
            activity.readDataFormCSV(strings[2]);
            activity.readDataFormCSV(strings[3]);
            activity.readDataFormCSV(strings[4]);
            activity.readDataFormCSV(strings[0]);
            activity.setUpGraph();


            Log.d(TAG, "doInBackground: Finished!");
            return "Finished!";

        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            HomeActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();

            activity.tv_loading.setVisibility(View.INVISIBLE);

        }

    }


}
