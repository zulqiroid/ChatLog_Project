package com.example.chatlog_project.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;
import com.example.chatlog_project.models.UserThought;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class View_tought extends AppCompatActivity {
CircleImageView profile_image;
EditText any_thought_text;
AppCompatButton share;
FirebaseAuth auth;
FirebaseDatabase database;
String name;
ImageView back_btn;
TextView any_thought_txtView, user_name, time;
ProgressDialog progressDialog;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_tought);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        back_btn=findViewById(R.id.back_btn);
        profile_image=findViewById(R.id.profile_image);
        any_thought_text=findViewById(R.id.any_thought_text);
        any_thought_txtView=findViewById(R.id.any_thought_txtView);
        user_name=findViewById(R.id.name);
        time=findViewById(R.id.time);
        share=findViewById(R.id.share);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        String profile_image_url = getIntent().getStringExtra("profile_image");
        if (profile_image_url != null) {
            // Use Picasso to load the image into the CircleImageView
            Picasso.get().load(profile_image_url).placeholder(R.drawable.user_w).into(profile_image);
        }

        any_thought_text.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String text=any_thought_text.getText().toString().trim();
                            if (!text.isEmpty()){
                                findViewById(R.id.share).setVisibility(View.VISIBLE);
                                share.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        progressDialog.show();
                                        String userId=auth.getUid();
                                        assert userId != null;
                                        DatabaseReference reference=database.getReference().child("Thoughts").child(userId);
                                        String thought=any_thought_text.getText().toString();
                                        Date date = Calendar.getInstance().getTime();
                                        @SuppressLint("SimpleDateFormat")
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                        String currentdate = formatter.format(date);

                                        Calendar currentDateTime = Calendar.getInstance();
                                        @SuppressLint("SimpleDateFormat")
                                        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
                                        String currentTime = df.format(currentDateTime.getTime());
                                        String timestamp = currentdate + " , " + currentTime;


                                        DatabaseReference ref=database.getReference().child("user").child(userId);
                                        ref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                name = snapshot.child("name").getValue(String.class);
                                                UserThought userThought=new UserThought(userId,name,profile_image_url,timestamp,thought,currentTime);
                                                reference.setValue(userThought);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });

                                        progressDialog.dismiss();

                                        onBackPressed();

                                    }
                                });
                            }else {
                                findViewById(R.id.share).setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
    @Override
    public void onBackPressed() {
        // Apply the animation here
        overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right);
        super.onBackPressed();
    }
            }


