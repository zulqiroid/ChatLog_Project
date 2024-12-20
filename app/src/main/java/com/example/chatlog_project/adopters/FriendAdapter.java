package com.example.chatlog_project.adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlog_project.R;
import com.example.chatlog_project.activities.CreateGroupActivity;
import com.example.chatlog_project.models.MessageModel;
import com.example.chatlog_project.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {



    public interface SnapsSendCallback {
        void onComplete();

        void onError(String errorMessage);
    }

    private Context context;
    private ArrayList<Users> friendsList;
    private ArrayList<String> selectedFriends;
    private AppCompatButton sendBtn;
    private SnapsSendCallback callback;

    public FriendAdapter(CreateGroupActivity context, ArrayList<Users> friendsList, ArrayList<String> selectedFriends, AppCompatButton createGroupBtn) {
        this.context = context;
        this.friendsList = friendsList;
        this.selectedFriends = selectedFriends;
        this.sendBtn = createGroupBtn;
    }

    public FriendAdapter(Context context, ArrayList<Users> friendsList, ArrayList<String> selectedFriends,
                          AppCompatButton sendBtn, SnapsSendCallback callback) {
        this.context = context;
        this.friendsList = friendsList;
        this.selectedFriends = selectedFriends;
        this.sendBtn = sendBtn;
        this.callback = callback; // Callback for notifying the activity
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Users friend = friendsList.get(position);

        holder.name.setText(friend.getName());
        Picasso.get().load(friend.getProfile_image()).into(holder.profilePicture);

        // Manage checkbox state
        holder.checkBox.setOnCheckedChangeListener(null); // Prevent infinite loops
        holder.checkBox.setChecked(selectedFriends.contains(friend.getUserId()));

        // Handle checkbox selection
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedFriends.add(friend.getUserId());
            } else {
                selectedFriends.remove(friend.getUserId());
            }

            // Toggle Send Button visibility based on selection
            if (selectedFriends.isEmpty()) {
                sendBtn.setVisibility(View.GONE);
            } else {
                sendBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    // Method to send snaps to selected friends
    @SuppressLint("NotifyDataSetChanged")
    public void sendSnapsToSelectedFriends(FirebaseDatabase database, FirebaseAuth auth, Uri uri, SnapsSendCallback snapsSendCallback) {
        if (selectedFriends.isEmpty()) {
            snapsSendCallback.onError("No friends selected.");
            return;
        }

        String senderId = auth.getUid();
        Calendar calendar = Calendar.getInstance();

        // Create a copy of selectedFriends to avoid out-of-bounds error
        ArrayList<String> friendsToSend = new ArrayList<>(selectedFriends);

        for (String receiverId : friendsToSend) {
            String senderRoom = senderId + receiverId;
            String receiverRoom = receiverId + senderId;

            // Upload the image to Firebase Storage
            StorageReference storage = FirebaseStorage.getInstance().getReference()
                    .child("chats")
                    .child(calendar.getTimeInMillis() + "");
            storage.putFile(uri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    storage.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String filePath = downloadUri.toString();
                        String message = "send you a snap";

                        Date date = calendar.getTime();
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        String currentDate = formatter.format(date);

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
                        String currentTime = timeFormatter.format(date);
                        String timestamp = currentDate + " , " + currentTime;

                        // Create message model
                        MessageModel messageModel = new MessageModel(senderId, receiverId, message, currentTime, currentDate, timestamp, "snap");
                        messageModel.setMessage("Snap \uD83D\uDCF8");
                        messageModel.setImageUrl(filePath);

                        String randomKey = database.getReference().push().getKey();

                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                        lastMsgObj.put("lastMsg", messageModel.getMessage());
                        lastMsgObj.put("lastMsgTime", currentTime);

                        // Update sender and receiver chats
                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                        database.getReference().child("chats")
                                .child(senderRoom)
                                .child("message")
                                .child(randomKey)
                                .setValue(messageModel)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        database.getReference()
                                                .child("chats")
                                                .child(receiverRoom)
                                                .child("message")
                                                .child(randomKey)
                                                .setValue(messageModel)
                                                .addOnCompleteListener(task2 -> {
                                                    // Trigger onComplete after processing the last friend
                                                    if (task2.isSuccessful() && receiverId.equals(friendsToSend.get(friendsToSend.size() - 1))) {
                                                        snapsSendCallback.onComplete();
                                                    }
                                                });
                                    }
                                });
                    }).addOnFailureListener(e -> snapsSendCallback.onError(e.getMessage()));
                } else {
                    snapsSendCallback.onError(task.getException().getMessage());
                }
            });
        }

        // Clear selected friends and refresh UI AFTER processing all snaps
        selectedFriends.clear();
        notifyDataSetChanged();
    }


    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView profilePicture;
        CheckBox checkBox;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.friendName);
            profilePicture = itemView.findViewById(R.id.friendProfilePicture);
            checkBox = itemView.findViewById(R.id.friendCheckBox);
        }
    }
}
