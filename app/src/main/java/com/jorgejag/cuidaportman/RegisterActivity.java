package com.jorgejag.cuidaportman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    //Elementos en la activity
    private EditText editUsuario;
    private EditText editNombreCompleto;
    private EditText editEmail;
    private EditText editPassword;
    private Button btnRegistrar;
    private Button btnSendToLogin;
    private ProgressDialog progressDialog;

    //Instancia Autenticacion
    FirebaseAuth auth;

    //Instancia DatabaseReference
    DatabaseReference dataBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //FirebaseAuth
        auth = FirebaseAuth.getInstance();

        //databaseReference
        dataBaseRef = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(RegisterActivity.this);

        editUsuario = findViewById(R.id.editUserLogin);
        editEmail = findViewById(R.id.editEmailLogin);
        editPassword = findViewById(R.id.editPasswordLogin);
        editNombreCompleto = findViewById(R.id.editFullName);
        btnRegistrar = findViewById(R.id.btnRegistrarLogin);
        btnSendToLogin = findViewById(R.id.btnSendToLogin);

        //Boton de registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Por favor espera...");
                progressDialog.show();

                //Toma de datos desde los EditView
                String strUserName = editUsuario.getText().toString();
                String strFullName = editNombreCompleto.getText().toString();
                String strEmail = editEmail.getText().toString();
                String strPassword = editPassword.getText().toString();

                //Comprobacion de que los campos de texto no estan vacios
                if (TextUtils.isEmpty(strUserName) || TextUtils.isEmpty(strFullName)
                        || TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
                    //Comprobacion de longitud del password
                } else if (strPassword.length() < 6) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "La contrasena debe tener 6 o mas caracteres", Toast.LENGTH_SHORT).show();
                    //Lamada al metodo register
                } else {
                    register(strUserName, strFullName, strEmail, strPassword);
                }
            }
        });

        //Al pulsar en el boton de login pasamos a la activity de login
        btnSendToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    //Metodo para proceder al registro del usuario
    private void register(final String userName, final String fullName, String email, String password) {
        //Crea un usuario autenticado con el metodo createUserWithEmailAndPassword
        //Y a su vez cuando la tarea este completa se crea la base de datos "Usuarios"
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            dataBaseRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(userId);

                            //Hashmap para agregar los datos a DatabaseReference
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("userName", userName.toLowerCase());
                            hashMap.put("fullName", fullName);

                            //Cuando los valores esten aniadidos a la Database vamos a la HomeActivity
                            dataBaseRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Ese email ya ha sido utilizado o password incorrecto.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    //Para mantener la sesion abierta, comprobamos si el usuario ha hecho login anteriormente
    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }
    }

}
