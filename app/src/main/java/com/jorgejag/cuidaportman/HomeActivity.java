package com.jorgejag.cuidaportman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeActivity extends AppCompatActivity {

    private TextView textViewUser2;
    private TextView textViewEmail;
    private TextView textViewFullName;

    private FirebaseAuth auth;
    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnSingOut = findViewById(R.id.btnSingOut);
        Button btnNuevaIncidencia = findViewById(R.id.btnIncidencia);
        Button btnIncidencias = findViewById(R.id.btnVerIncidecias);
        Button btnProfile = findViewById(R.id.btnProfile);
        textViewUser2 = findViewById(R.id.textViewUser2);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewFullName = findViewById(R.id.textViewFullName);

        auth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference();

        //Accion de cada boton

        btnIncidencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ReportActivity.class));
            }
        });

        btnNuevaIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Envio hacia la activity upload
                startActivity(new Intent(HomeActivity.this, UploadActivity.class));
                finish();
            }
        });

        //Hacer logout
        btnSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
                finish();
            }
        });

        //Boton profile
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
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
                    String email = auth.getCurrentUser().getEmail();
                    String user = dataSnapshot.child("userName").getValue().toString();
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    textViewUser2.setText(user);
                    textViewEmail.setText(email);
                    textViewFullName.setText(fullName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(HomeActivity.this, "Pulse en CERRAR SESION", Toast.LENGTH_SHORT).show();
    }
}







