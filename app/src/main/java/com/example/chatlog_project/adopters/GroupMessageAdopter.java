package com.example.chatlog_project.adopters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlog_project.R;
import com.example.chatlog_project.models.GroupMessageModel;
import com.example.chatlog_project.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class GroupMessageAdopter extends RecyclerView.Adapter {
    Context context;
    ArrayList<GroupMessageModel> groupMessageModelArrayList;
    final int ITEM_SEND = 1;
    final int ITEM_RECIEVE = 2;
    String  groupId;
    String sender_name;
    String sender_identity;

    public GroupMessageAdopter(Activity context, ArrayList<GroupMessageModel> groupMessageModelArrayList, String groupId,String sender_name, String sender_identity) {
        this.context = context;
        this.groupMessageModelArrayList = groupMessageModelArrayList;
        this.groupId=groupId;
        this.sender_name=sender_name;
        this.sender_identity=sender_identity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new senderViewHolder(view);
        } else {
            View view= LayoutInflater.from(context).inflate(R.layout.reciever_layout,parent,false);
            return new recieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupMessageModel groupMessageModel=groupMessageModelArrayList.get(position);
        if (holder instanceof senderViewHolder){
            senderViewHolder viewHolder = (senderViewHolder) holder;
            bindSenderMessage(viewHolder, groupMessageModel);
        }else {
            recieverViewHolder viewHolder = (recieverViewHolder) holder;
            bindReceiverMessage(viewHolder, groupMessageModel);

        }
    }


    private void bindSenderMessage(senderViewHolder viewHolder, GroupMessageModel groupMessageModel) {
        viewHolder.sender_message.setVisibility(View.VISIBLE);
        viewHolder.sender_message.setText(groupMessageModel.getMessage());
        String stime = groupMessageModel.getTime();
        viewHolder.sender_time.setText(stime);
        if (groupMessageModel.getType().equals("text")){
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.GONE);
            viewHolder.layout3.setVisibility(View.GONE);
            viewHolder.sender_image.setVisibility(View.GONE);
            viewHolder.sender_message.setVisibility(View.VISIBLE);
        }
    }

    private void bindReceiverMessage(recieverViewHolder viewHolder, GroupMessageModel groupMessageModel) {
        viewHolder.reciever_message.setVisibility(View.VISIBLE);
        viewHolder.reciever_message.setText(groupMessageModel.getMessage());
        String rtime = groupMessageModel.getTime();
        viewHolder.sender_name.setVisibility(View.VISIBLE);
        viewHolder.sender_name.setText(groupMessageModel.getSender_name());
        viewHolder.reciever_time.setText(rtime);
        if (groupMessageModel.getType().equals("text")){
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.GONE);
            viewHolder.reciever_image.setVisibility(View.GONE); // Hide image view for text messages
            viewHolder.reciever_message.setVisibility(View.VISIBLE); // Show text view for text messages
        }
    }

    @Override
    public int getItemCount() {
        return groupMessageModelArrayList.size();
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

        TextView reciever_message, reciever_time, voice_time, snap_text, sender_name;
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
            sender_name=itemView.findViewById(R.id.sender_name);


        }
    }
}
