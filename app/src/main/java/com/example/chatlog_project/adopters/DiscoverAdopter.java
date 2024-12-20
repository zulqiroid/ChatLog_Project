package com.example.chatlog_project.adopters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlog_project.R;
import com.example.chatlog_project.activities.User_profile_forRequest;
import com.example.chatlog_project.activities.chat_win;
import com.example.chatlog_project.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscoverAdopter extends RecyclerView.Adapter<DiscoverAdopter.ViewHolder>{
    Context mainActivity;
    ArrayList<Users> usersArrayList;

    public DiscoverAdopter() {
    }

    public DiscoverAdopter(Context mainActivity, ArrayList<Users> usersArrayList) {
        this.mainActivity = mainActivity;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.discover_user_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverAdopter.ViewHolder holder, int position) {
        Users users = usersArrayList.get(position);
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + users.getUserId();
        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom);

        holder.user_name.setText(users.getName());
        Picasso.get().load(users.getProfile_image()).into(holder.profile_image);
        holder.profile_image.setContentDescription(users.getUsername() + "'s profile image");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, User_profile_forRequest.class);
                intent.putExtra("uid", users.getUserId());
                mainActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView user_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            user_name = itemView.findViewById(R.id.user_name);

        }
    }
}
