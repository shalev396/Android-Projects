package com.example.fleemarket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

public class ActivitySellerDetails extends AppCompatActivity implements View.OnClickListener {
    protected ImageView iv_Seller_photo;
    protected TextView TVSellerUsername,TVSellerName,TVSellerPhone,TVSellerSoldCount,TVSellerCity;
    protected RatingBar RBSeller;
    protected Button butBackSeller;
    protected FBHelper fbHelper;
    protected Handler handlers,handleru;
    protected Intent i;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_details);
        fbHelper=FBHelper.getInstance(getApplicationContext());
        iv_Seller_photo=findViewById(R.id.iv_Seller_photo);
        TVSellerUsername=findViewById(R.id.TVSellerUsername);
        TVSellerName=findViewById(R.id.TVSellerName);
        TVSellerPhone=findViewById(R.id.TVSellerPhone);
        TVSellerSoldCount=findViewById(R.id.TVSellerSoldCount);
        TVSellerCity=findViewById(R.id.TVSellerCity);
        RBSeller=findViewById(R.id.RBSeller);
        butBackSeller=findViewById(R.id.butBackSeller);
        butBackSeller.setOnClickListener(this);
        i=getIntent();
        handlers=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.arg1==FBHelper.DONE_RET_SELLER)
                {
                    Seller seller= (Seller) msg.obj;
                    if (seller!=null)
                    {
                        TVSellerSoldCount.setText(TVSellerSoldCount.getText()+""+seller.getSoldCount());
                        TVSellerCity.setText(TVSellerCity.getText()+seller.getCity());
                        float num=seller.getRate()/seller.getSoldCount();
                        RBSeller.setRating(num);

                    }
                    }
                    else
                        {
                            Intent i=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                return true;
        }});
        handleru=new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if (msg.arg1==FBHelper.DONE_RET_DIF_USER)
                        {
                            User user= (User) msg.obj;
                            if (user!=null)
                            {
                                TVSellerName.setText(TVSellerName.getText()+user.getName());
                                TVSellerPhone.setText(TVSellerPhone.getText()+user.getPhone());
                                TVSellerUsername.setText(TVSellerUsername.getText()+user.getUsername());
                            }
                            else
                            {
                                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                        return true;
                    }
                });
        String userId=i.getStringExtra("seller").toString();

        ////////////////////////
        final long ONE_MEGABYTE = 1024 * 1024;
//download file as a byte array

        StorageReference picRef;
        picRef=fbHelper.getStorageReference();

        picRef=picRef.child("images/" +userId);
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        bytes,0, bytes.length);

                if(bitmap!=null) {
                    iv_Seller_photo.setImageBitmap(bitmap);
                    iv_Seller_photo.setBackground(null);
                    Toast.makeText(ActivitySellerDetails.this, "Download successful", Toast.LENGTH_SHORT).show();

                }
            }
        });
        fbHelper.retrieveDataSellerBiID(handlers, userId,this);
        fbHelper.retrieveDataUserByUId(handleru,userId,this);

    }



    @Override
    public void onClick(View v) {
        if (v==butBackSeller)
        {
            finish();
        }
    }

}