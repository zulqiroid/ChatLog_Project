package com.example.chatlog_project.adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlog_project.R;
import com.example.chatlog_project.activities.ProfilePopupActivity;
import com.example.chatlog_project.models.Users;
import com.example.chatlog_project.activities.chat_win;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdopter extends RecyclerView.Adapter<UserAdopter.ViewHolder> {

    Context mainActivity;
    ArrayList<Users> usersArrayList;
  private ArrayList<Users> originalUsersList; // Store the original list of users

    public UserAdopter(Context mainActivity, ArrayList<Users> usersArrayList) {
        this.mainActivity = mainActivity;
        this.usersArrayList = usersArrayList;
      this.originalUsersList = new ArrayList<>(usersArrayList); // Initialize original list
    }

    @NonNull
    @Override
    public UserAdopter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_profile_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdopter.ViewHolder holder, int position) {
        Users users = usersArrayList.get(position);
      loadChats(holder, users);

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView user_name, last_message, last_message_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            user_name = itemView.findViewById(R.id.user_name);
            last_message = itemView.findViewById(R.id.last_message);
            last_message_time = itemView.findViewById(R.id.last_message_time);

        }
    }

    public void loadChats(UserAdopter.ViewHolder holder, Users users) {
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + users.getUserId();
        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                    // Get lastMsgTime as a String
                    String lastMsgTime = snapshot.child("lastMsgTime").getValue(String.class);
                    String time = ""; // Initialize time to an empty string

                    if (lastMsg != null) {
                        if (lastMsg.equals("This message is removed.")) {
                            holder.last_message.setTypeface(null, android.graphics.Typeface.ITALIC);
                        } else {
                            holder.last_message.setTypeface(null, android.graphics.Typeface.NORMAL);
                        }
                        holder.last_message.setText(lastMsg);
                    }
                    else {
                        // Handle the case where lastMsg is null
                        holder.last_message.setText("Tap to chat"); // Or any other appropriate message
                    }
                    holder.last_message_time.setText(lastMsgTime);
                } else {
                    holder.last_message.setText("Tap to chat");
                    holder.last_message_time.setText("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.user_name.setText(users.getName());
        Picasso.get().load(users.getProfile_image()).into(holder.profile_image);
        holder.profile_image.setContentDescription(users.getUsername() + "'s profile image");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, chat_win.class);
                intent.putExtra("name", users.getName());
                intent.putExtra("profile_image", users.getProfile_image());
                intent.putExtra("uid", users.getUserId());
                intent.putExtra("token", users.getToken());
                mainActivity.startActivity(intent);
            }
        });
        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, ProfilePopupActivity.class);
                intent.putExtra("name", users.getName());
                intent.putExtra("profile_image", users.getProfile_image());
                intent.putExtra("uid", users.getUserId());
                mainActivity.startActivity(intent);
            }
        });

    }
}
