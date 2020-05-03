package com.jorgejag.cuidaportman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullImageActivity extends AppCompatActivity {

    private ImageView fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        fullImage = findViewById(R.id.fullImage);

        Intent i = getIntent();
        String url = i.getStringExtra("fullImage");

        Picasso.get().load(url).centerCrop().placeholder(R.drawable.ic_launcher_background).into(fullImage);


    }
}
