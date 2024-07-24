package com.example.fleemarket;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {
    protected Context context;
    protected List<Item> objects;
    protected FBHelper fbHelper;

    public ItemAdapter(Context context, int resource, List<Item> objects, FBHelper fbHelper){
        super(context, resource, objects);
        this.context=context;
        this.objects=objects;
        this.fbHelper=fbHelper;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        LayoutInflater layoutInflater=((Activity)context).getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.item_view_layout,parent,false);
        TextView tvIName=(TextView)view.findViewById(R.id.tvIName);
        RatingBar rateItem= (RatingBar) view.findViewById(R.id.rateItem);
        TextView tvPrice=(TextView)view.findViewById(R.id.tvprice);
        final ImageView ivipic=(ImageView)view.findViewById(R.id.ivipic);
        Item temp=objects.get(position);
        StorageReference picRef;
        if (!temp.getPicRef().equals("")&&temp.getPicRef()!=null){
        final long ONE_MEGABYTE = 1024 * 1024;
//download file as a byte array
        picRef=this.fbHelper.getStorageReference();
        picRef=picRef.child(""+temp.getPicRef());
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        bytes,0, bytes.length);
                if(bitmap!=null) {
                    ivipic.setImageBitmap(bitmap);
                    ivipic.setBackground(null);
                    //Toast.makeText(this, "Download successful", Toast.LENGTH_SHORT).show();
                }
                                                                       }
                                                                   });}
        //
        tvIName.setText(temp.getName());
        rateItem.setRating(temp.getRate());
        tvPrice.setText(String.valueOf(temp.getPrice()+" $"));

        return view;
    }

}
