package com.example.chatlog_project.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.example.chatlog_project.R;
import com.example.chatlog_project.adopters.GroupMessageAdopter;
import com.example.chatlog_project.models.GroupMessageModel;
import com.example.chatlog_project.models.MessageModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {
    TextView group_name;
    ImageView group_icon;
    ImageButton back_btn, more_btn;
    String groupId, groupName, groupIcon;
    EditText message_input;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String sender_name;
    String sender_id;
    RecordButton record_button;
    RecordView record_view;
    CardView message_input_card, send_btn_card;
    ArrayList<GroupMessageModel> groupMessageModels;
    GroupMessageAdopter groupMessageAdopter;
    RecyclerView recycler_view;
    GroupMessageModel messageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        groupIcon = getIntent().getStringExtra("groupIcon");
        groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getStringExtra("groupId");

        group_name = findViewById(R.id.group_name);
        group_icon = findViewById(R.id.group_icon);
        back_btn = findViewById(R.id.back_btn);
        more_btn = findViewById(R.id.more_btn);
        message_input = findViewById(R.id.message_input);
        record_button = findViewById(R.id.record_button);
        record_view = findViewById(R.id.record_view);
        message_input_card = findViewById(R.id.message_input_card);
        send_btn_card = findViewById(R.id.send_btn_card);
        recycler_view = findViewById(R.id.recycler_view);

        group_name.setText(groupName);
        Picasso.get().load(groupIcon).placeholder(R.drawable.group_w).into(group_icon);

        message_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = message_input.getText().toString().trim();
                if (message.isEmpty()) {
                    record_view.setVisibility(View.GONE);
                    record_button.setVisibility(View.VISIBLE);
                    send_btn_card.setVisibility(View.GONE);
                } else {
                    record_view.setVisibility(View.GONE);
                    record_button.setVisibility(View.GONE);
                    send_btn_card.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        send_btn_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = message_input.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(groupId, message);
                }
            }
        });

        listenForMessages(groupId);

        groupMessageModels=new ArrayList<>();
        groupMessageAdopter= new GroupMessageAdopter(GroupChatActivity.this, groupMessageModels,groupId,sender_name, sender_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setAdapter(groupMessageAdopter);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void sendMessage(String receiver_group_id, String message) {
        sender_id = auth.getUid();
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = formatter.format(date);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormatter.format(date);
        String timestamp = currentDate + " , " + currentTime;

        // Fetch sender's name
        database.getReference().child("user").child(sender_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sender_name = snapshot.child("name").getValue(String.class);

                // Create the GroupMessageModel object
                GroupMessageModel groupMessageModel = new GroupMessageModel(
                        sender_id, sender_name, receiver_group_id, message, timestamp, currentTime, currentDate, "text"
                );

                // Clear the message input field
                message_input.setText("");

                // Push the message to Firebase
                String message_id = database.getReference()
                        .child("groups")
                        .child(receiver_group_id)
                        .child("messages")
                        .push()
                        .getKey();

                if (message_id != null) {
                    database.getReference()
                            .child("groups")
                            .child(receiver_group_id)
                            .child("messages")
                            .child(message_id)
                            .setValue(groupMessageModel);
                }

                // Update the last message details at the group level
                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", groupMessageModel.getMessage());
                lastMsgObj.put("lastMsgTime", currentTime);

                database.getReference()
                        .child("groups")
                        .child(receiver_group_id)
                        .updateChildren(lastMsgObj);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error (e.g., log the error or show a toast)
            }
        });
    }
    private void listenForMessages(String groupId) {
        database.getReference()
                .child("groups")
                .child(groupId)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        groupMessageModels.clear(); // Clear the list to avoid duplicates
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                            String messageId = messageSnapshot.getKey();
                            String senderId = messageSnapshot.child("sender_id").getValue(String.class);
                            String message = messageSnapshot.child("message").getValue(String.class);
                            String senderName = messageSnapshot.child("sender_name").getValue(String.class);
                            String timestamp = messageSnapshot.child("timestamp").getValue(String.class);
                            String type = messageSnapshot.child("type").getValue(String.class);
                            String receiverGroupId = messageSnapshot.child("receiver_group_id").getValue(String.class);
                            String time = messageSnapshot.child("time").getValue(String.class);
                            String date = messageSnapshot.child("date").getValue(String.class);

                            // Create a new GroupMessageModel object
                            GroupMessageModel messageModel = new GroupMessageModel();
                            messageModel.setSender_id(senderId);
                            messageModel.setMessage(message);
                            messageModel.setSender_name(senderName);
                            messageModel.setTimestamp(timestamp);
                            messageModel.setType(type);
                            messageModel.setReceiver_group_id(receiverGroupId);
                            messageModel.setTime(time);
                            messageModel.setDate(date);
                            messageModel.setMessage_id(messageId); // Set the message ID

                            groupMessageModels.add(messageModel);
                        }
                        groupMessageAdopter.notifyDataSetChanged(); // Notify the adapter of changes
                        recycler_view.scrollToPosition(groupMessageModels.size() - 1); // Scroll to the last message

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}