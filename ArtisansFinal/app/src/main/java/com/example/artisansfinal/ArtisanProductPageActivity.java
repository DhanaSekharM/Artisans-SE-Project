package com.example.artisansfinal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;

public class ArtisanProductPageActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    final long ONE_MB = 1024*1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artisan_product_page);
        Toast.makeText(this, "REACHED!", Toast.LENGTH_LONG).show();

        NestedScrollView layout = findViewById(R.id.artisan_product_page_nsv);
        final TextView pname = layout.findViewById(R.id.artisan_product_page_tv_product_name);
        final Button price = layout.findViewById(R.id.artisan_product_page_button_product_price);
        final TextView desc = layout.findViewById(R.id.artisan_product_page_tv_product_description);
        final ImageView image = layout.findViewById((R.id.artisan_product_page_iv_product_image));

        final Intent intent = getIntent();
//        String productCategory = intent.getStringExtra("productCategory");
        final String artisanPhoneNumber = intent.getStringExtra("artisanPhoneNumber");
        final String productName = intent.getStringExtra("productName");
        final String productID = intent.getStringExtra("productID");

        databaseReference = FirebaseDatabase.getInstance().getReference("ArtisanProducts/"+artisanPhoneNumber+"/"+productName);
        storageReference = FirebaseStorage.getInstance().getReference("ProductImages/HighRes/" + productID);

        final String key = databaseReference.getKey();
        Log.d("KEY", key);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("DATA", dataSnapshot.toString());
                Log.d("DATA", dataSnapshot.getKey());
                Log.d("DATA", dataSnapshot.getValue().toString());

                ProductInfo productInfo = (ProductInfo)intent.getParcelableExtra("productInfo");

                try{
                    String productName = productInfo.getProductName();
                    Log.d("pINFO: ", "pNAME: "+productName);
                }
                catch(Exception e){
                    Log.d("EXCEPT" ,e.toString());
                }

                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                pname.setText(map.get("productName"));
                price.setText(map.get("productPrice"));
                desc.setText(map.get("productDescription"));
                Log.d("STORAGE", storageReference.child(map.get("productID")).toString());
                storageReference.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Glide.with(getApplicationContext())
                                .load(bytes)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Toast.makeText(getApplicationContext(), "Glide load failed", Toast.LENGTH_SHORT).show();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Image Load Failed", Toast.LENGTH_SHORT).show();
                    }
                });

//                Bundle extras = getIntent().getExtras();
//                byte[] b = extras.getByteArray("productImage");
//                Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
//                image.setImageBitmap(bmp);

//                Glide.with(getApplicationContext()).load(storageReference.child(map.get("productID"))).into(image);
//                storageReference.child(map.get("productID")).getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Glide.with(getApplicationContext()).load(bytes).into(image);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(ProductPageActivity.this, "Image Load Failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                Glide.with(getApplicationContext()).
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}