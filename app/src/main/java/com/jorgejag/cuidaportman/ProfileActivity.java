package com.jorgejag.cuidaportman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "LOG";
    private TextView textViewName;
    private TextView textViewEmail;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
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

        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);

        editTextName = findViewById(R.id.editNombreLogin);
        editTextEmail = findViewById(R.id.editEmailLogin);
        editTextPassword = findViewById(R.id.editPasswordLogin);

        btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();


                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                } else {

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("user", name);
                    userMap.put("email", email);
                    userMap.put("password", password);

                    //Obtenemos el id del usuario logeado
                    String id = auth.getCurrentUser().getUid();

                    //Agregamos los datos del hashmap al usuario con el id anteriormente obtenido
                    usersDatabase.child("Usuarios").child(id).setValue(userMap);
                    Toast.makeText(ProfileActivity.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getUserInfo();
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
                    String name = dataSnapshot.child("user").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    textViewName.setText(name);
                    textViewEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, ReportActivity.class));
        finish();
    }
}
