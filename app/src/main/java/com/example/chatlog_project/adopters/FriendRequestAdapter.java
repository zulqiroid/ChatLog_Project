package com.example.chatlog_project.adopters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlog_project.R;
import com.example.chatlog_project.models.FriendRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {
    private List<FriendRequest> friendRequests;
    private Context context;

    public FriendRequestAdapter(List<FriendRequest> friendRequests, Context context) {
        this.friendRequests = friendRequests;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.requested_user_item, parent, false);
        return new FriendRequestViewHolder(view,context ,friendRequests);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        FriendRequest friendRequest = friendRequests.get(position);
        // Bind friend request data to the UI
        holder.bind(friendRequest);
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        CircleImageView profile_image;
       private AppCompatButton accept_button, reject_button;
        private List<FriendRequest> friendRequests;
        private Context context;

        public FriendRequestViewHolder(View itemView,Context context,  List<FriendRequest> friendRequests) {
            super(itemView);
            this.context = context;
            this.friendRequests = friendRequests;

            userName = itemView.findViewById(R.id.user_name);
            profile_image = itemView.findViewById(R.id.profile_image);

            accept_button = itemView.findViewById(R.id.accept_button);
            reject_button = itemView.findViewById(R.id.reject_button);

            accept_button.setOnClickListener(v -> acceptFriendRequest());
            reject_button.setOnClickListener(v -> rejectFriendRequest());
        }

        public void bind(FriendRequest friendRequest) {
            userName.setText(friendRequest.getUserName());
            Picasso.get().load(friendRequest.getProfileImageUrl()).into(profile_image);

            // Set up accept and reject button actions
        }

        private void acceptFriendRequest() {
            int position = getAdapterPosition();
            FriendRequest friendRequest = friendRequests.get(position);
            String senderId = friendRequest.getSenderId();
            String receiverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friends");
            friendsRef.child(receiverId).child(senderId).setValue(true);
            friendsRef.child(senderId).child(receiverId).setValue(true);

            DatabaseReference friendRequestRef = FirebaseDatabase.getInstance()
                    .getReference("friendRequests")
                    .child(receiverId)
                    .child(senderId);

            friendRequestRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Friend Request Accepted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to accept request.", Toast.LENGTH_SHORT).show();
                }
            });

            // Implement the logic to accept a friend request
        }

        private void rejectFriendRequest() {
            int position = getAdapterPosition();
            FriendRequest friendRequest = friendRequests.get(position);

            String senderId = friendRequest.getSenderId();
            String receiverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference friendRequestRef = FirebaseDatabase.getInstance()
                    .getReference("friendRequests")
                    .child(receiverId)
                    .child(senderId);

            friendRequestRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Friend Request Rejected!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to reject request.", Toast.LENGTH_SHORT).show();
                        }
                    });
            // Implement the logic to reject a friend request
        }
    }
}
