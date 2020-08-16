package com.example.stockmarketapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText ev_email, ev_password;
    Button b_signIn;
    FirebaseAuth mFirebaseAuth;
    TextView tv_register;
    Context mContext;
    MyDatabaseHelper mydb;

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mydb = new MyDatabaseHelper(this);
        ev_email = findViewById(R.id.editView_email_id_login_page);
        ev_password = findViewById(R.id.editView_password_login_page);
        b_signIn = findViewById(R.id.button_login);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser != null){
                    toastMessage("You are logged in!");
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else
                    toastMessage("Please Login");
            }
        };


        ReadAsyncTask readAsyncTask = new ReadAsyncTask(this);
        readAsyncTask.execute("BSE","NSE","ASHOKLEY","RELIANCE","TATA STEELS","^NSEI", "EICHERMOT.NS"
                ,"^BSESN", "CIPLA.NS");


        b_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ev_email.getText().toString();
                String password = ev_password.getText().toString();
                if(email.isEmpty()){
                    ev_email.setError("Please Enter Email Id!");
                    ev_email.requestFocus();

                }else if(password.isEmpty()){
                    ev_password.setError("Please Enter Password!");
                    ev_password.requestFocus();

                }
                else if(email.isEmpty() || password.isEmpty()){
                    toastMessage("All Fields are Empty.");
                }
                else if(!email.isEmpty() && !password.isEmpty()){
                    mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                toastMessage("Login Error!!!");
                            }
                            else{
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                toastMessage("Please Wait");
                            }
                        }
                    });

                }else{
                    toastMessage("Error occurred!");
                }
            }
        });

        tv_register = findViewById(R.id.tv_registration);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                toastMessage("Please Wait");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseAuth.addAuthStateListener(authStateListener);
    }

    public void toastMessage(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }



    private static class ReadAsyncTask extends AsyncTask<String, Void, String> {
        private WeakReference<LoginActivity> activityWeakReference;
        private static final String TAG = "ReadAsyncTask";

        ReadAsyncTask(LoginActivity activity){
            activityWeakReference = new WeakReference<LoginActivity>(activity);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: Started.");
            LoginActivity activity = activityWeakReference.get();
            if(activity == null || activity.isFinishing()){
                return;
            }


        }



        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Running....");
            //read csv data and store in database
            LoginActivity activity = activityWeakReference.get();
            if(activity == null || activity.isFinishing()){
                return "AsyncTask incomplete";
            }

            activity.readDataFormCSV(strings[1]);
            activity.readDataFormCSV(strings[2]);
            activity.readDataFormCSV(strings[3]);
            activity.readDataFormCSV(strings[4]);
            activity.readDataFormCSV(strings[0]);
            activity.readDataFormCSV(strings[5]);
            activity.readDataFormCSV(strings[6]);
            activity.readDataFormCSV(strings[7]);
            activity.readDataFormCSV(strings[8]);




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
            LoginActivity activity = activityWeakReference.get();
            if(activity == null || activity.isFinishing()){
                return;
            }





        }

    }
    private void readDataFormCSV(String companyName) {



        Log.d(TAG, "readDataFormCSV: Called");
        String tableName = null;
        InputStream inputStream ;
        switch (companyName){
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
                tableName =mydb.TABLE_NAME_EICHER;
                inputStream = getResources().openRawResource(R.raw.eichermot_ns);
                break;
            case "^BSESN":
                tableName = mydb.TABLE_NAME_BSESN;
                inputStream = getResources().openRawResource(R.raw.bsesn);
                break;
            case "CIPLA.NS":
                tableName = mydb.TABLE_NAME_CIPLA;
                inputStream = getResources().openRawResource(R.raw.cipla_ns);
                break;
            default:
                tableName = mydb.TABLE_NAME_CIPLA;
                inputStream = getResources().openRawResource(R.raw.bse_sensex);
                break;
        }

        Cursor cursor = mydb.getAllData(tableName);
        if(cursor.getCount() != 0 || cursor.getCount() >150){
            return;
        }



        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName("UTF-8"))
        );

        String line = null;


        try{
            bufferedReader.readLine();
            while((line = bufferedReader.readLine()) != null){


                // Split line
                String[] tokens = line.split(",");


                // Read Line
                StockData data = new StockData();
                data.setDate(tokens[0]);

                if(tokens[0].equals("null") || tokens[1].equals("null")){
                    data.setOpen(0);
                }else {
                    data.setOpen(Float.parseFloat(tokens[1]));
                }

                if(tokens[0].equals("null") || tokens[2].equals("null")){
                    data.setHigh(0);
                }else {
                    data.setHigh(Float.parseFloat(tokens[2]));
                }

                if(tokens[0].equals("null") || tokens[3].equals("null")){
                    data.setLow(0);
                }else {
                    data.setLow(Float.parseFloat(tokens[3]));
                }
                if(tokens[0].equals("null") || tokens[4].equals("null")){
                    data.setClose(0);
                }else {
                    data.setClose(Float.parseFloat(tokens[4]));
                }
                if(tokens[0].equals("null") || tokens[5].equals("null")){
                    data.setAdj_close(0);
                }else {
                    data.setAdj_close(Float.parseFloat(tokens[5]));
                }
                if(tokens[0].equals("null") || tokens[6].equals("null")){
                    data.setVolume(0);
                }else {
                    data.setVolume(Integer.parseInt(tokens[6]));
                }

                //Store data in list
                boolean result  = mydb.insertData(tableName, data.getDate(), data.getOpen(), data.getHigh(),
                        data.getLow(), data.getClose(), data.getAdj_close(), data.getVolume());

            }

        }catch (IOException e){
            Log.w(TAG, "readDataFormCSV: Error in reading file : "+ line, e );
            e.printStackTrace();
        }

        Log.d(TAG, "readDataFormCSV: Completed.");

    }
}