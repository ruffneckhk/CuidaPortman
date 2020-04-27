package com.jorgejag.cuidaportman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    //Elementos en la activity
    private EditText editUsuario;
    private EditText editEmail;
    private EditText editPassword;
    private Button btnRegistrar;
    private Button btnSendToLogin;

    //Datos a registrar
    private String user = "";
    private String email = "";
    private String password = "";

    //Objeto Autenticacion
    FirebaseAuth auth;

    //Objeto DatabaseReference
    DatabaseReference dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Instanciamos FirebaseAuth
        auth = FirebaseAuth.getInstance();

        //Instanciamos database
        dataBase = FirebaseDatabase.getInstance().getReference();

        editUsuario = findViewById(R.id.editNombreLogin);
        editEmail = findViewById(R.id.editEmailLogin);
        editPassword = findViewById(R.id.editPasswordLogin);
        btnRegistrar = findViewById(R.id.btnRegistrarLogin);
        btnSendToLogin = findViewById(R.id.btnSendToLogin);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = editUsuario.getText().toString();
                email = editEmail.getText().toString();
                password = editPassword.getText().toString();


                //Comprobamos que los campos no esten vacios
                if (!user.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                    //Comprobacion de que la contrasena tenga mas de 6 caracteres
                    if (password.length() >= 6) {
                        //Llamada al metodo registrarUsuario
                        registrarUsuario();
                    } else {
                        Toast.makeText(RegisterActivity.this, "El password debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RegisterActivity.this, "Completa los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Al pulsar en el boton de login pasamos a la activity de login
        btnSendToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void registrarUsuario() {
        //Crea el usuario utilizando email y password ingresados
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //HashMap con los datos a agregar a las base de datos
                    Map<String, Object> map = new HashMap<>();
                    map.put("user", user);
                    map.put("email", email);
                    map.put("password", password);

                    //Obtenemos el id del usuario
                    String id = auth.getCurrentUser().getUid();

                    //Agregamos los datos del hashmap al usuario con el id anteriormente obtenido
                    dataBase.child("Usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "El usuario no se ha podido crear", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "La cuenta de correo ya esta registrada", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Para mantener la sesion abierta, comprobamos si el usuario ha hecho login anteriormente
    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
            finish();
        }
    }
}
