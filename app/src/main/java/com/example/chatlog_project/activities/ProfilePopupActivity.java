package com.example.chatlog_project.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.Common;
import com.example.chatlog_project.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePopupActivity extends AppCompatActivity {

    String  profile_image,name,uid;
    ImageView cross_button;
    TextView profile_name;
    CircleImageView user_profile_image;
    RelativeLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.activity_profile_popup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = getIntent().getStringExtra("name");
        profile_image = getIntent().getStringExtra("profile_image");
        uid = getIntent().getStringExtra("uid");

        cross_button = findViewById(R.id.cross_button);
        profile_name = findViewById(R.id.profile_name);
        user_profile_image = findViewById(R.id.user_profile_image);
        main = findViewById(R.id.main);

        profile_name.setText(name);
        user_profile_image.setContentDescription(name + "'s profile image");
        Picasso.get().load(profile_image).placeholder(R.drawable.placeholder_image).into(user_profile_image);

        cross_button.setOnClickListener(v -> onBackPressed());
        main.setOnClickListener(v -> onBackPressed());
        user_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_profile_image.invalidate();
                Drawable drawable=user_profile_image.getDrawable();
                // Ensure the drawable is not null and is an instance of BitmapDrawable
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    Common.IMAGE_BITMAP = bitmap;

                    // Transition to the View_image activity
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfilePopupActivity.this, user_profile_image, "image");
                    Intent intent = new Intent(ProfilePopupActivity.this, View_image.class);
                    startActivity(intent, options.toBundle());
                } else {
                    Toast.makeText(ProfilePopupActivity.this, "Unable to extract image", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}