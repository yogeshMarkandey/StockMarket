package com.example.stockmarketapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText ev_emailId, ev_password;
    Button b_signUp;
    TextView tv_signIn;

    FirebaseAuth mFirebaseAuth;

    Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        mFirebaseAuth = FirebaseAuth.getInstance();
        ev_emailId = findViewById(R.id.editText_email_registration);
        ev_password = findViewById(R.id.editText_password_registration);
        b_signUp = findViewById(R.id.button_SignUp);
        tv_signIn = findViewById(R.id.textView_sign_in);





       b_signUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String email = ev_emailId.getText().toString();
               String password = ev_password.getText().toString();
               if(email.isEmpty()){
                   ev_emailId.setError("Please Enter Email Id!");
                   ev_emailId.requestFocus();

               }else if(password.isEmpty()){
                   ev_password.setError("Please Enter Password!");
                   ev_password.requestFocus();

               }
               else if(email.isEmpty() || password.isEmpty()){
                   toastMessage("All Fields are Empty.");
               }
               else if(!email.isEmpty() && !password.isEmpty()){
                   mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(!task.isSuccessful()){
                                toastMessage("SignUp Unsuccessful, Please try again.");
                           }else{

                               toastMessage("SignUp Successful.");
                               Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                               startActivity(intent);


                           }
                       }
                   });

               }else{
                   toastMessage("Error occurred!");
               }

           }
       });


       tv_signIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, LoginActivity.class);
               startActivity(intent);
               toastMessage("Please Wait");
           }
       });
    }


    public void toastMessage(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}