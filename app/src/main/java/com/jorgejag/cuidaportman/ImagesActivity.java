package com.jorgejag.cuidaportman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {

    private TextView textViewNombre;
    private TextView textViewCorreo;

    private Button btnSingOut;
    private Button btnIncidencia;
    private ProgressBar progressCircle;
    private Button btnProfile;

    private FirebaseAuth auth;
    private DatabaseReference usersDatabase;
    private DatabaseReference incidencesDatabase;

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Upload> uploads;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        btnSingOut = findViewById(R.id.btnSingOut);
        btnIncidencia = findViewById(R.id.btnIncidencia);
        textViewNombre = findViewById(R.id.textViewName);
        progressCircle = findViewById(R.id.progressCircle);
        btnProfile = findViewById(R.id.btnProfile);

        auth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference();
        incidencesDatabase = FirebaseDatabase.getInstance().getReference("Incidencias");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        uploads = new ArrayList<>();

        incidencesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploads.add(upload);
                }

                imageAdapter = new ImageAdapter(ImagesActivity.this, uploads);
                recyclerView.setAdapter(imageAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,true));
                recyclerView.smoothScrollToPosition(recyclerView.getBottom());
                progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }
        });

        //Accion de cada boton
        btnIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Envio hacia la activity upload
                startActivity(new Intent(ImagesActivity.this, UploadActivity.class));
                finish();
            }
        });

        //Hacer logout
        btnSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(ImagesActivity.this, RegisterActivity.class));
                finish();
            }
        });

        //Boton profile
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImagesActivity.this, ProfileActivity.class));
            }
        });

        getUserInfo();

    }

    //Trabaja con el usuario que ha iniciado sesion
    //Pedimos a la base de datos los datos del id que ha iniciado sesion
    private void getUserInfo() {
        String id = auth.getCurrentUser().getUid();
        usersDatabase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("user").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    textViewNombre.setText("Bienvenido " + name);
                    //textViewCorreo.setText("Este es tu email: " + email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}