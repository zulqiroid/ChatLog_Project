package com.example.chatlog_project.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.cooltechworks.views.shimmer.BuildConfig;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.chatlog_project.R;
import com.example.chatlog_project.adopters.StatusAdopter;
import com.example.chatlog_project.adopters.UserAdopter;
import com.example.chatlog_project.menu.CallFragment;
import com.example.chatlog_project.menu.ChatFragment;
import com.example.chatlog_project.menu.DiscoverFragment;
import com.example.chatlog_project.menu.StatusFragment;
import com.example.chatlog_project.models.Status;
import com.example.chatlog_project.models.UserStatus;
import com.example.chatlog_project.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase databse;
    RecyclerView recycler_view_main;
    UserAdopter adopter;
    ArrayList<Users> usersArrayList;
    ImageView cross_icon, search_icon;
    StatusAdopter statusAdopter;
    ShimmerRecyclerView shimmerRecyclerView;
    ArrayList<UserStatus> statusesArrayList;
    FloatingActionButton floatingActionButton;
    ProgressDialog progressDialog;
    Users users;
    LinearLayout search_bar;
    EditText search_text;
    FrameLayout view_pager;
    int containerId;
    private Uri imageUri;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ChatFragment chatFragment = new ChatFragment();
            fragmentTransaction.replace(R.id.view_pager, chatFragment);
            fragmentTransaction.commit();
        }

        adopter = new UserAdopter(MainActivity.this, new ArrayList<>());

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(StatusCleanupWorker.class, 1, TimeUnit.HOURS)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("statusCleanup", ExistingPeriodicWorkPolicy.KEEP, workRequest);

        auth = FirebaseAuth.getInstance();
        databse = FirebaseDatabase.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        statusesArrayList = new ArrayList<>(); // Initialize statusesArrayList here
        statusAdopter = new StatusAdopter(this, statusesArrayList);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                String currentId = auth.getUid();
                if (currentId != null) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("token", token);
                    databse.getReference().child("user").child(auth.getUid()).updateChildren(map);
                } else {
                    Toast.makeText(MainActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, login_screen.class);
            startActivity(intent);
            finish();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.chats);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                view_pager = findViewById(R.id.view_pager);
                if (itemId == R.id.camera){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = createImageFile(); // Create a file for the captured image
                    if (photoFile != null) {
                        imageUri = FileProvider.getUriForFile(MainActivity.this  ,  "com.example.chatlog_project.provider", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, 100);
                    }
                }
                else if (itemId == R.id.chats) {
                    ChatFragment chatFragment = new ChatFragment();
                    fragmentTransaction.replace(R.id.view_pager, chatFragment);

                } else if (itemId == R.id.discover) {
                    DiscoverFragment discoverFragment = new DiscoverFragment();
                    fragmentTransaction.replace(R.id.view_pager, discoverFragment);

                } else if (R.id.status == itemId) {
                    StatusFragment statusFragment = new StatusFragment();
                    fragmentTransaction.replace(R.id.view_pager, statusFragment);
                } else {
                    Toast.makeText(MainActivity.this, "Temporary Unavailable", Toast.LENGTH_SHORT).show();
                    return false;
                }

                fragmentTransaction.commit();
                return true;
            }
        });


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.search_icon) {

        } else if (itemId == R.id.camera_icon) {

        } else if (itemId == R.id.new_group) {
            Intent intent = new Intent(MainActivity.this, CreateGroupActivity.class);
            startActivity(intent);

        } else if (itemId == R.id.settings) {

            Intent intent = new Intent(MainActivity.this, SettingScreen.class);
            startActivity(intent);

        } else if (itemId == R.id.logout) {

            Dialog dialog = new Dialog(MainActivity.this, R.style.dialogue);
            dialog.setContentView(R.layout.dialogue_layout);
            dialog.show();
            AppCompatButton no_btn, yes_btn;
            no_btn = dialog.findViewById(R.id.no_btn);
            yes_btn = dialog.findViewById(R.id.yes_btn);
            yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    auth.signOut();
                    Intent intent = new Intent(MainActivity.this, login_screen.class);
                    startActivity(intent);
                    finish();
                }
            });
            no_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        } else {
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private File createImageFile() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(
                    "IMG_" + System.currentTimeMillis(),
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onResume() {
        String currentId = auth.getUid();
        databse.getReference().child("present status").child(currentId).setValue("Online");
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra("imageUri", imageUri.toString());
            startActivity(intent);
        }

    }

    @Override
    protected void onPause() {
        String currentId = auth.getUid();
        databse.getReference().child("present status").child(currentId).setValue("Offline");
        super.onPause();
    }
}