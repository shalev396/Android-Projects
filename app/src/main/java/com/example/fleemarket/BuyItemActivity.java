package com.example.fleemarket;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

public class BuyItemActivity extends AppCompatActivity implements View.OnClickListener {
    protected ItemForSale selling;
    protected String itemid;
    protected Handler handleri,handlerSe;
    protected TextView tvItemname,TVprise;
    protected ImageView iv_itemPhoto,ivicon_pay;
    protected TextView tvDeliveryBuy,tvMid,tvPickUpBuy,tvPaypalBuy,midLeft,tvCashBuy,midRight,tvBitBuy,choosepay;
    protected CheckBox cheIsDeliveryBuy,cheIsPickUpBuy,cheIsPaypalBuy,cheIsCashBuy,cheIsBitBuy;
    protected RatingBar RBitem;
    protected FBHelper fbHelper;
    protected Intent i;
    protected Button butitemDonebuy,ButSeller,butRateSeller;
    protected ItemForFree buyItemForfree =new ItemForFree();
    protected ItemForSale buyItemForsale =new ItemForSale();
    protected Item item=new Item();
    protected boolean isFree=false;
    protected Seller seller;
    protected Dialog rateD;
    protected RatingBar RBSeller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_item);
        fbHelper=FBHelper.getInstance(this);
        i=getIntent();

        itemid=i.getStringExtra("item").toString();
        handleri=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.arg1==FBHelper.DONE_RET_ITEM)
                {
                    Item buyItem = (Item) msg.obj;
                    item=buyItem;
                    fbHelper.retrieveDataSellerBiID(handlerSe,item.getSellerUID(),BuyItemActivity.this);
                    if (buyItem.getPrice()==0)
                    {
                        buyItemForfree=(ItemForFree) msg.obj;
                        isFree=true;
                    }
                    else
                        {
                            buyItemForsale=(ItemForSale) msg.obj;
                        }

                    if (isFree)
                    {
                        tvItemname.setText(buyItemForfree.getName());
                        TVprise.setText(buyItemForfree.getPrice()+"$");
                        RBitem.setRating(buyItemForfree.getRate());
                        ivicon_pay.setVisibility(View.INVISIBLE);
                        if (!buyItemForfree.isDelivery()&& buyItemForfree.isPickup())
                        {
                            tvDeliveryBuy.setAlpha(0);
                            tvDeliveryBuy.setText("");
                            cheIsDeliveryBuy.setAlpha(0);
                            cheIsDeliveryBuy.setClickable(false);
                            tvMid.setAlpha(0);
                        }
                        if (buyItemForfree.isDelivery()&&!buyItemForfree.isPickup())
                        {
                            tvPickUpBuy.setAlpha(0);
                            tvPickUpBuy.setText("");
                            cheIsPickUpBuy.setAlpha(0);
                            cheIsPickUpBuy.setClickable(false);
                            tvMid.setAlpha(0);
                        }
                        cheIsPaypalBuy.setAlpha(0);
                        tvPaypalBuy.setAlpha(0);

                        cheIsPaypalBuy.setClickable(false);
                        cheIsBitBuy.setAlpha(0);
                        tvBitBuy.setAlpha(0);

                        cheIsBitBuy.setClickable(false);
                        cheIsCashBuy.setAlpha(0);
                        tvCashBuy.setAlpha(0);
                        cheIsCashBuy.setClickable(false);
                        choosepay.setAlpha(0);
                        midLeft.setAlpha(0);
                        midRight.setAlpha(0);


                    }
                    else {

                        tvItemname.setText(buyItemForsale.getName());
                        TVprise.setText(buyItemForsale.getPrice() + "$");
                        RBitem.setRating(buyItemForsale.getRate());
                        if (!buyItemForsale.isDelivery() && buyItemForsale.isPickup()) {
                            tvDeliveryBuy.setAlpha(0);
                            tvDeliveryBuy.setText("");
                            cheIsDeliveryBuy.setAlpha(0);
                            cheIsDeliveryBuy.setClickable(false);
                            tvMid.setAlpha(0);
                        }
                        if (buyItemForsale.isDelivery() && !buyItemForsale.isPickup()) {
                            tvPickUpBuy.setAlpha(0);
                            tvPickUpBuy.setText("");
                            cheIsPickUpBuy.setAlpha(0);
                            cheIsPickUpBuy.setClickable(false);
                            tvMid.setAlpha(0);
                        }
                        //
                        if (!buyItemForsale.isPayPal() && buyItemForsale.isCash() && buyItemForsale.isBit()) {

                        }
                        if (!buyItemForsale.isPayPal()) {
                            tvPaypalBuy.setAlpha(0);
                            tvPaypalBuy.setText("");
                            cheIsPaypalBuy.setAlpha(0);
                            cheIsPaypalBuy.setClickable(false);
                             midLeft.setAlpha(0);
                        }
                        if (!buyItemForsale.isBit())
                        {
                            tvBitBuy.setAlpha(0);
                            tvBitBuy.setText("");
                            cheIsBitBuy.setAlpha(0);
                            cheIsBitBuy.setClickable(false);
                            midRight.setAlpha(0);
                        }
                        if (!buyItemForsale.isCash()&&buyItemForsale.isPayPal())
                        {
                            midLeft.setAlpha(0);
                            tvCashBuy.setAlpha(0);
                            tvCashBuy.setText("");
                            cheIsCashBuy.setClickable(false);
                            cheIsCashBuy.setAlpha(0);
                        }
                        if (!buyItemForsale.isCash()&&buyItemForsale.isBit())
                        {
                            midRight.setAlpha(0);
                            tvCashBuy.setAlpha(0);
                            tvCashBuy.setText("");
                            cheIsCashBuy.setClickable(false);
                            cheIsCashBuy.setAlpha(0);
                        }
                    }

                    final long ONE_MEGABYTE = 1024 * 1024;
//download file as a byte array

                    StorageReference picRef;
                    picRef=fbHelper.getStorageReference();
                    picRef=picRef.child(item.getPicRef());
                    picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(
                                    bytes,0, bytes.length);
                            if(bitmap!=null) {
                                iv_itemPhoto.setImageBitmap(bitmap);
                                iv_itemPhoto.setBackground(null);
                                //Toast.makeText(this, "Download successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }
                return true;
            }
        }) ;
        tvItemname=findViewById(R.id.tvItemname);
        TVprise=findViewById(R.id.TVprise);

        tvDeliveryBuy=findViewById(R.id.tvDeliveryBuy);
        tvMid=findViewById(R.id.tvMid);
        tvPickUpBuy=findViewById(R.id.tvPickUpBuy);

        choosepay=findViewById(R.id.choosepay);
        tvPaypalBuy=findViewById(R.id.tvPaypalBuy);
        midLeft=findViewById(R.id.midLeft);
        tvCashBuy=findViewById(R.id.tvCashBuy);
        midRight=findViewById(R.id.midRight);
        tvBitBuy=findViewById(R.id.tvBitBuy);

        cheIsDeliveryBuy=findViewById(R.id.cheIsDeliveryBuy);
        cheIsPickUpBuy=findViewById(R.id.cheIsPickUpBuy);
        cheIsPaypalBuy=findViewById(R.id.cheIsPaypalBuy);
        cheIsCashBuy=findViewById(R.id.cheIsCashBuy);
        cheIsBitBuy=findViewById(R.id.cheIsBitBuy);

        RBitem=findViewById(R.id.RBitem);

        butitemDonebuy=findViewById(R.id.butitemDonebuy);
        ButSeller=findViewById(R.id.butSeller);

        iv_itemPhoto=findViewById(R.id.iv_itemPhoto);
        ivicon_pay=findViewById(R.id.ivicon_pay);

        fbHelper.retrieveDataItemById(handleri,itemid);

        butitemDonebuy.setOnClickListener(this);
        ButSeller.setOnClickListener(this);


        handlerSe=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.arg1==FBHelper.DONE_RET_SELLER) {
                    seller = (Seller) msg.obj;
                }
                return true;
            }
        });







    }


    class DialogListnerbuy implements DialogInterface.OnClickListener {
        protected Context context;

        /**
         * create class to show Dialog that confirm buy
         */
        public DialogListnerbuy(Context context) {
            this.context = context;
        }
        public void onClick(DialogInterface dialog, int which) {
            if(which== AlertDialog.BUTTON_POSITIVE) {
                Toast.makeText(context, "Sold", Toast.LENGTH_SHORT).show();//checked by google translate



                seller.setSoldCount(seller.getSoldCount()+1);
                createRateDialog();


            }
            else if (which== AlertDialog.BUTTON_NEGATIVE)
            {
                Toast.makeText(context,"Maybe next time" , Toast.LENGTH_SHORT).show();//checked by google translate

            }


        }}

    @Override
    public void onClick(View v) {
        if (v==butRateSeller)
        {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            if (seller.getSoldCount()==0)
            {
                seller.setRate(RBSeller.getRating());
            }
            else {
                seller.setRate((seller.getRate()+RBSeller.getRating())/2);
            }

            fbHelper.saveSellerData(seller);
            fbHelper.saveItemForUser(item);
            fbHelper.deleteItemForUser(item);
            startActivity(i);
            finish();
        }
        if (v==ButSeller)
        {
            Intent i = new Intent(getApplicationContext(), ActivitySellerDetails.class);
            i.putExtra("seller",item.getSellerUID());
            startActivity(i);
        }
        if (v==butitemDonebuy) {
            String msg = "";
            if ((!cheIsDeliveryBuy.isChecked()&&cheIsPickUpBuy.isChecked())||(!cheIsPickUpBuy.isChecked()&&cheIsDeliveryBuy.isChecked())) {
                if (isFree){

                    Context context = this;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("are you sure you want to buy this item.");
                    if (cheIsPickUpBuy.isChecked()) {
                        msg = msg + "you choose to pickup \n";
                    }
                    if (cheIsDeliveryBuy.isChecked()) {
                        msg = msg + "you choose to get delivery \n";
                    }
                    builder.setMessage(msg);// ההודעה גוף
                    builder.setCancelable(false);
                    builder.setNegativeButton("nope", new DialogListnerbuy(context));
                    builder.setPositiveButton("yes ", new DialogListnerbuy(context));
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

                else if ((!cheIsPaypalBuy.isChecked()&&!cheIsCashBuy.isChecked()&&cheIsBitBuy.isChecked())||(cheIsPaypalBuy.isChecked()&&!cheIsCashBuy.isChecked()&&!cheIsBitBuy.isChecked())||(!cheIsPaypalBuy.isChecked()&&cheIsCashBuy.isChecked()&&!cheIsBitBuy.isChecked())) {
                    Context context = this;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("are you sure you want to buy this item.");

                    if (cheIsPickUpBuy.isChecked()) {
                        msg = msg + "you choose to pickup \n";
                    }
                    if (cheIsDeliveryBuy.isChecked()) {
                        msg = msg + "you choose to get delivery \n";
                    }
                    ///
                    if (cheIsPaypalBuy.isChecked()) {
                        msg = msg + "you choose to pay with PayPal \n";
                    }
                    if (cheIsBitBuy.isChecked()) {
                        msg = msg + "you choose to pay with Bit \n";
                    }
                    if (cheIsCashBuy.isChecked()) {
                        msg = msg + "you choose to pay with cash \n";
                    }
                    msg = msg + "this will cost you :"+ buyItemForsale.getPrice() +" $\n";


                    builder.setMessage(msg);// ההודעה גוף
                    builder.setCancelable(false);
                    builder.setNegativeButton("nope", new DialogListnerbuy(context));
                    builder.setPositiveButton("yes ", new DialogListnerbuy(context));
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                    {
                Toast.makeText(this,"choose  one payment way" , Toast.LENGTH_SHORT).show();
                }
            }
            else
                {
                Toast.makeText(this, "choose  one send way", Toast.LENGTH_SHORT).show();
            }
        }


    }
    /**
     *  show Dialog that confirm buy
     */
    public void createRateDialog(){//specific function for lunching Dialog

        rateD=new Dialog(this);//sets new Dialog
        rateD.setContentView(R.layout.rate_dialog);//find id
        rateD.setTitle("Login dialog screen");//title
        rateD.setCancelable(true);//sets Dialog as Cancelable
        //find by ids
        RBSeller=(RatingBar)rateD.findViewById(R.id.RBSellerRate);

        butRateSeller=(Button) rateD.findViewById(R.id.butRateSeller);
        //set Listeners for buttons
        butRateSeller.setOnClickListener(this);

        rateD.show();//showDialog
    }
}