package com.example.fleemarket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    protected EditText ETemail,ETpassword;
    protected Button butlogin,butReg;
    protected Animation translate1;
    protected int wromgCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        translate1= AnimationUtils.loadAnimation(this,R.anim.anim1);
        translate1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                butlogin.setClickable(false);
                Toast.makeText(LoginActivity.this, "wait 5 seconds to try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                butlogin.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        wromgCount=0;
        auth = FirebaseAuth.getInstance();
        butReg = findViewById(R.id.butReg);
        butlogin = findViewById(R.id.butlogin);
        ETemail = findViewById(R.id.ETemail);
        ETpassword = findViewById(R.id.ETpassword);
        butlogin.setOnClickListener(this);
        butReg.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v==butReg)
        {
            Intent i=new Intent(this,Register.class);
            startActivity(i);
            //finish();
        }

        if (v==butlogin)
        {
            if (!ETemail.getText().toString().equals("")||!ETpassword.getText().toString().equals("")) {
                String email = ETemail.getText().toString();
                String password = ETpassword.getText().toString();
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();

                                    Toast.makeText(LoginActivity.this, "Authentication good.", Toast.LENGTH_SHORT).show();
//                                    fbHelper.retrieveData2();dif act

                                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                                       startActivity(i);
                                       finish();
//                                    }
                                    //updateUI(user);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                    wromgCount++;
                                    if (wromgCount>3)
                                    {
                                        butlogin.startAnimation(translate1);
                                    }


                                }

                                // ...
                            }
                        });
            }
            else
                {
                    Toast.makeText(LoginActivity.this, "email or password slots are empty.", Toast.LENGTH_SHORT).show();
                }

        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null)
            auth.removeAuthStateListener(authStateListener);
    }
    @Override
    public void finish() {
        super.finish();
    }

    /**
     * auto signin user by email and password
     * cheack if at FirebaseAuth if email and password still correct
     * @param email that user entered
     * @param passw that user entered
     */
    private void signIn(String email, String passw) {
        auth.signInWithEmailAndPassword(email,passw).addOnCompleteListener(
                LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "signIn Successful.",
                                    Toast.LENGTH_SHORT).show();
                            FBHelper fbHelper = FBHelper.getInstance(getApplicationContext());
                            fbHelper.setCurrentUser(auth.getCurrentUser());
                            //Intent intent = new Intent(getBaseContext(), MainActivity.class);
                           // startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "signIn failed."
                                            + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            task.getException().printStackTrace();
                        }
                    }
                });
    }
    //private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener=new
            FirebaseAuth.AuthStateListener() {
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    if(user!=null)
                    {
                        //del
                        //FirebaseAuth.getInstance().signOut();
                        Toast.makeText(LoginActivity.this, "user is signed in.", Toast.LENGTH_SHORT).show();

                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                        finish();




                    }
                    else
                    {//user signed out
                        Toast.makeText(LoginActivity.this, "user signed out.", Toast.LENGTH_SHORT).show();
                    }
                }
            };
}
