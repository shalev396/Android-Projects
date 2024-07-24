package com.example.fleemarket;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.util.ArrayList;

public class UserDetails extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth auth;
    protected Button butDone,butCamera, butGallery;
    protected EditText etname,etLast,etPhone,ETusername;
    protected ImageView iv1;
    protected final int TAKE_PICTURE = 200;
    protected final int PICK_PICTURE = 300;
    protected FBHelper fbHelper;
    protected User currentuser=new User();
    private Uri filePath;
    protected boolean picTaken=false;
    //Firebase
    protected  FirebaseStorage storage;
    protected  StorageReference storageReference,picReferece;
    protected ArrayList<String> permissionsAll=new ArrayList<String>(),permissionsNotGranted=new ArrayList<String>();
    protected int REQUEST_GROUP_PERMIT=101;
    protected User user;
    private final int PICK_IMAGE_REQUEST = 71;
    protected final String PIC_FILE_NAME="userpic";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        //firebase
        fbHelper = FBHelper.getInstance(getApplicationContext());
        auth=FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //upDatePicRef();
        //ET,but
        butDone=findViewById(R.id.butDone);
        butDone.setOnClickListener(this);
        etname=findViewById(R.id.ETname);
        etLast=findViewById(R.id.etLast);
        etPhone=findViewById(R.id.etphone);
        ETusername=findViewById(R.id.ETusername);
        butCamera = (Button) findViewById(R.id.but_camera);
        butGallery = (Button) findViewById(R.id.but_gallery);
        iv1 = findViewById(R.id.iv_photo);
        butCamera.setOnClickListener(this);
        butGallery.setOnClickListener(this);

        //permissions
        permissionsAll.add(Manifest.permission.INTERNET);
        permissionsAll.add(Manifest.permission.READ_PHONE_STATE);
        permissionsAll.add(Manifest.permission.CAMERA);
        permissionsAll.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        for (int i = 0; i <permissionsAll.size() ; i++) {
            if (ContextCompat.checkSelfPermission(this, permissionsAll.get(i)) != PackageManager.PERMISSION_GRANTED){
                permissionsNotGranted.add(permissionsAll.get(i));
            }
        }
        requestGroupPermissions(permissionsAll);

        user=fbHelper.getCurrent();
        ETusername.setText(user.getUsername());
        etname.setText(user.getName());
        etPhone.setText(user.getPhone());
        etLast.setText(user.getLname());

        final long ONE_MEGABYTE = 1024 * 1024;
//download file as a byte array



        StorageReference picRef;

        picRef=this.fbHelper.getStorageReference();
        picRef=picRef.child("images/" + fbHelper.getuID());
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        bytes,0, bytes.length);
                if(bitmap!=null) {
                    iv1.setImageBitmap(bitmap);
                    iv1.setBackground(null);
                    //Toast.makeText(this, "Download successful", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**request Permissions as a permission Group
     * @param permissions ArrayList<String> that have all the permissions
     */
    public void requestGroupPermissions(ArrayList<String> permissions){
        if(permissions!=null&& permissions.size()>0) {
            String[] permissionGroup = new String[permissions.size()];
            permissions.toArray(permissionGroup);
            ActivityCompat.requestPermissions(this, permissionGroup,REQUEST_GROUP_PERMIT);
        }}

    /**check if Permissions are GRANTED
     * @param requestCode
     * @param permissions ArrayList<String> that have all the permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_GROUP_PERMIT){
            for(int i=0;i<permissions.length;i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    //  tvList.get(i).setText("Yes");
                }
                else {
                    //  tvList.get(i).setText("No");
                    Context context =this;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("one or more of the Permissions not approved.");
                    builder.setMessage("the appliction needs all of the premition to work. "+ "\n"+"go to settings->flee market->Permissions and make sure all of the Permissions approved.");// ההודעה גוף
                    builder.setCancelable(false);
                    builder.setNegativeButton("not today",new UserDetails.DialogListner(context));
                    builder.setPositiveButton("yes i will do it", new UserDetails.DialogListner(context));
                    AlertDialog dialog=builder.create();
                    dialog.show();

                }}}}
    //permissions end
    class DialogListner implements DialogInterface.OnClickListener {
        Context context;

        public DialogListner(Context context) {
            this.context = context;
        }
        public void onClick(DialogInterface dialog, int which) {
            if(which== AlertDialog.BUTTON_POSITIVE) {
                Toast.makeText(context, "You agreed :)", Toast.LENGTH_SHORT).show();
                requestGroupPermissions(permissionsAll);
            }
            else if (which== AlertDialog.BUTTON_NEGATIVE)
            {
                Toast.makeText(context,"You didn't agreed :(" , Toast.LENGTH_SHORT).show();
                finish();
            }


        }}

    @Override
    public void onClick(View v) {
        if (v==butDone)
        {
            if (!etname.getText().toString().equals("")&&!ETusername.getText().toString().equals("")&&!etLast.getText().toString().equals("")&&!etPhone.getText().toString().equals(""))
            {
                //save
            String username=ETusername.getText().toString();
            String firstName =etname.getText().toString();
            String lastName=etLast.getText().toString();
            String phoneNum=etPhone.getText().toString();
            currentuser=new User(username,firstName,lastName,phoneNum);
                fbHelper.saveUserData(currentuser);
            storageReference = uploadImage();
            Intent i=new Intent(this, MainActivity.class);
                startActivity(i);
        }
            else
                {
                    Toast.makeText(this, "make sure you filled every slot and check if the passwords match", Toast.LENGTH_SHORT).show();
                }
        }
        //photo
        if (v == butCamera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_PICTURE);
        }
        if (v == butGallery) {
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
                iv1.setImageBitmap(bitmap);
                iv1.setBackground(null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK)
        // back from camera
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            iv1.setImageBitmap(bitmap);
            iv1.setBackground(null);
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
            storageReference = storage.getReference();
            //final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            final StorageReference ref = storageReference.child("images/" + fbHelper.getuID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    //Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();
                    // key to store at the FirebaseDatabase
                    String userPic = ref.getPath();
                    savePicRef(userPic);
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
            iv1.setImageBitmap(bitmap);
        //Toast.makeText(this, "Download successful!", Toast.LENGTH_SHORT).show();
        }
        }
        });
    }
    /**
     *update pic on imageview
     */
    public void savePicRef(String picRef){
        String userRef = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef = dbRef.child("Users").child(userRef).child("pic");
        dbRef.setValue(picRef);
    }
    /**
     *show dialog to enter a city and save on database in firebase
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


}}

