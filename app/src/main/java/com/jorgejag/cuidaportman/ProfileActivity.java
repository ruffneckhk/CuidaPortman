package com.jorgejag.cuidaportman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "LOG";
    private TextView textViewUser;
    private TextView textFullName;
    private EditText editTextUser;
    private EditText editTextFullName;
    private EditText editPassword;
    private Button btnUpdate;

    private FirebaseAuth auth;
    private DatabaseReference usersDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        textViewUser = findViewById(R.id.textViewUser);
        textFullName = findViewById(R.id.textViewFullName);

        editTextUser = findViewById(R.id.editUserLogin);
        editTextFullName = findViewById(R.id.editFullName);
        editPassword = findViewById(R.id.editPasswordLogin);

        btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = editTextUser.getText().toString();
                String fullName = editTextFullName.getText().toString();
                String password = editPassword.getText().toString();

                //Obtenemos el id del usuario logeado
                String id = auth.getCurrentUser().getUid();

                if (userName.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                } else {

                    auth.getCurrentUser().updatePassword(password);

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", id);
                    userMap.put("userName", userName);
                    userMap.put("fullName", fullName);


                    //Agregamos los datos del hashmap al usuario con el id anteriormente obtenido
                    usersDatabase.child("Usuarios").child(id).setValue(userMap);
                    Toast.makeText(ProfileActivity.this, "Datos actualizados", Toast.LENGTH_SHORT).show();

                    //Volver a Home
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                            finish();
                        }
                    }, 3000);
                }
            }
        });

        getUserInfo();
    }


    //Trabajamos con el usuario que ha iniciado sesion
    //Pedimos a la base de datos los datos del id que ha iniciado sesion
    private void getUserInfo() {
        final String id = auth.getCurrentUser().getUid();
        usersDatabase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String user = dataSnapshot.child("userName").getValue().toString();
                    String fullName = dataSnapshot.child("fullName").getValue().toString();

                    textViewUser.setText(user);
                    textFullName.setText(fullName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        finish();
    }
}
