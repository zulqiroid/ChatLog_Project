package com.example.chatlog_project.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.Common;
import com.example.chatlog_project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class User_profile extends AppCompatActivity {
    ImageView profile_image;
    TextView bio_here, username_here, gender_here, birthday_here, email_here;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profile_image = findViewById(R.id.profile_image);
        bio_here = findViewById(R.id.bio_here);
        username_here = findViewById(R.id.username_here);
        gender_here = findViewById(R.id.gender_here);
        birthday_here = findViewById(R.id.birthday_here);
        email_here = findViewById(R.id.email_here);
        toolbar = findViewById(R.id.toolbar);

        String receiver_id = getIntent().getStringExtra("receiver_id");
        String receiver_name = getIntent().getStringExtra("receiver_name");
        String receiver_img = getIntent().getStringExtra("receiver_img");

        toolbar.setTitle(receiver_name);
        Picasso.get().load(receiver_img).placeholder(R.drawable.user_w).into(profile_image);
        if (receiver_id != null) {
            fetchUserData(receiver_id);
        }
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_image.invalidate();
                Drawable drawable=profile_image.getDrawable();
                // Ensure the drawable is not null and is an instance of BitmapDrawable
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    Common.IMAGE_BITMAP = bitmap;

                    // Transition to the View_image activity
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(User_profile.this, profile_image, "image");
                    Intent intent = new Intent(User_profile.this, View_image.class);
                    intent.putExtra("user name", receiver_name);
                    startActivity(intent, options.toBundle());
                } else {
                    Toast.makeText(User_profile.this, "Unable to extract image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        initToolbar();
    }

    private void fetchUserData(String receiver_id) {
        // Reference to Firebase Realtime Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(receiver_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch and set user data
                    String bio = snapshot.child("status").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String birthday = snapshot.child("birthday").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    bio_here.setText(bio != null ? bio : "No bio available");
                    username_here.setText(username != null ? username : "No username available");
                    gender_here.setText(gender != null ? gender : "Not specified");
                    birthday_here.setText(birthday != null ? birthday : "Not provided");
                    email_here.setText(email != null ? email : "No email provided");

                } else {
                    // Handle case where data doesn't exist
                    bio_here.setText("Bio not available");
                    username_here.setText("Username not available");
                    gender_here.setText("Gender not available");
                    birthday_here.setText("Birthday not available");
                    email_here.setText("Email not available");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error case
                bio_here.setText("Failed to load data");
                username_here.setText("Failed to load data");
                gender_here.setText("Failed to load data");
                birthday_here.setText("Failed to load data");
                email_here.setText("Failed to load data");
            }
        });
    }


    private void initToolbar(){
        toolbar.setNavigationIcon(R.drawable.back_w);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

    }

}