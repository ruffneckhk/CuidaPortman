package com.jorgejag.cuidaportman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private ProgressBar progressCircle;
    private DatabaseReference reportsDatabase;

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Upload> uploads;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        progressCircle = findViewById(R.id.progressCircle);

        //Database reference para trabajar con las incidencias.
        reportsDatabase = FirebaseDatabase.getInstance().getReference("Incidencias");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressCircle = findViewById(R.id.progressCircle);

        context = new ReportActivity();


        //Array para las incidencias subidas
        uploads = new ArrayList<>();

        //Creamos un listener sobre el nodo Incidencias que recorrer las instantaneas
        //y las agregue al arraylist Uploads
        reportsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploads.add(upload);
                }
                //Añadimos el contenido del array uploads a la recyclerview mediante
                //el adapter
                imageAdapter = new ImageAdapter(ReportActivity.this, uploads);
                progressCircle.setVisibility(View.INVISIBLE);
                recyclerView.setAdapter(imageAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true));
                //Muevo la posicion de la recyclerview a la ultima posicion anadida
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,null, uploads.size()-1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressCircle.setVisibility(View.INVISIBLE);
                Toast.makeText(ReportActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ReportActivity.this, HomeActivity.class));
        finish();
    }

}