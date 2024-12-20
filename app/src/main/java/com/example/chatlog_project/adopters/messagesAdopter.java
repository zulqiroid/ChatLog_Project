package com.example.chatlog_project.adopters;


import static androidx.core.content.ContextCompat.startActivity;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.media3.common.util.UnstableApi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlog_project.AudioService.AudioService;
import com.example.chatlog_project.Common;
import com.example.chatlog_project.activities.MainActivity;
import com.example.chatlog_project.activities.SettingScreen;
import com.example.chatlog_project.activities.View_image;
import com.example.chatlog_project.activities.chat_win;
import com.example.chatlog_project.activities.splash_screen;
import com.example.chatlog_project.databinding.DeleteDialogueBinding;
import com.example.chatlog_project.models.MessageModel;
import com.example.chatlog_project.R;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class messagesAdopter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageModel> messageArrayList;
    final int ITEM_SEND = 1;
    final int ITEM_RECIEVE = 2;
    String senderRoom, receiverRoom;
    private ViewGroup parent;
    private int viewType;
    String receiver_name;
    int[] reaction = new int[]{
            R.drawable.heart,
            R.drawable.joy,
            R.drawable.hushed,
            R.drawable.cry,
            R.drawable.rage,
            R.drawable.like
    };
    Activity activityContext;
    FirebaseRemoteConfig remoteConfig;
    private ImageButton tmpBtnPlay;
    private AudioService audioService;
    private String currentAudioUrl;

    public messagesAdopter(Activity context, ArrayList<MessageModel> messageArrayList, String senderRoom, String receiverRoom, String receiver_name) {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        this.context = context.getApplicationContext();
        this.messageArrayList = messageArrayList;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
        activityContext = context;
        this.receiver_name = receiver_name;
        this.audioService = new AudioService(context);
    }

    public messagesAdopter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciever_layout, parent, false);
            return new recieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageArrayList.get(position);

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reaction)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (pos >= 0 && pos < reaction.length) {
                if (holder.getClass() == senderViewHolder.class) {
                    senderViewHolder viewHolder = (senderViewHolder) holder;
                    viewHolder.s_emoji_reaction.setImageResource(reaction[pos]);
                    viewHolder.s_emoji_reaction.setVisibility(View.VISIBLE);
                    viewHolder.itemView.setBackground(null);


                } else {
                    recieverViewHolder viewHolder = (recieverViewHolder) holder;
                    viewHolder.r_emoji_reaction.setImageResource(reaction[pos]);
                    viewHolder.r_emoji_reaction.setVisibility(View.VISIBLE);
                    viewHolder.itemView.setBackground(null);

                }
                messageModel.setReaction(pos);
                String messageId = messageModel.getMessage_id();
                if (messageId != null) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child("message")
                            .child(messageId)
                            .setValue(messageModel);
                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(receiverRoom)
                            .child("message")
                            .child(messageId)
                            .setValue(messageModel);
                }

            }

            return true;
        });


        if (holder instanceof senderViewHolder) {
            senderViewHolder viewHolder = (senderViewHolder) holder;

            bindSenderMessage(viewHolder, messageModel);
            bindSenderReaction(viewHolder, messageModel);


            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @OptIn(markerClass = UnstableApi.class)
                @Override
                public boolean onLongClick(View view) {
                    // Get the activity's root view
                    View rootView = activityContext.getWindow().getDecorView().getRootView();

                    Toolbar existingToolbar = rootView.findViewById(R.id.toolbar_layout);

                    // Inflate the custom toolbar layout
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View customToolbarView = inflater.inflate(R.layout.toolbar_onlongpress, (ViewGroup) existingToolbar.getParent(), false);
                    Toolbar customToolbar = customToolbarView.findViewById(R.id.toolbar_layout); // Get the Toolbar from the inflated layout
                    ViewGroup customToolbarParent = (ViewGroup) customToolbar.getParent();
                    if (customToolbarParent != null) {
                        customToolbarParent.removeView(customToolbar);
                    }
                    // Replace the existing toolbar with the custom toolbar
                    ViewGroup parentOfExistingToolbar = (ViewGroup) existingToolbar.getParent();
                    int index = parentOfExistingToolbar.indexOfChild(existingToolbar);
                    parentOfExistingToolbar.removeView(existingToolbar);
                    parentOfExistingToolbar.addView(customToolbar, index);

                    // Set up button click listeners for the custom toolbar
                    ImageView deleteBtn = customToolbar.findViewById(R.id.delete_btn);
                    ImageView backBtn = customToolbar.findViewById(R.id.back_btn);

                    backBtn.setOnClickListener(v -> {
                        // Restore the original toolbar
                        parentOfExistingToolbar.removeView(customToolbar);
                        parentOfExistingToolbar.addView(existingToolbar, index);
                        viewHolder.itemView.setBackground(null);
                    });
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            View v = LayoutInflater.from(context).inflate(R.layout.delete_dialogue, null);
                            TextView everyone = v.findViewById(R.id.everyone);
                            TextView delete = v.findViewById(R.id.delete);
                            TextView cancel = v.findViewById(R.id.cancel);
                            DeleteDialogueBinding binding = DeleteDialogueBinding.bind(v);
                            AlertDialog dialog = new AlertDialog.Builder(activityContext)
                                    .setTitle("Delete Message")
                                    .setView(binding.getRoot())
                                    .create();

                            if (messageModel.getType().equals("deleted from everyone")) {
                                everyone.setVisibility(View.GONE);
                                delete.setText("Delete");
                            } else {
                                everyone.setVisibility(View.VISIBLE);
                            }

                            everyone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    messageModel.setMessage("This message is removed.");
                                    messageModel.setType("deleted from everyone");
                                    messageModel.setReaction(-1);
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("chats")
                                            .child(senderRoom)
                                            .child("message")
                                            .child(messageModel.getMessage_id()).setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("chats")
                                                            .child(senderRoom)
                                                            .child("message") // Access the "message" node
                                                            .orderByKey()
                                                            .limitToLast(2) // Get the last two messages
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                                                        // Check if there are messages in the chat

                                                                        if (snapshot.getChildrenCount() == 1) {
                                                                            // Only one message left, set lastMsg to "Tap to chat"
                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                    .child("chats")
                                                                                    .child(senderRoom)
                                                                                    .child("lastMsg")
                                                                                    .setValue("Tap to chat");
                                                                        } else {
                                                                            // More than one message, get the second-to-last message
                                                                            Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                                                                            iterator.next(); // Skip the last message (the one being deleted)
                                                                            DataSnapshot previousMessageSnapshot = iterator.next();
                                                                            MessageModel previousMessage = previousMessageSnapshot.getValue(MessageModel.class);

                                                                            // Update lastMsg with the previous message
                                                                            if (previousMessage != null) {
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(senderRoom)
                                                                                        .child("lastMsg")
                                                                                        .setValue(previousMessage.getMessage());
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(senderRoom)
                                                                                        .child("lastMsgTime")
                                                                                        .setValue(previousMessage.getTime());
                                                                            } else {
                                                                                // Handle case where previous message is null (shouldn't happen ideally)
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(senderRoom)
                                                                                        .child("lastMsg")
                                                                                        .setValue("Tap to chat");
                                                                            }
                                                                        }
                                                                    } else {
                                                                        // No messages in the chat, set lastMsg to "Tap to chat"
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("chats")
                                                                                .child(senderRoom)
                                                                                .child("lastMsg")
                                                                                .setValue("Tap to chat");
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    // Handle onCancelled event
                                                                }
                                                            });
                                                }
                                            });

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("chats")
                                            .child(receiverRoom)
                                            .child("message")
                                            .child(messageModel.getMessage_id()).setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("chats")
                                                            .child(receiverRoom)
                                                            .child("message") // Access the "message" node
                                                            .orderByKey()
                                                            .limitToLast(2) // Get the last two messages
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                                                        // Check if there are messages in the chat

                                                                        if (snapshot.getChildrenCount() == 1) {
                                                                            // Only one message left, set lastMsg to "Tap to chat"
                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                    .child("chats")
                                                                                    .child(receiverRoom)
                                                                                    .child("lastMsg")
                                                                                    .setValue("Tap to chat");
                                                                        } else {
                                                                            // More than one message, get the second-to-last message
                                                                            Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                                                                            iterator.next(); // Skip the last message (the one being deleted)
                                                                            DataSnapshot previousMessageSnapshot = iterator.next();
                                                                            MessageModel previousMessage = previousMessageSnapshot.getValue(MessageModel.class);

                                                                            // Update lastMsg with the previous message
                                                                            if (previousMessage != null) {
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(receiverRoom)
                                                                                        .child("lastMsg")
                                                                                        .setValue(previousMessage.getMessage());
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(receiverRoom)
                                                                                        .child("lastMsgTime")
                                                                                        .setValue(previousMessage.getTime());
                                                                            } else {
                                                                                // Handle case where previous message is null (shouldn't happen ideally)
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(receiverRoom)
                                                                                        .child("lastMsg")
                                                                                        .setValue("Tap to chat");
                                                                            }
                                                                        }
                                                                    } else {
                                                                        // No messages in the chat, set lastMsg to "Tap to chat"
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("chats")
                                                                                .child(receiverRoom)
                                                                                .child("lastMsg")
                                                                                .setValue("Tap to chat");
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    // Handle onCancelled event

                                                                }
                                                            });
                                                }
                                            });
                                    dialog.dismiss();
                                    parentOfExistingToolbar.removeView(customToolbar);
                                    parentOfExistingToolbar.addView(existingToolbar, index);
                                    viewHolder.itemView.setBackground(null);
                                }
                            });

                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("chats")
                                            .child(senderRoom)
                                            .child("message")
                                            .child(messageModel.getMessage_id()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("chats")
                                                            .child(senderRoom)
                                                            .child("message") // Access the "message" node
                                                            .orderByKey()
                                                            .limitToLast(2) // Get the last two messages
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                                                        // Check if there are messages in the chat

                                                                        if (snapshot.getChildrenCount() == 1) {
                                                                            // Only one message left, set lastMsg to "Tap to chat"
                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                    .child("chats")
                                                                                    .child(senderRoom)
                                                                                    .child("lastMsg")
                                                                                    .setValue("Tap to chat");
                                                                        } else {
                                                                            // More than one message, get the second-to-last message
                                                                            Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                                                                            iterator.next(); // Skip the last message (the one being deleted)
                                                                            DataSnapshot previousMessageSnapshot = iterator.next();
                                                                            MessageModel previousMessage = previousMessageSnapshot.getValue(MessageModel.class);

                                                                            // Update lastMsg with the previous message
                                                                            if (previousMessage != null) {
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(senderRoom)
                                                                                        .child("lastMsg")
                                                                                        .setValue(previousMessage.getMessage());
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(senderRoom)
                                                                                        .child("lastMsgTime")
                                                                                        .setValue(previousMessage.getTime());
                                                                            } else {
                                                                                // Handle case where previous message is null (shouldn't happen ideally)
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(senderRoom)
                                                                                        .child("lastMsg")
                                                                                        .setValue("Tap to chat");
                                                                            }
                                                                        }
                                                                    } else {
                                                                        // No messages in the chat, set lastMsg to "Tap to chat"
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("chats")
                                                                                .child(senderRoom)
                                                                                .child("lastMsg")
                                                                                .setValue("Tap to chat");
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    // Handle onCancelled event

                                                                }
                                                            });
                                                }
                                            });
                                    dialog.dismiss();
                                    parentOfExistingToolbar.removeView(customToolbar); // Remove the custom toolbar
                                    parentOfExistingToolbar.addView(existingToolbar, index); // Add back the original toolbar
                                    viewHolder.itemView.setBackground(null); // Reset background
                                }
                            });

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    parentOfExistingToolbar.removeView(customToolbar);
                                    parentOfExistingToolbar.addView(existingToolbar, index);
                                    viewHolder.itemView.setBackground(null);
                                }
                            });

                            dialog.show();

                        }
                    });
                    Drawable selectedBackground = ContextCompat.getDrawable(context, R.drawable.selected_background);
                    viewHolder.itemView.setBackground(selectedBackground);
                    popup.onTouch(view, MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                    return true; // Consume the long click event
                }
            });

        } else {

            recieverViewHolder viewHolder = (recieverViewHolder) holder;

            bindReceiverMessage(viewHolder, messageModel);

            bindReceiverReaction(viewHolder, messageModel);

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() { // Add onLongClickListener
                @OptIn(markerClass = UnstableApi.class)
                @Override
                public boolean onLongClick(View view) {
                    // Get the activity's root view
                    View rootView = activityContext.getWindow().getDecorView().getRootView();

                    Toolbar existingToolbar = rootView.findViewById(R.id.toolbar_layout);

                    // Inflate the custom toolbar layout
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View customToolbarView = inflater.inflate(R.layout.toolbar_onlongpress, (ViewGroup) existingToolbar.getParent(), false);
                    Toolbar customToolbar = customToolbarView.findViewById(R.id.toolbar_layout); // Get the Toolbar from the inflated layout
                    ViewGroup customToolbarParent = (ViewGroup) customToolbar.getParent();
                    if (customToolbarParent != null) {
                        customToolbarParent.removeView(customToolbar);
                    }
                    // Replace the existing toolbar with the custom toolbar
                    ViewGroup parentOfExistingToolbar = (ViewGroup) existingToolbar.getParent();
                    int index = parentOfExistingToolbar.indexOfChild(existingToolbar);
                    parentOfExistingToolbar.removeView(existingToolbar);
                    parentOfExistingToolbar.addView(customToolbar, index);

                    // Set up button click listeners for the custom toolbar
                    ImageView deleteBtn = customToolbar.findViewById(R.id.delete_btn);
                    ImageView backBtn = customToolbar.findViewById(R.id.back_btn);

                    backBtn.setOnClickListener(v -> {
                        // Restore the original toolbar
                        parentOfExistingToolbar.removeView(customToolbar);
                        parentOfExistingToolbar.addView(existingToolbar, index);
                        viewHolder.itemView.setBackground(null);
                    });
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            View v = LayoutInflater.from(context).inflate(R.layout.delete_dialogue, null);
                            TextView everyone = v.findViewById(R.id.everyone);
                            TextView delete = v.findViewById(R.id.delete);
                            TextView cancel = v.findViewById(R.id.cancel);
                            DeleteDialogueBinding binding = DeleteDialogueBinding.bind(v);
                            AlertDialog dialog = new AlertDialog.Builder(activityContext)
                                    .setTitle("Delete Message")
                                    .setView(binding.getRoot())
                                    .create();

                            everyone.setVisibility(View.GONE);
                            delete.setText("Delete");

                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("chats")
                                            .child(senderRoom)
                                            .child("message")
                                            .child(messageModel.getMessage_id())
                                            .setValue(null)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("chats")
                                                            .child(senderRoom)
                                                            .child("message") // Access the "message" node
                                                            .orderByKey()
                                                            .limitToLast(2) // Get the last two messages
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                                                        // Check if there are messages in the chat

                                                                        if (snapshot.getChildrenCount() == 1) {
                                                                            // Only one message left, set lastMsg to "Tap to chat"
                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                    .child("chats")
                                                                                    .child(senderRoom)
                                                                                    .child("lastMsg")
                                                                                    .setValue("Tap to chat");
                                                                        } else {
                                                                            // More than one message, get the second-to-last message
                                                                            Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                                                                            iterator.next(); // Skip the last message (the one being deleted)
                                                                            DataSnapshot previousMessageSnapshot = iterator.next();
                                                                            MessageModel previousMessage = previousMessageSnapshot.getValue(MessageModel.class);

                                                                            // Update lastMsg with the previous message
                                                                            if (previousMessage != null) {
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(senderRoom)
                                                                                        .child("lastMsg")
                                                                                        .setValue(previousMessage.getMessage());
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(senderRoom)
                                                                                        .child("lastMsgTime")
                                                                                        .setValue(previousMessage.getTime());
                                                                            } else {
                                                                                // Handle case where previous message is null (shouldn't happen ideally)
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("chats")
                                                                                        .child(senderRoom)
                                                                                        .child("lastMsg")
                                                                                        .setValue("Tap to chat");
                                                                            }
                                                                        }
                                                                    } else {
                                                                        // No messages in the chat, set lastMsg to "Tap to chat"
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("chats")
                                                                                .child(senderRoom)
                                                                                .child("lastMsg")
                                                                                .setValue("Tap to chat");
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    // Handle onCancelled event

                                                                }
                                                            });
                                                }
                                            });
                                    dialog.dismiss();
                                    parentOfExistingToolbar.removeView(customToolbar);
                                    parentOfExistingToolbar.addView(existingToolbar, index);
                                    viewHolder.itemView.setBackground(null);
                                }
                            });


                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    parentOfExistingToolbar.removeView(customToolbar);
                                    parentOfExistingToolbar.addView(existingToolbar, index);
                                    viewHolder.itemView.setBackground(null);
                                }
                            });

                            dialog.show();

                        }
                    });
                    Drawable selectedBackground = ContextCompat.getDrawable(context, R.drawable.selected_background);
                    viewHolder.itemView.setBackground(selectedBackground);
                    popup.onTouch(view, MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                    return true; // Consume the long click event
                }
            });
        }
    }

    private void bindSenderMessage(senderViewHolder viewHolder, MessageModel messageModel) {
        viewHolder.sender_message.setVisibility(View.VISIBLE);
        viewHolder.sender_message.setText(messageModel.getMessage());
        String stime = messageModel.getTime();
        viewHolder.sender_time.setText(stime);
        if (messageModel.getType().equals("photo")) {
            viewHolder.sender_message.setVisibility(View.GONE);
            viewHolder.sender_image.setVisibility(View.VISIBLE);
            Picasso.get().load(messageModel.getImageUrl()).placeholder(R.drawable.placeholder_image).into(viewHolder.sender_image);
            viewHolder.sender_time.setText(stime);
            viewHolder.sender_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    viewHolder.sender_image.invalidate();
                    Drawable drawable = viewHolder.sender_image.getDrawable();
                    // Ensure the drawable is not null and is an instance of BitmapDrawable
                    if (drawable instanceof BitmapDrawable) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        Common.IMAGE_BITMAP = bitmap;

                        if (activityContext instanceof Activity) {
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) activityContext, viewHolder.sender_image, "image");
                            Intent intent = new Intent(activityContext, View_image.class);
                            intent.putExtra("user name", receiver_name);
                            intent.putExtra("time", stime);
                            activityContext.startActivity(intent, Objects.requireNonNull(options.toBundle()));
                        } else {
                            Toast.makeText(activityContext, "Context is not an Activity", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activityContext, "Unable to extract image", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        else if (messageModel.getType().equals("voice")) {
            viewHolder.layout1.setVisibility(View.GONE);
            viewHolder.layout2.setVisibility(View.VISIBLE);
            viewHolder.voice_time.setText(stime);
            // Assuming messageModel.getDuration() is in the format "00:00" (MM:SS)
            String durationString = messageModel.getDuration();
            String[] parts = durationString.split(":");
            if (parts.length == 2) {
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                long totalMillis = (minutes * 60 + seconds) * 1000;

                viewHolder.duration.setBase(SystemClock.elapsedRealtime() - totalMillis);
                viewHolder.duration.setFormat("%s"); // Display in seconds
            } else {
                Toast.makeText(context, "false duration", Toast.LENGTH_SHORT).show();
            }
            viewHolder.play_pause_btn.setOnClickListener(v -> {
                String messageUrl = messageModel.getMessage();

                if (audioService.isPlaying(messageUrl)) {
                    // Pause current playing audio
                    audioService.pauseAudio();
                    viewHolder.duration.stop();
                    viewHolder.play_pause_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.play_icon_w));
                } else {
                    // Stop any currently playing audio
                    if (audioService.isPlaying(currentAudioUrl)) {
                        audioService.stopAudio();
                        viewHolder.duration.stop();
                        tmpBtnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.play_icon_w));
                    }

                    // Play the selected audio
                    audioService.playAudioFromURL(messageUrl, new AudioService.OnPlayCallBack() {
                        @Override
                        public void onFinished() {
                            viewHolder.play_pause_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.play_icon_w));
                            viewHolder.duration.stop();
                        }
                    });
                    // Start Chronometer from 0
                    viewHolder.duration.setBase(SystemClock.elapsedRealtime());

                    // Update Chronometer periodically
                    final Handler handler = new Handler(Looper.getMainLooper());
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (audioService.isPlaying(messageUrl)) {
                                viewHolder.duration.start();
                                long currentPosition = audioService.getCurrentPosition(); // Get current position in milliseconds
                                viewHolder.duration.setBase(SystemClock.elapsedRealtime() - currentPosition); // Update Chronometer
                                handler.postDelayed(this, 1); // Update every 100 milliseconds
                            }
                        }
                    };
                    handler.postDelayed(runnable, 100); // Start updating

                    viewHolder.play_pause_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_icon_w));
                }

                // Update the reference to the currently playing audio
                currentAudioUrl = messageUrl;
                tmpBtnPlay = viewHolder.play_pause_btn;
            });

        }
        else if (messageModel.getType().equals("snap")) {
            viewHolder.layout1.setVisibility(View.GONE);
            viewHolder.layout2.setVisibility(View.GONE);
            viewHolder.layout3.setVisibility(View.VISIBLE);
            if (!messageModel.getImageUrl().isEmpty()) {
                viewHolder.snapBlock.setBackground(ContextCompat.getDrawable(context, R.drawable.trues_snap_background));
                Picasso.get().load(messageModel.getImageUrl()).into(viewHolder.snap_img);
                viewHolder.layout3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.layout3.invalidate();
                        Drawable drawable = viewHolder.snap_img.getDrawable();

                        // Ensure the drawable is not null and is an instance of BitmapDrawable
                        if (drawable instanceof BitmapDrawable) {
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                            Common.IMAGE_BITMAP = bitmap;

                            if (activityContext instanceof Activity) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) activityContext, viewHolder.layout3, "image");
                                Intent intent = new Intent(activityContext, View_image.class);
                                intent.putExtra("user name", "snap");
                                activityContext.startActivity(intent, Objects.requireNonNull(options.toBundle()));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                     activityContext.finish();
                                    }
                                },500);
                            } else {
                                Toast.makeText(activityContext, "Context is not an Activity", Toast.LENGTH_SHORT).show();
                            }
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("message").child(messageModel.getMessage_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.child("imageUrl").getRef().setValue("");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }


                    }
                });
            }
            else {
                viewHolder.snapBlock.setBackground(ContextCompat.getDrawable(context, R.drawable.falses_snap_background));
                viewHolder.snap_text.setTextColor(ContextCompat.getColor(context, R.color.grey)); // Set text color to grey
                viewHolder.snap_text.setTypeface(null, android.graphics.Typeface.ITALIC); // Set text style to italic
                viewHolder.snap_text.setText("Opened");
            }


        }
        else {
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.GONE);
            viewHolder.layout3.setVisibility(View.GONE);
            viewHolder.sender_image.setVisibility(View.GONE); // Hide image view for text messages
            viewHolder.sender_message.setVisibility(View.VISIBLE); // Show text view for text messages
            if (messageModel.getMessage().equals("This message is removed.") && messageModel.getType().equals("deleted from everyone")) {
                viewHolder.sender_message.setTextColor(ContextCompat.getColor(context, R.color.grey)); // Set text color to grey
                viewHolder.sender_message.setTypeface(null, android.graphics.Typeface.ITALIC); // Set text style to italic
            } else {
                viewHolder.sender_message.setTextColor(ContextCompat.getColor(context, R.color.white)); // Reset text color
                viewHolder.sender_message.setTypeface(null, android.graphics.Typeface.NORMAL); // Reset text style
            }
        }
    }

    private void bindReceiverMessage(recieverViewHolder viewHolder, MessageModel messageModel) {
        // ... (Implementation for binding receiver message) ...

        viewHolder.reciever_message.setVisibility(View.VISIBLE);
        viewHolder.reciever_message.setText(messageModel.getMessage());
        viewHolder.reciever_time.setText(messageModel.getTime());
        String rtime = messageModel.getTime();
        if (messageModel.getMessage().equals("photo")) {
            viewHolder.reciever_image.setVisibility(View.VISIBLE);
            viewHolder.reciever_message.setVisibility(View.GONE);
            Picasso.get().load(messageModel.getImageUrl()).placeholder(R.drawable.placeholder_image).into(viewHolder.reciever_image);
            viewHolder.reciever_time.setText(rtime);
            viewHolder.reciever_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    viewHolder.reciever_image.invalidate();
                    Drawable drawable = viewHolder.reciever_image.getDrawable();
                    // Ensure the drawable is not null and is an instance of BitmapDrawable
                    if (drawable instanceof BitmapDrawable) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        Common.IMAGE_BITMAP = bitmap;

                        if (activityContext instanceof Activity) {
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) activityContext, viewHolder.reciever_image, "image");
                            Intent intent = new Intent(activityContext, View_image.class);
                            intent.putExtra("user name", receiver_name);
                            intent.putExtra("time", rtime);
                            activityContext.startActivity(intent, Objects.requireNonNull(options.toBundle()));
                        } else {
                            Toast.makeText(activityContext, "Context is not an Activity", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activityContext, "Unable to extract image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else if (messageModel.getType().equals("snap")) {
            viewHolder.layout1.setVisibility(View.GONE);
            viewHolder.layout2.setVisibility(View.GONE);
            viewHolder.layout3.setVisibility(View.VISIBLE);
            if (!messageModel.getImageUrl().isEmpty()) {
                viewHolder.snapBlock.setBackground(ContextCompat.getDrawable(context, R.drawable.trues_snap_background));
                Picasso.get().load(messageModel.getImageUrl()).into(viewHolder.snap_img);
                viewHolder.layout3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.layout3.invalidate();
                        Drawable drawable = viewHolder.snap_img.getDrawable();

                        // Ensure the drawable is not null and is an instance of BitmapDrawable
                        if (drawable instanceof BitmapDrawable) {
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                            Common.IMAGE_BITMAP = bitmap;

                            if (activityContext instanceof Activity) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) activityContext, viewHolder.layout3, "image");
                                Intent intent = new Intent(activityContext, View_image.class);
                                intent.putExtra("user name", "snap");
                                activityContext.startActivity(intent, Objects.requireNonNull(options.toBundle()));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        activityContext.finish();
                                    }
                                },500);
                            } else {
                                Toast.makeText(activityContext, "Context is not an Activity", Toast.LENGTH_SHORT).show();
                            }
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("message").child(messageModel.getMessage_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.child("imageUrl").getRef().setValue("");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }


                    }
                });
            }
            else {
                viewHolder.snapBlock.setBackground(ContextCompat.getDrawable(context, R.drawable.falses_snap_background));
                viewHolder.snap_text.setTextColor(ContextCompat.getColor(context, R.color.grey)); // Set text color to grey
                viewHolder.snap_text.setTypeface(null, android.graphics.Typeface.ITALIC); // Set text style to italic
                viewHolder.snap_text.setText("Opened");
            }


        }
        else if (messageModel.getType().equals("voice")) {
            viewHolder.layout1.setVisibility(View.GONE);
            viewHolder.layout2.setVisibility(View.VISIBLE);
            viewHolder.voice_time.setText(rtime);
            viewHolder.play_pause_btn.setOnClickListener(v -> {
                String messageUrl = messageModel.getMessage();

                if (audioService.isPlaying(messageUrl)) {
                    // Pause current playing audio
                    audioService.pauseAudio();
                    viewHolder.play_pause_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.play_icon_w));
                } else {
                    // Stop any currently playing audio
                    if (audioService.isPlaying(currentAudioUrl)) {
                        audioService.stopAudio();
                        tmpBtnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.play_icon_w));
                    }

                    // Play the selected audio
                    audioService.playAudioFromURL(messageUrl, new AudioService.OnPlayCallBack() {
                        @Override
                        public void onFinished() {
                            viewHolder.play_pause_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.play_icon_w));
                        }
                    });
                    viewHolder.play_pause_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_icon_w));
                }

                // Update the reference to the currently playing audio
                currentAudioUrl = messageUrl;
                tmpBtnPlay = viewHolder.play_pause_btn;
            });

        }
        else {
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.GONE);
            viewHolder.layout3.setVisibility(View.GONE);
            viewHolder.reciever_image.setVisibility(View.GONE); // Hide image view for text messages
            viewHolder.reciever_message.setVisibility(View.VISIBLE); // Show text view for text messages
            if (messageModel.getMessage().equals("This message is removed.") && messageModel.getType().equals("deleted from everyone")) {
                viewHolder.reciever_message.setTextColor(ContextCompat.getColor(context, R.color.grey)); // Set text color to grey
                viewHolder.reciever_message.setTypeface(null, android.graphics.Typeface.ITALIC); // Set text style to italic
            } else {
                viewHolder.reciever_message.setTextColor(ContextCompat.getColor(context, R.color.white)); // Reset text color
                viewHolder.reciever_message.setTypeface(null, android.graphics.Typeface.NORMAL); // Reset text style
            }
        }
    }

    private void bindSenderReaction(senderViewHolder viewHolder, MessageModel messageModel) {
        if (messageModel.getReaction() >= 0 && messageModel.getReaction() < reaction.length) {
            viewHolder.s_emoji_reaction.setVisibility(View.VISIBLE);
            viewHolder.s_emoji_reaction.setImageResource(reaction[messageModel.getReaction()]);
            viewHolder.itemView.setBackground(null);
        } else {
            viewHolder.s_emoji_reaction.setVisibility(View.GONE);
            viewHolder.itemView.setBackground(null);
        }
    }

    private void bindReceiverReaction(recieverViewHolder viewHolder, MessageModel messageModel) {
        if (messageModel.getReaction() >= 0 && messageModel.getReaction() < reaction.length) {
            viewHolder.r_emoji_reaction.setVisibility(View.VISIBLE);
            viewHolder.r_emoji_reaction.setImageResource(reaction[messageModel.getReaction()]);
            viewHolder.itemView.setBackground(null);
        } else {
            viewHolder.r_emoji_reaction.setVisibility(View.GONE);
            viewHolder.itemView.setBackground(null);
        }
    }

    @Override
    public int getItemCount() {
        if (messageArrayList != null) {
            return messageArrayList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= 0 && position < messageArrayList.size()) {
            MessageModel messageModel = messageArrayList.get(position);
            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messageModel.getSender())) {
                return ITEM_SEND;
            } else {
                return ITEM_RECIEVE;
            }
        } else {
            return ITEM_RECIEVE; // Default to receive if position is invalid
        }
    }

    class senderViewHolder extends RecyclerView.ViewHolder {
        TextView sender_message, sender_time, voice_time, snap_text;
        ImageView s_emoji_reaction, sender_image, snap_img;
        RelativeLayout layout, layout1, layout2, voice_here;
        ImageButton play_pause_btn;
        View bar, snapBlock;
        Chronometer duration;
        LinearLayout layout3;


        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            sender_message = itemView.findViewById(R.id.sender_message);
            s_emoji_reaction = itemView.findViewById(R.id.s_emoji_reaction);
            sender_image = itemView.findViewById(R.id.sender_image);
            layout = itemView.findViewById(R.id.sender_layout);
            sender_time = itemView.findViewById(R.id.sender_time);
            layout1 = itemView.findViewById(R.id.layout1);
            layout2 = itemView.findViewById(R.id.layout2);
            voice_here = itemView.findViewById(R.id.voice_here);
            play_pause_btn = itemView.findViewById(R.id.play_pause_btn);
            bar = itemView.findViewById(R.id.bar);
            duration = itemView.findViewById(R.id.duration);
            voice_time = itemView.findViewById(R.id.voice_time);
            layout3 = itemView.findViewById(R.id.layout3);
            snapBlock = itemView.findViewById(R.id.snapBlock);
            snap_text = itemView.findViewById(R.id.snap_text);
            snap_img = itemView.findViewById(R.id.snap_img);


        }
    }

    class recieverViewHolder extends RecyclerView.ViewHolder {

        TextView reciever_message, reciever_time, voice_time, snap_text;
        ImageView r_emoji_reaction, reciever_image, snap_img;
        RelativeLayout layout, layout1, layout2, voice_here;
        ImageButton play_pause_btn;
        View bar, snapBlock;
        Chronometer duration;
        LinearLayout layout3;

        @SuppressLint("WrongViewCast")
        public recieverViewHolder(@NonNull View itemView) {
            super(itemView);
            reciever_message = itemView.findViewById(R.id.reciever_message);
            r_emoji_reaction = itemView.findViewById(R.id.r_emoji_reaction);
            reciever_image = itemView.findViewById(R.id.reciever_image);
            layout = itemView.findViewById(R.id.reciever_layout);
            reciever_time = itemView.findViewById(R.id.receiver_time);
            layout1 = itemView.findViewById(R.id.layout1);
            layout2 = itemView.findViewById(R.id.layout2);
            voice_here = itemView.findViewById(R.id.voice_here);
            play_pause_btn = itemView.findViewById(R.id.play_pause_btn);
            bar = itemView.findViewById(R.id.bar);
            duration = itemView.findViewById(R.id.duration);
            voice_time = itemView.findViewById(R.id.voice_time);
            layout3 = itemView.findViewById(R.id.layout3);
            snapBlock = itemView.findViewById(R.id.snapBlock);
            snap_img = itemView.findViewById(R.id.snap_img);
            snap_text = itemView.findViewById(R.id.snap_text);


        }
    }
}
