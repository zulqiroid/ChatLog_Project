/*
package com.example.chatlog_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class incomming_call extends AppCompatActivity {
    CircleImageView caller_profile_picture;
    TextView caller_name, incoming_call_label;
    ImageButton accept_btn, reject_btn;
    DatabaseReference callRef1, callRef2;
    ValueEventListener callStatusListener;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_incomming_call);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        caller_profile_picture = findViewById(R.id.caller_profile_picture);
        caller_name = findViewById(R.id.caller_name);
        incoming_call_label = findViewById(R.id.incoming_call_label);
        accept_btn = findViewById(R.id.accept_btn);
        reject_btn = findViewById(R.id.reject_btn);

        String username=auth.getUid();

        // Get call information from the intent
        String receiverId = getIntent().getStringExtra("receiverId");
        String callId = getIntent().getStringExtra("callId");
        String callerId = getIntent().getStringExtra("callerId");
        String callerNameValue = getIntent().getStringExtra("callerName");
        String callerProfilePicUrl = getIntent().getStringExtra("callerProfilePic");

        caller_name.setText(callerNameValue);
        Picasso.get().load(callerProfilePicUrl).placeholder(R.drawable.user_w).into(caller_profile_picture);

        // Reference to the call in Firebase
        callRef1 = FirebaseDatabase.getInstance().getReference("calls").child(callId);
        callRef2 = FirebaseDatabase.getInstance().getReference("usersCall").child(callerId);

        // Handle Accept button click
        accept_btn.setOnClickListener(view -> {
            callRef1.child("status").setValue("accepted");
            callRef2.child("status").setValue("accepted");
            Intent intent = new Intent(incomming_call.this, call_screen.class);
            intent.putExtra("username", username);
            intent.putExtra("incoming", receiverId);
            intent.putExtra("callId", callId);
            intent.putExtra("createdBy", callerId);
            startActivity(intent);
            finish();
        });

        // Handle Reject button click
        reject_btn.setOnClickListener(view -> {
            callRef1.child("status").setValue("rejected");
            callRef2.removeValue();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    finish();
                }
            }, 1000);
        });

        // Listen for changes in call status
        callStatusListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.child("status").getValue(String.class);
                    if ("caller ended".equals(status)) {
                        Toast.makeText(incomming_call.this, "The caller has ended the call.", Toast.LENGTH_SHORT).show();
                        callRef2.removeValue();
                        finish(); // Close the activity
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(incomming_call.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return null;
            }
        };
        callRef2.addValueEventListener(callStatusListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the listener to prevent memory leaks
        if (callRef1 != null && callStatusListener != null) {
            callRef1.removeEventListener(callStatusListener);
        }
    }
}
*/
