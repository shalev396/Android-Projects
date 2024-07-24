package com.example.fleemarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth auth;
    protected Button butregister;
    protected EditText etemail,etpassword,etconPassword;
    protected FBHelper fbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //fbHelper=FBHelper.getInstance(getApplicationContext());
        auth=FirebaseAuth.getInstance();
        butregister=findViewById(R.id.butDone);
        butregister.setOnClickListener(this);
        etemail=findViewById(R.id.ETemail);
        etpassword=findViewById(R.id.ETpassword);
        etconPassword=findViewById(R.id.etconpassword);


    }

    @Override
    public void onClick(View v) {
        if (v==butregister)
        {
            if (!etemail.getText().toString().equals("")||!etpassword.getText().toString().equals("")||!etconPassword.getText().toString().equals("")) {
                if (etpassword.getText().toString().equals(etconPassword.getText().toString())){
                String email = etemail.getText().toString();
                String passw = etpassword.getText().toString();
                creatAcount(email, passw);
//                fbHelper.setFinished("0");

                }   else
                    {
                        Toast.makeText(Register.this, "passwords are not match", Toast.LENGTH_SHORT).show();

                    }
            }
            else{
                Toast.makeText(Register.this, "make sure you filled every slot", Toast.LENGTH_SHORT).show();
            }
        }



    }

    /**
     * make new account with the email and password
     * and log the user in the app
     * @param email used to create account
     * @param passw used to create account
     */
    private void creatAcount(String email,String passw){
        auth.createUserWithEmailAndPassword(email,passw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                            //updateUserProfile(task.getResult().getUser());
                            Intent i=new Intent(getApplicationContext(), UserDetails.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Authentication fail" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            task.getException().printStackTrace();
                        }
                    }
                });
    }
}