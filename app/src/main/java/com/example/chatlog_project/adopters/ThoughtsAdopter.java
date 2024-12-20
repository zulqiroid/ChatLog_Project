package com.example.chatlog_project.adopters;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlog_project.R;
import com.example.chatlog_project.activities.MainActivity;
import com.example.chatlog_project.activities.Show_user_thoughts;
import com.example.chatlog_project.activities.View_tought;
import com.example.chatlog_project.models.Status;
import com.example.chatlog_project.models.UserStatus;
import com.example.chatlog_project.models.UserThought;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class ThoughtsAdopter extends RecyclerView.Adapter<ThoughtsAdopter.ViewHolder>{
    Context mainActivity;
    ArrayList<UserThought> userThoughts;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public ThoughtsAdopter(Context mainActivity, ArrayList<UserThought> userThoughts) {
        this.mainActivity = mainActivity;
        this.userThoughts = userThoughts;
    }

    @NonNull
    @Override
    public ThoughtsAdopter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.thoughts_user_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserThought userThought = userThoughts.get(position);
        String userId = auth.getUid();
        FirebaseDatabase.getInstance().getReference().child("Thoughts");

        holder.name.setText(userThought.getName());
        Picasso.get().load(userThought.getProfileImage()).into(holder.profile_image);
        holder.any_thought_text.setText(userThought.getThought());
        holder.any_thought_text.setPadding(25, 25, 25, 25);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, Show_user_thoughts.class);
                intent.putExtra("profile_image", userThought.getProfileImage());
                intent.putExtra("name", userThought.getName());
                intent.putExtra("thought", userThought.getThought());
                intent.putExtra("time", userThought.getTime());
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) mainActivity, holder.thought_profile_section, "thought");
                mainActivity.startActivity(intent, options.toBundle());
            }
        });


    }


    @Override
    public int getItemCount() {
        return userThoughts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView any_thought_text;
        TextView name;
        LinearLayout thought_profile_section;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            any_thought_text = itemView.findViewById(R.id.any_thought_text);
            name = itemView.findViewById(R.id.name);
            thought_profile_section = itemView.findViewById(R.id.thought_profile_section);
        }
    }
}
