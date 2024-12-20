package com.example.chatlog_project.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Show_user_thoughts extends AppCompatActivity {
    ImageView back_btn;
    TextView name;
    CircleImageView profile_image;
    TextView any_thought_txtView;
    TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_user_thoughts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        back_btn = findViewById(R.id.back_btn);
        name = findViewById(R.id.name);
        profile_image = findViewById(R.id.profile_image);
        any_thought_txtView = findViewById(R.id.any_thought_txtView);
        time = findViewById(R.id.time);

        String profile_image_url = getIntent().getStringExtra("profile_image");
        if (profile_image_url != null) {
            // Use Picasso to load the image into the CircleImageView
            Picasso.get().load(profile_image_url).placeholder(R.drawable.user_w).into(profile_image);
        }
        String name_text = getIntent().getStringExtra("name");
        if (name_text != null) {
            name.setText(name_text);
        }
        String thought_text = getIntent().getStringExtra("thought");
        if (thought_text != null) {
            any_thought_txtView.setText(thought_text);
        }
        String time_text = getIntent().getStringExtra("time");
        if (time_text != null) {
            time.setText(time_text);
        }
        back_btn.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public void onBackPressed() {
        // Apply the animation here
        overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right);
        super.onBackPressed();
    }
}