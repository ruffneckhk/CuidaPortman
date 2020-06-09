package com.jorgejag.cuidaportman;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class UploadActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private Button btnSend;
    private ImageView selectedImage;
    private ImageButton imgBtnCapture;
    private EditText editTextComent;
    private ProgressBar progressBar;
    private String name;
    private Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference database;
    private StorageTask uploadTask;

    private FirebaseAuth auth;
    private DatabaseReference usersDatabase;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        btnSend = findViewById(R.id.btnEnviar);
        imgBtnCapture = findViewById(R.id.imgButtonCapture);
        selectedImage = findViewById(R.id.imageView);
        editTextComent = findViewById(R.id.editTextComent);
        progressBar = findViewById(R.id.progressBar);

        storageReference = FirebaseStorage.getInstance().getReference("Incidencias");
        database = FirebaseDatabase.getInstance().getReference("Incidencias");

        auth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference();

        //Accion de cada boton
        imgBtnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pedir permisos para usar la camara al usuario
                askCameraPermissions();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Con uploadtask evitamos que se envien incidencias seguidas
                //No se enviaran hasta que termine de enviarse la primera
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(UploadActivity.this, "Subida en progreso", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(UploadActivity.this, HomeActivity.class));
                            finish();
                        }
                    }, 3000);

                }
            }
        });

        getUserInfo();
    }

    //Solicitar permisos de camara
    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    //Llamada a metodo dispatchTakePictureIntent() si se han otorgado los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Debe aceptar el uso de la camara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Agrega la imageUri mediante Picasso
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                imageUri = Uri.fromFile(f);
                Picasso.get().load(imageUri).into(selectedImage);
            }

        }
    }

    //Crear el fichero de imagen
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  //* prefijo *//*
                ".jpg",         //* sufijo *//*
                storageDir      //* directorio *//*
        );

        // Guarda el fichero: path para uso con los ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Nos aseguramos de que haya una actividad de cámara para manejar el intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Crea el file donde va a ir la foto
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.getStackTrace();

            }
            //Continua solo si el File ha sudo creado correctamente
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.jorgejag.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    //Meotodo para subir finalmente la foto y el comentario
    private void uploadFile() {
        String comment = editTextComent.getText().toString();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final String stringTimeStam = timestamp.toString();
        if (imageUri != null && !comment.isEmpty()) {
            //Damos un nombre unico a cada incidencia subida
            StorageReference fileRerence = storageReference.child("Incidencias" + System.currentTimeMillis() + ".jpg");

            uploadTask = fileRerence.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Retraso de tiempo para la barra de progreso
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 500);
                    Toast.makeText(UploadActivity.this, "Subida completada", Toast.LENGTH_SHORT).show();

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();


                    Upload upload = new Upload(name + ": " + editTextComent.getText().toString().trim(), downloadUrl.toString(), stringTimeStam);
                    String uploadId = database.push().getKey();

                    database.child(uploadId).setValue(upload);

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());//Barra de progreso
                    progressBar.setProgress((int) progress);
                }
            });

        } else {
            Toast.makeText(this, "Debes realizar una foto y dejar un comentario", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent (UploadActivity.this, HomeActivity.class));
        finish();
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
}
