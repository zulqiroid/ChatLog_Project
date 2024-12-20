package com.example.chatlog_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class setting_name extends AppCompatActivity {
    TextInputEditText name_edtxt;
    AppCompatButton update_btn;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_name);

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

        name_edtxt = findViewById(R.id.name_edtxt);
        update_btn = findViewById(R.id.update_btn);

        String name = getIntent().getStringExtra("name");
        if (name != null) {
            name_edtxt.setText(name);
        }

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = name_edtxt.getText().toString();
                if (!updatedName.isEmpty() && userId != null) {
                    reference.child(userId).child("name").setValue(updatedName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(setting_name.this, "Name updated successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                }else {
                    name_edtxt.setError("Please enter a name");
                }
            }
        });
    }
}
