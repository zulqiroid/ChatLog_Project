package com.example.chatlog_project.activities;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlog_project.R;
import com.example.chatlog_project.adopters.FriendAdapter;
import com.example.chatlog_project.models.Users;
import com.giphy.sdk.core.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {
    RecyclerView friendsRecyclerView;
    AppCompatButton createGroupBtn;
    ArrayList<Users> friendsList = new ArrayList<>();
    ArrayList<String> selectedFriends = new ArrayList<>();
    FriendAdapter friendsAdapter;
    FirebaseDatabase database;
    FirebaseAuth auth;
    CircleImageView profilePicture;
    ImageButton addImage, backBtn;
    EditText groupNameEditText;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        // Set up RecyclerView

        {
            friendsRecyclerView = findViewById(R.id.friendsRecyclerView);
            createGroupBtn = findViewById(R.id.createGroupBtn);
            profilePicture = findViewById(R.id.profilePicture);
            addImage = findViewById(R.id.addImage);
            groupNameEditText = findViewById(R.id.groupNameEditText);
            backBtn = findViewById(R.id.back_btn);

            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
        }
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Fetch friends from Firebase
        fetchFriends();
        // Create group on button click
        createGroupBtn.setOnClickListener(v -> createGroup());
    }
    private void fetchFriends() {
        database.getReference("friends").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friendsList.clear();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            String friendId = friendSnapshot.getKey(); // Friend's UID
                            if (friendId != null) {
                                FirebaseDatabase.getInstance().getReference("user")
                                        .child(friendId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Users friend = snapshot.getValue(Users.class);
                                                if (friend != null) {
                                                    friendsList.add(friend);
                                                    friendsAdapter = new FriendAdapter(CreateGroupActivity.this, friendsList, selectedFriends, createGroupBtn);
                                                    friendsRecyclerView.setAdapter(friendsAdapter);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CreateGroupActivity.this, "Failed to load friends", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createGroup() {
        String groupName = groupNameEditText.getText().toString().trim();

        if (groupName.isEmpty()) {
            Toast.makeText(this, "Please enter group name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFriends.isEmpty()) {
            Toast.makeText(this, "Select at least one friend", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload group image to Firebase Storage
        String groupId = database.getReference("groups").push().getKey(); // Unique group ID
        uploadGroupImage(groupId, groupName);
    }

    private void uploadGroupImage(String groupId, String groupName) {
        imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/chatlog-c2899.appspot.com/o/group_w.png?alt=media&token=24eef554-4917-481f-ac58-07733b3f2355");

        saveGroupToDatabase(groupId, groupName, imageUri.toString());


    }

    private void saveGroupToDatabase(String groupId, String groupName, String groupIconUrl) {
        HashMap<String, Object> groupMap = new HashMap<>();
        groupMap.put("groupId", groupId);
        groupMap.put("groupName", groupName);
        groupMap.put("groupIcon", groupIconUrl);
        groupMap.put("createdBy", auth.getUid());
        groupMap.put("createdAt", System.currentTimeMillis());
        groupMap.put("members", getSelectedUserIds());

        database.getReference("groups").child(groupId)
                .setValue(groupMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreateGroupActivity.this, "Group Created Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateGroupActivity.this, "Failed to create group", Toast.LENGTH_SHORT).show();
                });
    }

    private ArrayList<String> getSelectedUserIds() {
        ArrayList<String> userIds = new ArrayList<>();
        for (Users user : friendsList) {
            if (selectedFriends.contains(user.getUserId())) {
                userIds.add(user.getUserId());
            }
        }
        userIds.add(auth.getUid()); // Add creator's ID to the group
        return userIds;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}