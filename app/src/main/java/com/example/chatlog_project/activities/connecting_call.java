/*
package com.example.chatlog_project.activities;

import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class connecting_call extends AppCompatActivity {

    CircleImageView profile;
    ImageButton imageButton2;
    TextView textView;
    String receiver_name, receiver_img, receiver_id, caller_id;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference, userRef;
    String callId;
    String callerName;
    String callerProfilePic;
    String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connecting_call);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        profile = findViewById(R.id.profile);
        imageButton2 = findViewById(R.id.imageButton2);
        textView = findViewById(R.id.textView);

        caller_id = getIntent().getStringExtra("caller_id");
        receiver_name = getIntent().getStringExtra("receiver_name");
        receiver_img = getIntent().getStringExtra("receiver_img");
        receiver_id = getIntent().getStringExtra("receiver_id");

        textView.setText(receiver_name);
        Picasso.get().load(receiver_img).placeholder(R.drawable.user_w).into(profile);
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String currentdate = formatter.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());
        timestamp = currentdate + "-" + currentTime;
        callId = caller_id + "-" + receiver_id+ "-" + timestamp;

        reference=FirebaseDatabase.getInstance().getReference("calls").child(callId);
        userRef = FirebaseDatabase.getInstance().getReference("usersCall").child(caller_id);
        FirebaseDatabase.getInstance().getReference().child("user").child(caller_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callerName=snapshot.child("name").getValue(String.class);
                callerProfilePic=snapshot.child("profile_image").getValue(String.class);

               sendCallRequest(callId ,callerName, callerProfilePic, receiver_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                return null;
            }
        });



listenForCallStatus();
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("status").setValue("caller ended");
                userRef.child("status").setValue("caller ended");
                finish();
            }
        });
    }
    private void listenForCallStatus() {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("usersCall").orderByChild("createdBy")
                .equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                    for(DataSnapshot snapshot : datasnapshot.getChildren())  {
                                        String status = snapshot.child("status").getValue(String.class);
                                        if ("accepted".equals(status)) {
                                            String callerName = snapshot.child("callerName").getValue(String.class);
                                            String callerProfilePic = snapshot.child("callerProfilePic").getValue(String.class);
                                            String callerId = snapshot.child("createdBy").getValue(String.class);
                                            String callId = snapshot.child("callId").getValue(String.class);
                                            // Open Incoming Call Activity
                                           Intent intent = new Intent(connecting_call.this, call_screen.class);
                                           intent.putExtra("username", currentUserId);
                                            intent.putExtra("incoming", receiver_id);
                                            intent.putExtra("callId", callId);
                                            intent.putExtra("callerId", callerId);
                                            intent.putExtra("createdBy", callerName);
                                            intent.putExtra("callerProfilePic", callerProfilePic);
                                            startActivity(intent);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle error
                                    return null;
                                }
                            });
                }

    private void sendCallRequest(String callId,String callerName, String callerProfilePic, String receiver_id) {
        HashMap<String, Object> callData = new HashMap<>();
        callData.put("callerId", FirebaseAuth.getInstance().getUid());
        callData.put("receiverId", receiver_id);
        callData.put("callerName", callerName);
        callData.put("callerProfilePic", callerProfilePic);
        callData.put("status", "waiting");
        callData.put("timestamp", System.currentTimeMillis());
        callData.put("callType", "video"); // or "voice"
        reference.setValue(callData);

        HashMap<String, Object> callReceiveData = new HashMap<>();
        callReceiveData.put("callId", callId);
        callReceiveData.put("createdBy", FirebaseAuth.getInstance().getUid());
        callReceiveData.put("receiverId", receiver_id);
        callReceiveData.put("callerName", callerName);
        callReceiveData.put("callerProfilePic", callerProfilePic);
        callReceiveData.put("status", "waiting");
        callReceiveData.put("timestamp", System.currentTimeMillis());
        callReceiveData.put("callType", "video"); // or "voice"
        userRef.setValue(callReceiveData);
    }


}
*/
