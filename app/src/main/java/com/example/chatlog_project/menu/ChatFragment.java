package com.example.chatlog_project.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.chatlog_project.R;
import com.example.chatlog_project.activities.MainActivity;
import com.example.chatlog_project.activities.Show_user_thoughts;
import com.example.chatlog_project.activities.View_tought;
import com.example.chatlog_project.adopters.GroupAdapter;
import com.example.chatlog_project.adopters.ThoughtsAdopter;
import com.example.chatlog_project.adopters.UserAdopter;
import com.example.chatlog_project.models.UserThought;
import com.example.chatlog_project.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseDatabase databse;
    FirebaseAuth auth;
    UserAdopter adopter;
    ArrayList<Users> usersArrayList;
    RecyclerView recycler_view_chats, recycler_view_thoughts, recycler_view_groups;
    ShimmerRecyclerView shimmerRecyclerView;
    LinearLayout thought_profile_section_uploader;
    private String mParam1;
    private String mParam2;
    String profile_Image_url;
    CircleImageView profile_image;
    TextView any_thought_text, btn_chat, btn_group;
    ThoughtsAdopter thoughtsAdopter;
    ArrayList<UserThought> userThoughts;
    ProgressDialog progressDialog;
    GroupAdapter groupAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        btn_chat = view.findViewById(R.id.btn_chat);
        btn_group = view.findViewById(R.id.btn_group);
        recycler_view_groups = view.findViewById(R.id.recycler_view_groups);
        {   //finding views for chat sention
            shimmerRecyclerView = view.findViewById(R.id.recycler_view_chats);
            shimmerRecyclerView.showShimmerAdapter();
            recycler_view_chats = view.findViewById(R.id.recycler_view_chats);
            recycler_view_chats.setHasFixedSize(true);
        }
        {   //finding views for thought sention
            shimmerRecyclerView = view.findViewById(R.id.recycler_view_thoughts);
            shimmerRecyclerView.showShimmerAdapter();
            recycler_view_thoughts = view.findViewById(R.id.recycler_view_thoughts);
            thought_profile_section_uploader = view.findViewById(R.id.thought_profile_section_uploader);
            profile_image = view.findViewById(R.id.profile_image);
            any_thought_text = view.findViewById(R.id.any_thought_text);
            recycler_view_thoughts.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recycler_view_thoughts.setLayoutManager(layoutManager);
        }

        thoughts();

        thought_profile_section_uploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userid = auth.getUid();
                databse.getReference().child("Thoughts").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            showBottomSheetDialog(view);
                        } else {

                            Intent intent = new Intent(getContext(), View_tought.class);
                            intent.putExtra("profile_image", profile_Image_url);
                            // Apply the explode transition
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    getActivity(), view.findViewById(R.id.thought_profile_section_uploader), "thought"); // Replace with a unique transition name
                            startActivity(intent, options.toBundle());
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });

            }
        });

        chats();

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler_view_chats.setVisibility(View.VISIBLE);
                recycler_view_groups.setVisibility(View.GONE);
                btn_chat.setBackgroundResource(R.drawable.selected_chat_group_bg);
                btn_group.setBackgroundResource(R.drawable.any_thought_background);
                btn_group.setTextColor(getResources().getColor(R.color.color3));
                btn_chat.setTextColor(getResources().getColor(R.color.black));
                chats();
            }
        });
        btn_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler_view_chats.setVisibility(View.GONE);
                recycler_view_groups.setVisibility(View.VISIBLE);
                btn_chat.setBackgroundResource(R.drawable.any_thought_background);
                btn_group.setBackgroundResource(R.drawable.selected_chat_group_bg);
                btn_group.setTextColor(getResources().getColor(R.color.black));
                btn_chat.setTextColor(getResources().getColor(R.color.color3));
                groups();
            }
        });

        return view;
    }

    private void showBottomSheetDialog(View view) {
        // Create the BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());

        // Set the custom layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_thought, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Find and set click listeners for the options
        LinearLayout viewThought = bottomSheetView.findViewById(R.id.btn_view_thought);
        LinearLayout deleteThought = bottomSheetView.findViewById(R.id.btn_delete_thought);
        LinearLayout cancel = bottomSheetView.findViewById(R.id.btn_cancel);

        viewThought.setOnClickListener(v -> {
            // Handle View Thought action
            viewThought(view);
            bottomSheetDialog.dismiss();
        });

        deleteThought.setOnClickListener(v -> {
            // Handle Delete Thought action
            deleteThought(view);
            bottomSheetDialog.dismiss();
        });

        cancel.setOnClickListener(v -> {
            // Dismiss the dialog
            bottomSheetDialog.dismiss();
        });

        // Show the BottomSheetDialog
        bottomSheetDialog.show();
    }

    private void viewThought(View view) {
        DatabaseReference reference = databse.getReference().child("Thoughts").child(auth.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> dataMap = (Map<String, Object>) snapshot.getValue();
                    if (dataMap != null) {
                        profile_Image_url = (String) dataMap.get("profileImage");
                        String name = (String) dataMap.get("name");
                        String thought = (String) dataMap.get("thought");
                        String time = (String) dataMap.get("time");

                        Intent intent = new Intent(requireContext(), Show_user_thoughts.class);
                        intent.putExtra("profile_image", profile_Image_url);
                        intent.putExtra("name", name);
                        intent.putExtra("thought", thought);
                        intent.putExtra("time", time);

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(), view.findViewById(R.id.thought_profile_section_uploader), "thought");
                        startActivity(intent, options.toBundle());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void deleteThought(View view) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Deleting...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String userid = auth.getUid();
        databse.getReference().child("Thoughts").child(userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), view.findViewById(R.id.thought_profile_section_uploader), "intent"); // Replace with a unique transition name
                startActivity(intent, options.toBundle());
                getActivity().finish();
                progressDialog.dismiss();
            }
        });


    }


    public void thoughts() {
        auth = FirebaseAuth.getInstance();
        databse = FirebaseDatabase.getInstance();
        DatabaseReference reference = databse.getReference().child("user").child(auth.getUid()).child("profile_image");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profile_Image_url = snapshot.getValue(String.class);
                if (profile_Image_url != null) {
                    // Use Picasso to load the image into the CircleImageView
                    Picasso.get().load(profile_Image_url).placeholder(R.drawable.user_w).into(profile_image);
                } else {
                    Toast.makeText(getContext(), "No image found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference ref = databse.getReference().child("Thoughts").child(auth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String text = snapshot.child("thought").getValue(String.class);
                if (text != null) {
                    any_thought_text.setText(text);
                    any_thought_text.setPadding(25, 25, 25, 25);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        thoughtsAdopter = new ThoughtsAdopter(getContext(), new ArrayList<>());

        DatabaseReference refer = databse.getReference().child("Thoughts");

        userThoughts = new ArrayList<>();
        refer.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userThoughts.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserThought userThought = dataSnapshot.getValue(UserThought.class);
                    assert userThought != null;
                    if (!userThought.getUploader_id().equals(auth.getUid())) {
                        userThoughts.add(userThought);
                    }
                }
                thoughtsAdopter = new ThoughtsAdopter(getContext(), userThoughts);
                recycler_view_thoughts.setAdapter(thoughtsAdopter);
                shimmerRecyclerView.hideShimmerAdapter();
                thoughtsAdopter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }

    private void chats() {
        databse = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        adopter = new UserAdopter(getContext(), new ArrayList<>());
        usersArrayList = new ArrayList<>();

        String currentUserId = auth.getUid();

        // Reference to the friends node of the current user
        DatabaseReference friendsRef = databse.getReference().child("friends").child(currentUserId);

        // Fetch friends list
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> friendsList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    friendsList.add(dataSnapshot.getKey()); // Add friend IDs to the list
                }

                // Reference to all users
                DatabaseReference usersRef = databse.getReference().child("user");

                // Fetch user details for the friends
                usersRef.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersArrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Users user = dataSnapshot.getValue(Users.class);
                            if (user != null && friendsList.contains(user.getUserId())) {
                                usersArrayList.add(user); // Add only friends to the list
                            }
                        }

                        adopter = new UserAdopter(getContext(), usersArrayList);
                        recycler_view_chats.setAdapter(adopter);
                        shimmerRecyclerView.hideShimmerAdapter();
                        adopter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error loading users", Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading friends", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void groups() {
        recycler_view_groups.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<HashMap<String, Object>> groupList = new ArrayList<>();


        String currentUserId = FirebaseAuth.getInstance().getUid();
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");

        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    HashMap<String, Object> group = (HashMap<String, Object>) groupSnapshot.getValue();
                    ArrayList<String> members = (ArrayList<String>) group.get("members");

                    if (members != null && members.contains(currentUserId)) {
                        groupList.add(group);
                    }
                }
                groupAdapter = new GroupAdapter(getContext(), groupList);
                recycler_view_groups.setAdapter(groupAdapter);
                shimmerRecyclerView.hideShimmerAdapter();
                groupAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load groups", Toast.LENGTH_SHORT).show();
            }
        });
    }


}