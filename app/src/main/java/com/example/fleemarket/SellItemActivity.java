package com.example.fleemarket;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SellItemActivity extends AppCompatActivity implements View.OnClickListener {

    protected final int TAKE_PICTURE = 200;
    protected final int PICK_PICTURE = 300;
    protected FBHelper fbHelper;
    protected User currentuser=new User();
    protected ItemForSale itemForSale =new ItemForSale();
    protected ItemForFree itemForfree= new ItemForFree();
    private Uri filePath;
    protected boolean picTaken=false;
    private final int PICK_IMAGE_REQUEST = 71;
    protected final String PIC_FILE_NAME="itempic";
    private String path;
    protected Dialog city_dialog;
    //Firebase
    protected FirebaseStorage storage;
    protected StorageReference storageReference,picReferece;
    protected Boolean isSeller=false;
    protected Button butitemDone,but_itemcamera,but_itemgallery,butDoneCity;
    protected EditText etItemname,ETprise,ETcity;
    protected ImageView iv_itemPhoto;
    protected CheckBox cheIsDelivery,cheIspickup,cheIsPaypal,cheIsCash,cheIsBit;
    protected RatingBar RBitem;
    protected boolean itemgood=false;
    protected Seller seller;
    protected Handler handlerSel,handlerScheack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_item);
        fbHelper=FBHelper.getInstance(this);
        currentuser=fbHelper.getCurrent();
        boolean itemgood=false;
        String path="";
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        butitemDone=findViewById(R.id.butitemDone);
        but_itemcamera=findViewById(R.id.but_itemcamera);
        but_itemgallery=findViewById(R.id.but_itemgallery);

        etItemname=findViewById(R.id.etItemname);
        ETprise=findViewById(R.id.ETprise);

        iv_itemPhoto=findViewById(R.id.iv_itemPhoto);

        cheIsDelivery=findViewById(R.id.cheIsDelivery);
        cheIspickup=findViewById(R.id.cheIspickup);
        cheIsPaypal=findViewById(R.id.cheIsPaypal);
        cheIsCash=findViewById(R.id.cheIsCash);
        cheIsBit=findViewById(R.id.cheIsBit);

        RBitem=findViewById(R.id.RBitem);

        but_itemcamera.setOnClickListener(this);
        but_itemgallery.setOnClickListener(this);
        butitemDone.setOnClickListener(this);
        handlerScheack=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.arg1==FBHelper.DONE_RET_SELLER_IS_SELLER)
                {
                   isSeller= (boolean) msg.obj;
                }
                return true;
            }
        });
        handlerSel=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.arg1==FBHelper.DONE_RET_SELLER) {
                    seller = (Seller) msg.obj;
                }
                return true;
            }
        });
        fbHelper.retrieveDataIsSeller(handlerScheack);
        fbHelper.retrieveDataSellerBiID(handlerSel,fbHelper.getuID(),this);



    }

    @Override
    public void onClick(View v) {
        if (v==butitemDone)
        {
            if (!etItemname.getText().toString().equals("")&&!ETprise.getText().toString().equals("")&&(cheIsDelivery.isChecked()||cheIspickup.isChecked()))
            {
                if (ETprise.getText().toString().equals("0"))
                {
                    itemForfree =new ItemForFree(etItemname.getText().toString(),Integer.parseInt(ETprise.getText().toString()),RBitem.getRating(),cheIsDelivery.isChecked(),cheIspickup.isChecked(),fbHelper.getuID());
                    storageReference = uploadImage();
                    itemForfree.setPicRef(path);
                    fbHelper.saveItemForFreeData(itemForfree);
                    itemgood=true;
                }
                else if ((cheIsPaypal.isChecked()||cheIsCash.isChecked()||cheIsBit.isChecked())&&!itemgood)
                {
                    itemForSale = new ItemForSale(etItemname.getText().toString(), Integer.parseInt(ETprise.getText().toString()), RBitem.getRating(), cheIsDelivery.isChecked(), cheIspickup.isChecked(), cheIsPaypal.isChecked(), cheIsCash.isChecked(), cheIsBit.isChecked(), fbHelper.getuID());
                    storageReference = uploadImage();
                    itemForSale.setPicRef(path);
                    fbHelper.saveItemForSellData(itemForSale);
                    itemgood=true;
                }
            }

            if (itemgood) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                if (!isSeller)
                {
                    createCityDialog();
                }
                else
                    {
                        seller.setOffersCount(seller.getOffersCount() + 1);
                        fbHelper.saveSellerData(seller);
                        startActivity(i);
                        finish();
                    }


            }
            if (!cheIsPaypal.isChecked()&&!cheIsCash.isChecked()&&!cheIsBit.isChecked()&&ETprise.getText().equals(""))
            {
                Toast.makeText(this, "if you wanted to sell for free the payment way wont work", Toast.LENGTH_SHORT).show();
            }


        }
        if (v==butDoneCity)
        {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            seller=new Seller(0,0,ETcity.getText().toString(),fbHelper.getuID(),1);
            fbHelper.saveSellerData(seller);
            startActivity(i);
            finish();
        }
        if (v == but_itemcamera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_PICTURE);
        }
        if (v == but_itemgallery) {
            Intent pickPickIntent = new Intent(Intent.ACTION_PICK);
            File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String picDirPath = picDir.getPath();
            Uri uData = Uri.parse(picDirPath);
            pickPickIntent.setDataAndType(uData, "image/*");
            startActivityForResult(pickPickIntent, PICK_PICTURE);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), filePath);
                iv_itemPhoto.setImageBitmap(bitmap);
                iv_itemPhoto.setBackground(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK)
        // back from camera
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            iv_itemPhoto.setImageBitmap(bitmap);
            iv_itemPhoto.setBackground(null);
            FileHelper.saveBitmapToFile(bitmap,this, PIC_FILE_NAME);
            File tmpFile=new File(getFilesDir()+"/"+PIC_FILE_NAME);
            filePath= Uri.fromFile(tmpFile);
        }

    }

    /**
     *saves pic on Storage in firebase
     * @return StorageReference to pic
     */
    private StorageReference uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            storageReference = storage.getReference();//eror
            path="images/" + UUID.randomUUID().toString();
            final StorageReference ref = storageReference.child(path);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    //Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();
                    // key to store at the FirebaseDatabase
                    String userPic = ref.getPath();
                    //savePicRef(userPic);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            //Toast.makeText(this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
            return ref;
        }
        return null;
    }
    /**
     * Download pic from Storage in firebase to image view
     * @param picRef StorageReference for pic path
     */
    public void upDateImageView(StorageReference picRef)
    {
        final long ONE_MEGABYTE = 1024 * 1024;
        //download file as a byte array
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        bytes, 0, bytes.length);
                if(bitmap!=null) {
                    iv_itemPhoto.setImageBitmap(bitmap);
                    //Toast.makeText(this, "Download successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *update pic on imageview
     */
    public void upDatePicRef(){
        String userRef = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference uRef = FirebaseDatabase.getInstance().getReference();
        uRef = uRef.child(userRef);
        uRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String picStr=dataSnapshot.child("pic").getValue(String.class);
                picReferece = storageReference.child(picStr);
                upDateImageView(picReferece);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /**
     *show dialog to enter a city and save on database in firebase
     */
    public void createCityDialog(){//specific function for lunching Dialog

        city_dialog=new Dialog(this);//sets new Dialog
        city_dialog.setContentView(R.layout.city_dialog);//find id
        city_dialog.setTitle("Login dialog screen");//title
        city_dialog.setCancelable(true);//sets Dialog as Cancelable
        //find by ids
        ETcity=(EditText)city_dialog.findViewById(R.id.ETCity);

        butDoneCity=(Button) city_dialog.findViewById(R.id.butCity);
        //set Listeners for buttons
        butDoneCity.setOnClickListener(this);

        city_dialog.show();//showDialog
    }
}