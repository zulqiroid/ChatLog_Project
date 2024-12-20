package com.example.chatlog_project.adopters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlog_project.R;
import com.example.chatlog_project.activities.GroupChatActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private Context context;
    private ArrayList<HashMap<String, Object>> groupList;

    public GroupAdapter(Context context, ArrayList<HashMap<String, Object>> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        HashMap<String, Object> group = groupList.get(position);

        holder.groupName.setText(group.get("groupName").toString());
        String groupIcon = group.get("groupIcon").toString();
        Picasso.get().load(groupIcon).placeholder(R.drawable.group_w).into(holder.groupIcon);

        holder.itemView.setOnClickListener(v -> {
            // Handle group click (e.g., navigate to group chat)
           Intent intent = new Intent(context, GroupChatActivity.class);
            intent.putExtra("groupId", group.get("groupId").toString());
            intent.putExtra("groupName", group.get("groupName").toString());
            intent.putExtra("groupIcon", group.get("groupIcon").toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        ImageView groupIcon;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            groupIcon = itemView.findViewById(R.id.groupIcon);
        }
    }
}
