package com.example.chatlog_project.menu;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.chatlog_project.R;
import com.example.chatlog_project.adopters.DiscoverAdopter;
import com.example.chatlog_project.adopters.FriendRequestAdapter;
import com.example.chatlog_project.adopters.UserAdopter;
import com.example.chatlog_project.models.FriendRequest;
import com.example.chatlog_project.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    FirebaseDatabase databse;
    FirebaseAuth auth;
    DiscoverAdopter adopter;
    ArrayList<Users> usersArrayList;

    RecyclerView recycler_view_Suggestion, recycler_view_Frquest;
    ShimmerRecyclerView shimmerRecyclerView;
    LinearLayout recycler_view_Frquest_layout;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DiscoverFragment() {
        // Required empty public constructor
    }
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_discover, container, false);

        {
            recycler_view_Frquest_layout = view.findViewById(R.id.recycler_view_Frquest_layout);
            recycler_view_Frquest = view.findViewById(R.id.recycler_view_Frquest);
        }
        {
            shimmerRecyclerView = view.findViewById(R.id.recycler_view_Suggestion);
            shimmerRecyclerView.showShimmerAdapter();
            recycler_view_Suggestion = view.findViewById(R.id.recycler_view_Suggestion);
            recycler_view_Suggestion.setHasFixedSize(true);
        }
        auth=FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        loadFriendRequests(userId);
        suggestion();


        return view;
    }

    private void suggestion() {

        databse = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        adopter = new DiscoverAdopter(getContext(), new ArrayList<>());

        DatabaseReference ref = databse.getReference().child("user");

        usersArrayList = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (!users.getUserId().equals(auth.getUid())) {
                        usersArrayList.add(users);
                    }
                }
                adopter = new DiscoverAdopter(getContext(), usersArrayList);
                recycler_view_Suggestion.setAdapter(adopter);
                shimmerRecyclerView.hideShimmerAdapter();
                adopter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }

    // Load friend requests
    private void loadFriendRequests(String userId) {
        DatabaseReference friendRequestRef = FirebaseDatabase.getInstance()
                .getReference("friendRequests")
                .child(userId);

        friendRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    recycler_view_Frquest_layout.setVisibility(View.VISIBLE);
                    List<FriendRequest> friendRequests = new ArrayList<>();
                    for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                        String senderId = requestSnapshot.getKey();
                        String status = requestSnapshot.child("status").getValue(String.class);
                        String senderName = requestSnapshot.child("userName").getValue(String.class);
                        String profileImageUrl = requestSnapshot.child("profileImageUrl").getValue(String.class);
                        Boolean isAccepted = requestSnapshot.child("accepted").getValue(Boolean.class);
                        if (isAccepted == null) {
                            isAccepted = false; // Provide a default value if the field is null
                        }
                        // You can retrieve other details as needed
                        FriendRequest friendRequest = new FriendRequest(status ,senderId, senderName, profileImageUrl, isAccepted);
                        friendRequests.add(friendRequest);
                    }
                    // Update RecyclerView with the list of friend requests
                    updateFriendRequestRecyclerView(friendRequests);
                }
                else {
                    recycler_view_Frquest_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load friend requests: " + error.getMessage());

            }
        });
    }
    private void updateFriendRequestRecyclerView(List<FriendRequest> friendRequests) {
        recycler_view_Frquest_layout.setVisibility(View.VISIBLE);
        FriendRequestAdapter adapter = new FriendRequestAdapter(friendRequests, getContext());
        recycler_view_Frquest.setAdapter(adapter);
    }
}