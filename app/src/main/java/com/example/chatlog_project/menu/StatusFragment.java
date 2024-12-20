package com.example.chatlog_project.menu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.devlomi.circularstatusview.CircularStatusView;
import com.example.chatlog_project.R;
import com.example.chatlog_project.activities.MainActivity;
import com.example.chatlog_project.activities.StatusCleanupWorker;
import com.example.chatlog_project.adopters.StatusAdopter;
import com.example.chatlog_project.models.Status;
import com.example.chatlog_project.models.UserStatus;
import com.example.chatlog_project.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.model.MyStory;

public class StatusFragment extends Fragment {
    StatusAdopter statusAdopter;
    ArrayList<UserStatus> statusesArrayList= new ArrayList<>();
    ShimmerRecyclerView shimmerRecyclerView;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseDatabase databse;
    Users users;
    FloatingActionButton floatingActionButton;
    RecyclerView recycler_view_status;
    CircleImageView profile_image;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public StatusFragment() {
        // Required empty public constructor
    }

    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
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

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        auth = FirebaseAuth.getInstance();
        databse = FirebaseDatabase.getInstance();
        String user_id = auth.getUid();
        profile_image = view.findViewById(R.id.profile_image);
        databse.getReference().child("user").child(user_id).child("profile_image")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String profileImageUrl = dataSnapshot.getValue(String.class);
                        if (profileImageUrl != null) {
                            // Use the profileImageUrl to display the image
                            // For example, using Picasso:
                            Picasso.get().load(profileImageUrl).into(profile_image);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors that occur} });

                        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(StatusCleanupWorker.class, 1, TimeUnit.HOURS)
                                .build();
                        WorkManager.getInstance(getContext()).enqueueUniquePeriodicWork("statusCleanup", ExistingPeriodicWorkPolicy.KEEP, workRequest);

                        statusAdopter = new StatusAdopter(getContext(), statusesArrayList);
                        setupUploaderStatus(view, statusesArrayList);

                        FirebaseDatabase.getInstance().getReference().child("statuses")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList<UserStatus> updatedStatuses = new ArrayList<>();
                                        for (DataSnapshot userStatusSnapshot : snapshot.getChildren()) {
                                            UserStatus userStatus = userStatusSnapshot.getValue(UserStatus.class);
                                            updatedStatuses.add(userStatus);
                                        }
                                        statusAdopter.updateStatuses(updatedStatuses);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle error

                                    }
                                });
                    }
                });
        statuses(view);

        return view;
    }
    private void setupUploaderStatus(View view, ArrayList<UserStatus> userStatuses) {
        // Assuming userStatuses is a list containing all statuses
        UserStatus uploaderStatus = userStatuses.stream().filter(status -> status.getUploader_id() != null && status.getUploader_id().equals(FirebaseAuth.getInstance().getUid())).findFirst().orElse(null);

        // Find and update layout_status in your StatusFragment's view
        RelativeLayout layoutStatus = view.findViewById(R.id.layout_status);
        CircleImageView profileImage = layoutStatus.findViewById(R.id.profile_image);
        CircularStatusView circularStatusView = layoutStatus.findViewById(R.id.circular_status_view);
        if (uploaderStatus != null) {
            // Set profile image using Picasso
            Picasso.get().load(uploaderStatus.getProfileImage()).into(profileImage);
            circularStatusView.setPortionsCount(uploaderStatus.getStatuses().size());
            layoutStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<MyStory> myStories = new ArrayList<>();
                    for (Status status : uploaderStatus.getStatuses()) {
                        myStories.add(new MyStory(status.getImageURL()));
                    }
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    String formattedDate = dateFormat.format(new Date(uploaderStatus.getLastUpdated()));
                    new StoryView.Builder(getFragmentManager()) .setStoriesList(myStories)  .setStoryDuration(5000) .setTitleText("My Status") .setSubtitleText(formattedDate) .setTitleLogoUrl(uploaderStatus.getProfileImage()) .build() .show();
                } });
        } else {
            circularStatusView.setPortionsCount(0);
        }  }

    private void statuses(View view) {
        shimmerRecyclerView = view.findViewById(R.id.recycler_view_status);
        shimmerRecyclerView.showShimmerAdapter();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading Image");
        progressDialog.setCancelable(false);

        databse.getReference().child("user").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users = snapshot.getValue(Users.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        databse.getReference().child("status").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentTime = System.currentTimeMillis();
                statusesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserStatus userStatus = new UserStatus();
                    userStatus.setName(dataSnapshot.child("name").getValue(String.class));
                    userStatus.setProfileImage(dataSnapshot.child("profileImage").getValue(String.class));
                    userStatus.setLastUpdated(dataSnapshot.child("lastUpdated").getValue(Long.class));
                    userStatus.setUploader_id(dataSnapshot.getKey());

                    ArrayList<Status> validStatuses = new ArrayList<>();
                    for (DataSnapshot statusSnapshot : dataSnapshot.child("statuses").getChildren()) {
                        Status status = statusSnapshot.getValue(Status.class);
                        if (status != null && currentTime - status.getTimestamp() < 24 * 60 * 60 * 1000) {
                            validStatuses.add(status);
                        }
                    }

                    if (!validStatuses.isEmpty()) {
                        userStatus.setStatuses(validStatuses);
                        statusesArrayList.add(userStatus);
                    }
                }
                statusAdopter.updateStatuses(statusesArrayList);
                statusAdopter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setVisibility(View.VISIBLE);
        recycler_view_status = view.findViewById(R.id.recycler_view_status);
        recycler_view_status.setHasFixedSize(true);

        DatabaseReference ref = databse.getReference().child("user");

        shimmerRecyclerView.hideShimmerAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_view_status.setLayoutManager(layoutManager);
        recycler_view_status.setAdapter(statusAdopter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null) {
                progressDialog.show();
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = firebaseStorage.getReference().child("status").child(date.getTime() + "");
                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setUploader_id(auth.getUid());
                                    userStatus.setName(users.getName());
                                    userStatus.setProfileImage(users.getProfile_image());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("name", userStatus.getName());
                                    hashMap.put("profileImage", userStatus.getProfileImage());
                                    hashMap.put("lastUpdated", userStatus.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Status status = new Status( userStatus.getUploader_id(),imageUrl, userStatus.getLastUpdated());

                                    databse.getReference()
                                            .child("status")
                                            .child(auth.getUid())
                                            .updateChildren(hashMap);
                                    databse.getReference()
                                            .child("status")
                                            .child(auth.getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    statusesArrayList.clear(); // Clear the existing list
                                                    databse.getReference().child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                UserStatus userStatus = new UserStatus();
                                                                userStatus.setName(dataSnapshot.child("name").getValue(String.class));
                                                                userStatus.setProfileImage(dataSnapshot.child("profileImage").getValue(String.class));
                                                                userStatus.setLastUpdated(dataSnapshot.child("lastUpdated").getValue(Long.class));

                                                                ArrayList<Status> statuses = new ArrayList<>();
                                                                for (DataSnapshot statusSnapshot : dataSnapshot.child("statuses").getChildren()) {
                                                                    Status status = statusSnapshot.getValue(Status.class);
                                                                    statuses.add(status);
                                                                }
                                                                userStatus.setStatuses(statuses);
                                                                statusesArrayList.add(userStatus);
                                                            }
                                                            statusAdopter.updateStatuses(statusesArrayList); // Update the adapter
                                                            statusAdopter.notifyDataSetChanged(); // Notify the adapter of data changes

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                        }
                                                    });
                                                }
                                            });
                                    Toast.makeText(getContext(), "Status Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}