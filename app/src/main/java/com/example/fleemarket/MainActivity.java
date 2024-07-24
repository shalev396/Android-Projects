package com.example.fleemarket;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    protected FBHelper fbHelper;
    protected Handler handleru,handleri,handlerBI;
    protected Button butEditUser,but_forsell,but_search,butlogout;
    protected TextView TV_name;
    protected EditText ETSearch;
    protected ListView listvItems;
    protected ImageView iv_userPic;
    protected User currentuser;
    protected StorageReference picRef;
    protected ArrayList<String> permissionsAll=new ArrayList<String>(),permissionsNotGranted=new ArrayList<String>();
    protected int REQUEST_GROUP_PERMIT=101;
    protected ArrayList<Item> itemsList, itemsListsearches,itemssortlist,ItemsListBought;
    protected ItemAdapter itemAdapter;
    protected boolean pause;
    public static boolean IS_BOUGHT=false;
    protected CallListen callListen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent j = new Intent(MainActivity.this,Srvmusic.class);
        startService(j);
        callListen=new CallListen();

        fbHelper=FBHelper.getInstance(this);
        handleru=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.arg1==FBHelper.DONE_RET_USER)
                {
                    User user= (User) msg.obj;
                    if (user!=null)
                    {
                        if (!user.detailsOk())
                        {
                            Intent i=new Intent(getApplicationContext(),UserDetails.class);
                            startActivity(i);
                            finish();
                            //fill detail,finish()
                        }
                        else {
                            TV_name.setText(user.getUsername());
                            fbHelper.setUser(user);
                        }
                    }
                    else
                    {
                        Intent i=new Intent(getApplicationContext(),UserDetails.class);
                        startActivity(i);
                        finish();

                    }
                }
                return true;
            }
        });
        //items
        handleri=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.arg1==FBHelper.DONE_RET_ITEMS_LIST)
                {
                    itemsList=fbHelper.getItemsList();
                    //itemsList.add(new Item("if its first list=null","null",(float) 0.5,false,false,false,false,false,"0"));

                    itemAdapter=new ItemAdapter(MainActivity.this,0,itemsList,fbHelper);
                    listvItems.setAdapter(itemAdapter);
                }
                return true;
            }
        });
        handlerBI=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.arg1==FBHelper.DONE_RET_ITEMS_LIST_BOUGHT)
                {
                    ItemsListBought=fbHelper.getBoughtList();
                    //itemsList.add(new Item("if its first list=null","null",(float) 0.5,false,false,false,false,false,"0"));


                }
                return true;
            }
        });
        itemssortlist=new ArrayList<>();
        ItemsListBought=new ArrayList<>();
        currentuser=fbHelper.getCurrent();
        butlogout=findViewById(R.id.butLogOut);
        iv_userPic=findViewById(R.id.iv_userPic);
        TV_name=findViewById(R.id.TV_name);
        butEditUser=findViewById(R.id.butEditUser);
        but_forsell=findViewById(R.id.but_forsell);
        ETSearch=findViewById(R.id.ETSearch);
        but_search=findViewById(R.id.but_search);
        listvItems=findViewById(R.id.listvItems);
        butEditUser.setOnClickListener(this);
        but_forsell.setOnClickListener(this);
        but_search.setOnClickListener(this);
        butlogout.setOnClickListener(this);


        final long ONE_MEGABYTE = 1024 * 1024;
