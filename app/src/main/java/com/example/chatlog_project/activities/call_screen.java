/*
package com.example.chatlog_project.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;
import com.example.chatlog_project.models.InterfaceJava;
import com.example.chatlog_project.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class call_screen extends AppCompatActivity {
    String uniqueId = "";
    FirebaseAuth auth;
    String username = "";
    String friendsUsername = "";
    ImageButton endCall;
    ImageView micBtn, videoBtn;
    Group loadingGroup, controls;
    CircleImageView profile;
    TextView name;
    ;
    WebView webView;

    boolean isPeerConnected = false;

    DatabaseReference firebaseRef;

    boolean isAudio = true;
    boolean isVideo = true;
    String createdBy;

    boolean pageExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call_screen);


        auth = FirebaseAuth.getInstance();
        firebaseRef = FirebaseDatabase.getInstance().getReference().child("usersCall");

        endCall = findViewById(R.id.endCall);
        micBtn = findViewById(R.id.micBtn);
        videoBtn = findViewById(R.id.videoBtn);
        loadingGroup = findViewById(R.id.loadingGroup);
        controls = findViewById(R.id.controls);
        webView = findViewById(R.id.webView);
        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);

        username = getIntent().getStringExtra("username");
        String incoming = getIntent().getStringExtra("incoming");
        createdBy = getIntent().getStringExtra("createdBy");

//        friendsUsername = "";
//
//        if(incoming.equalsIgnoreCase(friendsUsername))
//            friendsUsername = incoming;

        friendsUsername = incoming;

        setupWebView(username, createdBy, friendsUsername);

        micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAudio = !isAudio;
                callJavaScriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");
                if (isAudio) {
                    micBtn.setImageResource(R.drawable.mic_on);
                } else {
                    micBtn.setImageResource(R.drawable.mic_off);
                }
            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVideo = !isVideo;
                callJavaScriptFunction("javascript:toggleVideo(\"" + isVideo + "\")");
                if (isVideo) {
                    videoBtn.setImageResource(R.drawable.video_on);
                } else {
                    videoBtn.setImageResource(R.drawable.video_off);
                }
            }
        });

        endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @SuppressLint("SetJavaScriptEnabled")
    void setupWebView(String username, String createdBy, String friendsUsername) {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new InterfaceJava(this), "Android");

        loadVideoCall(username, createdBy, friendsUsername);
    }

    public void loadVideoCall(String username, String createdBy, String friendsUsername) {
        String filePath = "file:///android_asset/call.html";
        webView.loadUrl(filePath);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializePeer(username, createdBy);
            }
        });
    }


    void initializePeer(String username, String createdBy) {
        uniqueId = getUniqueId();

        callJavaScriptFunction("javascript:init(\"" + uniqueId + "\")");

        if (createdBy.equalsIgnoreCase(username)) {
            if (pageExit)
                return;
            firebaseRef.child(username).child("connId").setValue(uniqueId);
            firebaseRef.child(username).child("isAvailable").setValue(true);

            loadingGroup.setVisibility(View.GONE);
            controls.setVisibility(View.VISIBLE);

            FirebaseDatabase.getInstance().getReference()
                    .child("user")
                    .child(friendsUsername)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Users user = snapshot.getValue(Users.class);

                            assert user != null;
                            Picasso.get().load(user.getProfile_image()).into(profile);
                            name.setText(user.getName());

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            return null;
                        }
                    });

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    friendsUsername = createdBy;
                    FirebaseDatabase.getInstance().getReference()
                            .child("user")
                            .child(friendsUsername)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    String picture = snapshot.child("profile_image").getValue(String.class);
                                    String uname= snapshot.child("name").getValue(String.class);
                                    Picasso.get().load(picture).into(profile);
                                    name.setText(uname);

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    return null;
                                }
                            });
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(friendsUsername)
                            .child("connId")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        sendCallRequest();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    return null;
                                }
                            });
                }
            }, 3000);
        }

    }

    public void onPeerConnected() {
        isPeerConnected = true;
    }

    void sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You are not connected. Please check your internet.", Toast.LENGTH_SHORT).show();
            return;
        }

        listenConnId();
    }

    void listenConnId() {
        firebaseRef.child(friendsUsername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null)
                    return;

               loadingGroup.setVisibility(View.GONE);
               controls.setVisibility(View.VISIBLE);
                String connId = snapshot.getValue(String.class);
                callJavaScriptFunction("javascript:startCall(\"" + connId + "\")");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                return null;
            }
        });
    }

    void callJavaScriptFunction(String function) {
       webView.post(new Runnable() {
            @Override
            public void run() {
              webView.evaluateJavascript(function, null);
            }
        });
    }

    String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageExit = true;
        firebaseRef.child(createdBy).setValue(null);
        finish();
    }
}*/
