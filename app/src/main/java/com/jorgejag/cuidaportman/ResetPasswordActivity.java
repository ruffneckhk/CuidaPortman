package com.jorgejag.cuidaportman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    //Elementos en la activity
    private EditText editTextEmail;
    private Button btnResetPassword;

    //Variable para captura el email
    private String email = "";

    //Autenticacion
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editTextEmail = findViewById(R.id.editTextEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        auth = FirebaseAuth.getInstance();


        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = editTextEmail.getText().toString();

                if (!email.isEmpty()) {
                    resetPassword();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Debe ingresar el email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPassword() {
        //Estable el idioma del correo de recuperacion
        auth.setLanguageCode("es");
        //Enviamos el mensaje al email introducido
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Comprueba que la tarea de envio del email se realizo con exito, si no es asi se envia un mensaje indicandolo
                if (task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Se ha enviado un correo de reestablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "No se ha podido enviar el correo de recuperacion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
