package com.example.chatlog_project.adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.chatlog_project.R;
import com.example.chatlog_project.activities.MainActivity;
import com.example.chatlog_project.models.Status;
import com.example.chatlog_project.models.UserStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdopter extends RecyclerView.Adapter<StatusAdopter.StatusViewHolder> {
    Context context;
    ArrayList<UserStatus> userStatuses;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    UserStatus userStatus;
    public StatusAdopter(){
    }

    public StatusAdopter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        UserStatus userStatus = userStatuses.get(position);

        Picasso.get().load( userStatus.getProfileImage()).into(holder.profile_image);
        if (userStatus.getUploader_id() != null && userStatus.getUploader_id().equals(auth.getUid())) {

            holder.user_name.setText("My Status"); // Display "My Status"
        } else {
            holder.user_name.setText(userStatus.getName());

        }
        // Set text to user's name
        holder.circular_status_view.setPortionsCount(userStatus.getStatuses().size());
        holder.layout_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status : userStatus.getStatuses()){
                    myStories.add(new MyStory(status.getImageURL()));
                }
                DateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String formattedDate = dateFormat.format(new Date(userStatus.getLastUpdated()));
                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName()) // Default is Hidden
                        .setSubtitleText(formattedDate) // Default is Hidden
                        .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateStatuses(ArrayList<UserStatus> updatedStatuses) {
        this.userStatuses = updatedStatuses;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        CircularStatusView circular_status_view;
        TextView user_name;
        RelativeLayout layout_status;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            circular_status_view = itemView.findViewById(R.id.circular_status_view);
            user_name = itemView.findViewById(R.id.user_name);
            layout_status = itemView.findViewById(R.id.layout_status);
        }
    }
    }
