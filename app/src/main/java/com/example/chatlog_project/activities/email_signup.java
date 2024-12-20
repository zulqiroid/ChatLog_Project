package com.example.chatlog_project.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;
import com.google.android.material.textfield.TextInputEditText;

public class email_signup extends AppCompatActivity {
    String email_regex="^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    TextInputEditText email;
    AppCompatButton cont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email=findViewById(R.id.email);
        cont=findViewById(R.id.cont);

        // Retrieve the name
        String name = getIntent().getStringExtra("name");
        Toast.makeText(this, "Name: " + name, Toast.LENGTH_SHORT).show();

        // Retrieve the image URI
        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=email.getText().toString();
                if(Email.isEmpty()){
                    email.setError("Please enter your email address");
                }
                else if(!Email.matches(email_regex)){
                    email.setError("Please enter a valid email address");
                }
                else{
                    Intent intent=new Intent(email_signup.this, username_signup.class);
                    intent.putExtra("name",name);
                    intent.putExtra("imageUri",imageUriString);
                    intent.putExtra("email",Email);
                    startActivity(intent);
                }
            }
        });
    }
}