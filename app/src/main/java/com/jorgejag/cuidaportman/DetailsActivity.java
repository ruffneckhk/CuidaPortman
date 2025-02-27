package com.jorgejag.cuidaportman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private ImageView imageView;
    private TextView textView;
    private Button btnComent;
    private EditText editTextComent;
    private String name;

    private DatabaseReference database;

    private FirebaseAuth auth;
    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageView = findViewById(R.id.imageViewFull);
        textView = findViewById(R.id.txtCommentFull);
        btnComent = findViewById(R.id.btnComent);
        editTextComent = findViewById(R.id.editTextNewComent);

        database = FirebaseDatabase.getInstance().getReference("Incidencias");
        auth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference();

        //Intent con la informacion de la incidencia recibida desde la activity Reports
        Intent intent = getIntent();
        final String comentTextView = intent.getStringExtra("comment");
        byte[] bytes = getIntent().getByteArrayExtra("image");
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        textView.setText(comentTextView);
        imageView.setImageBitmap(bitmap);

        getUserInfo();

        //Envia un comentario, recorriendo el nodo Incidencias añadiendo el comentario a la incidencia
        //seleccionada
        btnComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                            String oldComent = zoneSnapshot.child("comment").getValue(String.class);
                            String addComent = "\n" + name + ": " + editTextComent.getText().toString();
                            Log.i(TAG, zoneSnapshot.child("comment").getValue(String.class));

                            if (oldComent.equalsIgnoreCase(comentTextView)) {
                                zoneSnapshot.child("comment").getRef().setValue(comentTextView + addComent);
                                Toast.makeText(DetailsActivity.this, "Comentario enviado", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(DetailsActivity.this, ReportActivity.class));
                                        finish();
                                    }
                                }, 1000);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });
            }


        });

    }

    //Trabajamos con el usuario que ha iniciado sesion
    //Pedimos a la base de datos los datos del id que ha iniciado sesion
    private void getUserInfo() {
        String id = auth.getCurrentUser().getUid();
        usersDatabase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = dataSnapshot.child("userName").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetailsActivity.this, ReportActivity.class));
        finish();
    }
}
