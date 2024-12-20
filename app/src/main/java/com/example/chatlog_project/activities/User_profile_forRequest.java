package com.example.chatlog_project.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.Common;
import com.example.chatlog_project.R;
import com.example.chatlog_project.models.FriendRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_profile_forRequest extends AppCompatActivity {
    ImageView back_btn, background_image;
    CircleImageView profile_image;
    TextView user_name, user_username, user_email, user_birthday, user_gender, user_status;
    AppCompatButton btn_request_friend;
    String user_id;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile_for_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        auth = FirebaseAuth.getInstance();
        String sender_id = auth.getCurrentUser().getUid();

        back_btn = findViewById(R.id.back_btn);
        background_image = findViewById(R.id.background_image);
        profile_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.user_name);
        user_username = findViewById(R.id.user_username);
        user_email = findViewById(R.id.user_email);
        user_birthday = findViewById(R.id.user_birthday);
        user_gender = findViewById(R.id.user_gender);
        user_status = findViewById(R.id.user_status);
        btn_request_friend = findViewById(R.id.btn_request_friend);

        showInfo();


        back_btn.setOnClickListener(v -> finish());

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showImage();
            }
        });

        btn_request_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(User_profile_forRequest.this, sender_id, Toast.LENGTH_SHORT).show();
                sendFriendRequest(sender_id, user_id);
            }
        });

    }
    // Function to send a friend request
    private void sendFriendRequest(String senderId, String receiverId) {
        progressDialog.show();
        DatabaseReference friendRequestRef = FirebaseDatabase.getInstance()
                .getReference("friendRequests")
                .child(receiverId)
                .child(senderId);


        DatabaseReference ref=database.getReference().child("user").child(senderId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String sender_name = snapshot.child("name").getValue(String.class);
                String sender_profile=snapshot.child("profile_image").getValue(String.class);
                FriendRequest userThought=new FriendRequest("pending",senderId,sender_name,sender_profile, false);
                friendRequestRef.setValue(userThought);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        progressDialog.dismiss();


        friendRequestRef.setValue("pending").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Friend Request Sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to send request.", Toast.LENGTH_SHORT).show();
            }
        });


        /*
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
*/
    }

    private void showInfo() {
        user_id = getIntent().getStringExtra("uid");
        database = FirebaseDatabase.getInstance();

        database.getReference().child("user").child(user_id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                String p_image = snapshot.child("profile_image").getValue(String.class);
                String bio = snapshot.child("status").getValue(String.class);
                String username = snapshot.child("username").getValue(String.class);
                String name = snapshot.child("name").getValue(String.class);
                String gender = snapshot.child("gender").getValue(String.class);
                String birthday = snapshot.child("birthday").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);

                user_name.setText(name);
                user_username.setText(username);
                user_email.setText(email);
                user_birthday.setText(birthday);
                user_gender.setText(gender);
                user_status.setText(bio);
                Picasso.get().load(p_image).placeholder(R.drawable.user_w).into(profile_image);
                progressDialog.dismiss();
            }
        });

    }

    private void showImage(){
        profile_image.invalidate();
        Drawable drawable=profile_image.getDrawable();
        // Ensure the drawable is not null and is an instance of BitmapDrawable
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Common.IMAGE_BITMAP = bitmap;


            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(User_profile_forRequest.this, profile_image, "image");
            Intent intent = new Intent(User_profile_forRequest.this, View_image.class);
            intent.putExtra("user name", user_name.getText().toString());
            startActivity(intent, Objects.requireNonNull(options.toBundle()));

        } else {
            Toast.makeText(User_profile_forRequest.this, "Unable to extract image", Toast.LENGTH_SHORT).show();
        }
    }

}