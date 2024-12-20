package com.example.chatlog_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;
import com.google.android.material.textfield.TextInputEditText;

public class username_signup extends AppCompatActivity {
    TextInputEditText username;
    AppCompatButton cont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_username_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        username=findViewById(R.id.username);
        cont=findViewById(R.id.cont);
        // Retrieve the name
        String name = getIntent().getStringExtra("name");
        String imageUriString = getIntent().getStringExtra("imageUri");
        String Email=getIntent().getStringExtra("email");
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname=username.getText().toString();
                if (uname.isEmpty()) {
                    username.setError("Please enter your username");
                } else if (uname.length() < 6 || uname.length() > 20) {
                    username.setError("Username must be between 6 and 20 characters");
                } else if (!uname.matches("^[a-zA-Z0-9_-]+$")) {
                    username.setError("Username can only contain letters, numbers, underscores, and hyphens");
                } else {
                    // Username is valid, proceed to the next activity
                    Intent intent = new Intent(username_signup.this, Birthday_signup.class);
                    intent.putExtra("name", name);
                    intent.putExtra("imageUri", imageUriString);
                    intent.putExtra("email", Email);
                    intent.putExtra("username", uname);
                    startActivity(intent);
                }
            }
        });

    }
}