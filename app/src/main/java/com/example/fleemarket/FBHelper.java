package com.example.fleemarket;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FBHelper {
    private FirebaseUser currentUser;
    private static FBHelper myInstance = null;
    private static Context context;
    private DatabaseReference appRootRef;
    private DatabaseReference userRef,itemsRef;//,postRef;
    private ProgressDialog progressDialog;
    private String uID;
    private Message message;
    private Handler myHandler;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private User current;
    private Seller currentSeller;
    public static final int DONE_RET_USER=396;
    public static final int DONE_RET_ITEMS_LIST=123;
    public static final int DONE_RET_ITEM=321;
    public static final int DONE_RET_SELLER=369;
    public static final int DONE_RET_DIF_USER=963;
    public static final int DONE_RET_SELLER_IS_SELLER=936;
    public static final int DONE_RET_ITEMS_LIST_BOUGHT=321;
    private ArrayList<Item> itemsList,boughtList;
    private Item buyItem;
    private ItemForFree buyItemfree;
    private ItemForSale buyItemSale;
    private String temp;

    /**
     * create fbhelper and sets all details
     * when user created or on login
     * abstract constructor method reacquired
     */
    private FBHelper() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        appRootRef = FirebaseDatabase.getInstance().getReference();
        uID = currentUser.getUid();
        userRef = appRootRef.child("Users").child(uID);
        current=new User();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        itemsList=new ArrayList<Item>();
        boughtList=new ArrayList<>();


    }

    /**
     * check if there is fbhelper already
     * if not create one
     * abstract constructor
     * @param context app context
     * @return instance of fbhelper
     */
    public static FBHelper getInstance(Context context) {
        FBHelper.context = context;
        if (myInstance == null)
            myInstance = new FBHelper();
        return myInstance;
    }

    /**
     * get instance of firebase user
     * @return instance of firebase
     */
    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    /**
     * set for fbhelper different user
     * @param currentUser is the user its setting on fb helper
     */
    public void setCurrentUser(FirebaseUser currentUser) {
        this.uID = currentUser.getUid();
        this.currentUser = currentUser;
    }

    /**
     *
     * @return user uid
     */
    public String getuID() {
        return uID;
    }

    /**
     * sets user uid
     * @param uID it setting to user
     */
    public void setuID(String uID) {
        this.uID = uID;
    }

    /**
     * reset the fbhelper and signout of firebase
     */
    public void fbLogout() {
        FirebaseAuth.getInstance().signOut();
        myInstance = null;

    }

    /**
     * creating path to user pic
     * adding "images/" before user uid
     * @return StorageReference that has path of user pic
     */
    public StorageReference picRefUid()
    {
        StorageReference picRef=storageReference;
        picRef=picRef.child("images/" + uID.toString());
        return picRef;
    }

    //------------------------

    /**
     * save user details on database in firebase
     * in child "users",in child (user uid)uid, in child "user details"
     * @param current is the user its saving
     */
    public void saveUserData(User current)
    {
        appRootRef.child("Users").child(uID).child("user details").setValue(current);
    }

    /**
     * save itemForSell on database in firebase
     * in child "items",child random string
     * from DatabaseReference.getkey()
     * @param item is the ItemForSale its saves on firebase
     */
    public void saveItemForSellData(ItemForSale item)
    {
        DatabaseReference itemRef=appRootRef.child("items").push();
        item.setItemId(itemRef.getKey());
        itemRef.setValue(item);
    }
    /**
     * save itemForFree on database in firebase
     * in child "items",child random string
     * from DatabaseReference.getkey()
     * @param item is the ItemForFree its saves on firebase
     */
    public void saveItemForFreeData(ItemForFree item)
    {
        DatabaseReference itemRef=appRootRef.child("items").push();
        item.setItemId(itemRef.getKey());
        itemRef.setValue(item);



    }
    //
    /**
     * save item on database in firebase
     * in child "user",child (user uid) uid,child  "item bought", child Item.getItemId()(get item id)
     * @param item is the Item its saves on firebase
     */
    public void saveItemForUser(Item item)
    {
        appRootRef.child("Users").child(uID).child("item bought").child(item.getItemId()).setValue(item);
    }

    /**
     * delete item from database in firebase
     * in child "items",child item.getid() (item uid)
     * @param item is the Item its saves on firebase
     */
    public void deleteItemForUser(Item item)
    {
        appRootRef.child("items").child(item.getItemId()).removeValue();
    }

    /**
     * retrieve user data from firebase
     * add value event listener on in child "user",child (user uid) uid,child "user details"
     * sets user value (details) in msg.obj
     * @param handler use to get the value in activity that needed
     */
    public void retrieveDataUser(final Handler handler)
    {
        final ProgressDialog pd =new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
        DatabaseReference tmp=userRef;
        tmp = appRootRef.child("Users").child(uID).child("user details");
        tmp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.getValue(User.class);
                pd.dismiss();
                Message message=new Message();
                message.obj=current;
                message.arg1=DONE_RET_USER;
                handler.sendMessage(message);

//hendlr
//                boolean isOk=current.detailsOk();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });}

    /**
     *
     *  retrieve user data from firebase
     *  add value event listener on in child "user",child id,child "user details"
     *  sets user value (details) in msg.obj
     *
     * @param handler use to get the value in activity that needed
     * @param id of user we will retrive data from
     * @param con for the ProgressDialog to show
     */
    public void retrieveDataUserByUId(final Handler handler,String id,Context con)
    {
        final ProgressDialog pd =new ProgressDialog(con);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
        DatabaseReference tmp=userRef;
        tmp = appRootRef.child("Users").child(id).child("user details");
        tmp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               User tmpu = dataSnapshot.getValue(User.class);
                pd.dismiss();
                Message message=new Message();
                message.obj=tmpu;
                message.arg1=DONE_RET_DIF_USER;
                handler.sendMessage(message);

//hendlr
//                boolean isOk=current.detailsOk();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });}

    /**
     * retrieve items list data from firebase
     * add value event listener on in child "items"
     * read all slots of it and put them in ArryList
     * sets Arrylist value in fbhelper
     * @param handler use to get the value in activity that needed
     * @param con for the ProgressDialog to show
     */
    public void retrieveDataItems(final Handler handler,Context con)
    {
        final ProgressDialog pd2 =new ProgressDialog(con);
        pd2.setCancelable(false);
        pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd2.show();
        //fixed
        itemsRef = appRootRef.child("items");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemsList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren())
                {
                    Item item =data.getValue(Item.class);
                    if (item.getPrice()==0)
                    {
                        ItemForFree i =data.getValue(ItemForFree.class);
                        itemsList.add(i);
                    }
                    else
                        {
                            ItemForSale i=data.getValue(ItemForSale.class);
                            itemsList.add(i);
                        }

                }

                Message message=new Message();
                message.arg1=DONE_RET_ITEMS_LIST;
                handler.sendMessage(message);
                pd2.dismiss();

//hendlr
//                boolean isOk=current.detailsOk();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });}

    /**
     * retrieve item data from firebase
     * add value event listener on in child "items"
     * read all slots of it and check for same item id
     * sets msg.obj value with item
     *
     * @param handler use to get the value in activity that needed
     * @param itemid of item we will retrive data from
     */
    public void retrieveDataItemById(final Handler handler,String itemid)
    {
        final ProgressDialog pd =new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();


        itemsRef = appRootRef.child("items").child(itemid);
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buyItem = dataSnapshot.getValue(Item.class);
                if (buyItem!=null) {
                    boolean isfree = false;
                    if (buyItem.getPrice() == 0) {
                        buyItemfree = dataSnapshot.getValue(ItemForFree.class);
                        isfree = true;
                    } else {
                        buyItemSale = dataSnapshot.getValue(ItemForSale.class);
                    }
                    pd.dismiss();
                    if (isfree) {
                        Message message = new Message();
                        message.obj = buyItemfree;
                        message.arg1 = DONE_RET_ITEM;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.obj = buyItemSale;
                        message.arg1 = DONE_RET_ITEM;
                        handler.sendMessage(message);
                    }

//hendlr
//                boolean isOk=current.detailsOk();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });}

    /**
     * save seller details on database in firebase
     * in child "sellers",in child (user uid)uid, in child "Seller details"
     * @param current is the seller it will save
     */
    public void saveSellerData(Seller current)
    {
        appRootRef.child("Sellers").child(uID).child("Seller details").setValue(current);
    }

    /**
     * retrieve seller data from firebase
     * in child "sellers",in child id, in child "Seller details"
     * add value event listener on in child "Seller details",
     * sets msg.obj value with seller
     * @param handler use to get the value in activity that needed
     * @param id of item we will retrive data from
     * @param con for the ProgressDialog to show
     */
    public void retrieveDataSellerBiID(final Handler handler,String id,Context con)
    {
        final ProgressDialog pd =new ProgressDialog(con);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
        DatabaseReference tmp=userRef;
        tmp = appRootRef.child("Sellers").child(id).child("Seller details");
        tmp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentSeller = dataSnapshot.getValue(Seller.class);
                pd.dismiss();
                Message message=new Message();
                message.obj=currentSeller;
                message.arg1=DONE_RET_SELLER;
                handler.sendMessage(message);

//hendlr
//                boolean isOk=current.detailsOk();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });}

    /**
     *
     * @return current the user thet currently loged in
     */
    public User getCurrent() {
        return current;
    }

    /**
     *
     * @return userRef the DatabaseReference to the current user to app database on firebase
     */
    public DatabaseReference getUserRef() {
        return userRef;
    }

    /**
     *
     * @return storageReference the StorageReference to app storeg on firebase
     */
    public StorageReference getStorageReference() {
        return storageReference;
    }

    /**
     *
     * @return username(String) of user that currently loged in
     */
    public String getusername()
    {
        return current.getUsername();
    }

    /**
     * sets user as the user tat currently online
     * @param current user tat currently online
     */
    public void setUser(User current) {
        this.current = current;
    }

    /**
     *
     * @return itemsList  ArrayList<Item> of all the items for sell
     */
    public ArrayList<Item> getItemsList() {
        return itemsList;
    }

    /**
     *
     * @return buyItemSale ItemForSale
     */
    public ItemForSale getBuyItemForSale() {
        return buyItemSale;
    }

    /**
     *retrieve seller data from firebase
     * in child "sellers",in child (user id)uid, in child "Seller details"
     * add value event listener on in child "Seller details",
     * sets msg.obj value with seller
     * @param handler use to get the value in activity that needed
     */
    public void retrieveDataIsSeller(final Handler handler)
    {
        final ProgressDialog pd2 =new ProgressDialog(context);
        pd2.setCancelable(false);
        pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd2.show();
        //fixed
        DatabaseReference tmp = appRootRef.child("Sellers").child(uID);
        tmp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean flag=false;
                for (DataSnapshot data:dataSnapshot.getChildren())
                {

                    Seller seller =data.getValue(Seller.class);
                    if (seller!=null) {
                        if (uID.equals(seller.getUserID())) {
                            flag = true;
                        }
                    }

                }

                Message message=new Message();
                message.arg1=DONE_RET_SELLER_IS_SELLER;
                message.obj=flag;
                handler.sendMessage(message);
                pd2.dismiss();

//hendlr
//                boolean isOk=current.detailsOk();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });}

    /**
     *  retrieve item data from firebase
     * in child "user",in child (user id)uid, in child "item bought"
     * add value event listener on in child "item bought",
     read all slots of it and put them in ArryList
     * sets Arrylist value in fbhelper
     * @param handler use to get the value in activity that needed
     */
    public void retrieveDataBoughtItems(final Handler handler)
    {
        final ProgressDialog pd2 =new ProgressDialog(context);
        pd2.setCancelable(false);
        pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd2.show();
        //fixed
        DatabaseReference tmp=itemsRef;
        tmp = appRootRef.child("Users").child(uID).child("item bought");
        tmp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boughtList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren())
                {
                    Item item =data.getValue(Item.class);
                    if (item.getPrice()==0)
                    {
                        ItemForFree i =data.getValue(ItemForFree.class);
                        boughtList.add(i);
                    }
                    else
                    {
                        ItemForSale i=data.getValue(ItemForSale.class);
                        boughtList.add(i);
                    }

                }

                Message message=new Message();
                message.arg1=DONE_RET_ITEMS_LIST_BOUGHT;
                handler.sendMessage(message);
                pd2.dismiss();

//hendlr
//                boolean isOk=current.detailsOk();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });}

    /**
     *
     * @return boughtList ArrayList<Item> of items bought by current user
     */
    public ArrayList<Item> getBoughtList() {
        return boughtList;
    }
}