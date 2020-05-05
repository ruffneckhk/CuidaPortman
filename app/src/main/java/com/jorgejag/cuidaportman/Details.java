package com.jorgejag.cuidaportman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Details extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        imageView = findViewById(R.id.imageViewFull);
        textView = findViewById(R.id.txtCommentFull);


        Intent intent = getIntent();
        String comment = intent.getStringExtra("comment");

        byte[] bytes = getIntent().getByteArrayExtra("image");

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);



        textView.setText(comment);
        imageView.setImageBitmap(bitmap);






    }
}
