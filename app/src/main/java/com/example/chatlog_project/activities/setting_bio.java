package com.example.chatlog_project.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class setting_bio extends AppCompatActivity {
    EditText bio_edtxt;
    androidx.appcompat.widget.AppCompatButton update_btn;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_bio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        // Enable back navigation icon
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Handle back button click
        toolbar.setNavigationOnClickListener(v -> {
            // Finish the current activity
            finish();
        });

        auth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        String userId = auth.getCurrentUser().getUid();

        bio_edtxt = findViewById(R.id.bio_edtxt);
        update_btn = findViewById(R.id.update_btn);
        String bio = getIntent().getStringExtra("bio");
        if (bio != null) {
            bio_edtxt.setText(bio);
        }
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String updatedbio= bio_edtxt.getText().toString();
                    if (!updatedbio.isEmpty() && userId != null) {
                        reference.child(userId).child("status").setValue(updatedbio)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(setting_bio.this, "Bio updated successfully!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }else {
                        Toast.makeText(setting_bio.this, "Describe yourself", Toast.LENGTH_SHORT).show();

                    }
            }
        });


    }
}