//download file as a byte array
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef = dbRef.child("Users").child(fbHelper.getuID());
        picRef=fbHelper.getStorageReference();
        picRef=picRef.child("images/" + fbHelper.getuID().toString());
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new
        OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        bytes,0, bytes.length);
                if(bitmap!=null) {
                    iv_userPic.setImageBitmap(bitmap);
                    iv_userPic.setBackground(null);
                    //Toast.makeText(this, "Download successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
//permissions
        permissionsAll.add(Manifest.permission.INTERNET);
        //permissionsAll.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        permissionsAll.add(Manifest.permission.CAMERA);
        permissionsAll.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsAll.add(Manifest.permission.READ_PHONE_STATE);

        for (int i = 0; i <permissionsAll.size() ; i++) {
            if (ContextCompat.checkSelfPermission(this, permissionsAll.get(i)) != PackageManager.PERMISSION_GRANTED){
                permissionsNotGranted.add(permissionsAll.get(i));
            }
        }
        requestGroupPermissions(permissionsAll);
        fbHelper.retrieveDataUser(handleru);

        ///////////items
        fbHelper.retrieveDataBoughtItems(handlerBI);
        fbHelper.retrieveDataItems(handleri,MainActivity.this);
        itemsListsearches =new ArrayList<Item>();

        listvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (!IS_BOUGHT)
                {
                Intent i = new Intent(getApplicationContext(), BuyItemActivity.class);
                i.putExtra("item",itemsList.get(position).getItemId().toString());
                startActivity(i);
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
                    builder.setNegativeButton("not today",new MainActivity.DialogListner(context));
                    builder.setPositiveButton("yes i will do it", new MainActivity.DialogListner(context));
                    AlertDialog dialog=builder.create();
                    dialog.show();

                }}}}
    //permissions end

    /**
     * class DialogListner to show this dialog when needed
     */
    class DialogListner implements DialogInterface.OnClickListener {
        Context context;

        public DialogListner(Context context) {
            this.context = context;
        }
        public void onClick(DialogInterface dialog, int which) {
            if(which== AlertDialog.BUTTON_POSITIVE) {
                Toast.makeText(context, "You agreed ", Toast.LENGTH_SHORT).show();//checked by google translate
                requestGroupPermissions(permissionsAll);
            }
            else if (which== AlertDialog.BUTTON_NEGATIVE)
            {
                Toast.makeText(context,"You didn't agreed :(" , Toast.LENGTH_SHORT).show();//checked by google translate
                finish();
            }


        }}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.sort_menu,menu);
        return true;
    }
        public boolean onOptionsItemSelected(MenuItem item)
        {
            switch(item.getItemId()) {
                case R.id.mnItemsortHL:
                    IS_BOUGHT=false;
                    itemssortlist.clear();
                    Toast.makeText(this,"sorting by price high to low", Toast.LENGTH_LONG).show();
                    //add sort
                    for (int i = 0; i < itemsList.size(); i++)
                    {
                        for (int j = i + 1; j < itemsList.size(); j++)
                        {
                            Item tmp =null;
                            if (itemsList.get(i).getPrice() < itemsList.get(j).getPrice())
                            {
                                tmp = (Item) itemsList.get(i);
                                itemsList.set(i, itemsList.get(j));
                                itemsList.set(j, tmp);

                            }}}
                    itemAdapter=new ItemAdapter(MainActivity.this,0,itemsList,fbHelper);
                    listvItems.setAdapter(itemAdapter);
                    break;
                case R.id.mnItemsortLH:
                    IS_BOUGHT=false;
                    itemssortlist.clear();

                    Toast.makeText(this,"sorting by price low to high", Toast.LENGTH_LONG).show();
                    //add sort
                    for (int i = 0; i < itemsList.size(); i++)
                    {
                        for (int j = i + 1; j < itemsList.size(); j++)
                        {
                            Item tmp =null;
                            if (itemsList.get(i).getPrice() >itemsList.get(j).getPrice())
                            {
                                tmp = (Item) itemsList.get(i);
                                itemsList.set(i, itemsList.get(j));
                                itemsList.set(j, tmp);

                            }}}
                    itemAdapter=new ItemAdapter(MainActivity.this,0,itemsList,fbHelper);
                    listvItems.setAdapter(itemAdapter);
                    break;
                case R.id.mnItemsortFree:
                    IS_BOUGHT=false;
                    itemssortlist.clear();
                    Toast.makeText(this,"sorting by only items for free", Toast.LENGTH_LONG).show();
                    //add sort
                    for (int i = 0; i < itemsList.size(); i++)
                    {
                            if (itemsList.get(i).getPrice()==0)
                            {
                                itemssortlist.add(itemsList.get(i));
                            }
                    }
                    itemAdapter=new ItemAdapter(MainActivity.this,0,itemssortlist,fbHelper);
                    listvItems.setAdapter(itemAdapter);
                    break;
                case R.id.mnItemsorsell:
                    IS_BOUGHT=false;
                    itemssortlist.clear();
                    Toast.makeText(this,"sorting by only items for sell", Toast.LENGTH_LONG).show();
                    //add sort
                    for (int i = 0; i < itemsList.size(); i++)
                    {
                        if (itemsList.get(i).getPrice()!=0)
                        {
                            itemssortlist.add(itemsList.get(i));
                        }
                    }
                    itemAdapter=new ItemAdapter(MainActivity.this,0,itemssortlist,fbHelper);
                    listvItems.setAdapter(itemAdapter);
                    break;
                case R.id.mnItemBought:
                    itemssortlist.clear();
                    Toast.makeText(this,"sorting by only your purchases", Toast.LENGTH_LONG).show();
                    //to do
                    IS_BOUGHT=true;
                        itemAdapter=new ItemAdapter(MainActivity.this,0,ItemsListBought,fbHelper);
                        listvItems.setAdapter(itemAdapter);
                        listvItems.setClickable(false);
                        break;
                case R.id.mnReset:
                    itemAdapter=new ItemAdapter(MainActivity.this,0,itemsList,fbHelper);
                    listvItems.setAdapter(itemAdapter);
                    IS_BOUGHT=false;
                    listvItems.setClickable(true);
                    fbHelper.retrieveDataItems(handleri,MainActivity.this);
                    upDateImageView(fbHelper.picRefUid());
                    ETSearch.setText("");
                    break;




            }
            return true;
        }

    /**
     * Download pic from Storage in firebase to image view
     * @param picRef StorageReference for pic path
     */
    public void upDateImageView(StorageReference picRef) {
        final long ONE_MEGABYTE = 1024 * 1024;
        //download file as a byte array
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        bytes, 0, bytes.length);
                if (bitmap != null) {
                    iv_userPic.setImageBitmap(bitmap);
                    //Toast.makeText(this, "Download successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v == butEditUser) {
            Intent i = new Intent(getApplicationContext(), UserDetails.class);
            startActivity(i);
        }
        if (v == but_search) {
            String text = ETSearch.getText().toString();
            itemsListsearches =new ArrayList<Item>();
            //add search
            //to do add by users
            boolean found=false;
            for (int i = 0; i < itemsList.size(); i++) {
                Item tmp = itemsList.get(i);

                    if (tmp.getName().equals(text)) {
                        itemsListsearches.add(tmp);
                        found = true;
                    }
                }
                if (found) {
                    Toast.makeText(this,"search: "+text, Toast.LENGTH_LONG).show();
                    itemAdapter = new ItemAdapter(MainActivity.this, 0, itemsListsearches, fbHelper);
                    listvItems.setAdapter(itemAdapter);
                }
                else
                    {
                        Toast.makeText(this,"search for: '"+text+"' not found", Toast.LENGTH_LONG).show();
                        itemAdapter = new ItemAdapter(MainActivity.this, 0, itemsList, fbHelper);
                        listvItems.setAdapter(itemAdapter);
                    }

        }
        if (v == but_forsell) {
            Intent i = new Intent(getApplicationContext(), SellItemActivity.class);
            startActivity(i);
        }
        if (v == butlogout) {
            fbHelper.fbLogout();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            TV_name.setText("");
            finish();



        }

    }
    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        pause=true;
        Intent i=new Intent(MainActivity.this,Srvmusic.class);
        stopService(i);
        callListen.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pause=false;
        Intent i=new Intent(MainActivity.this,Srvmusic.class);

        startService(i);
        callListen.start();
    }



}