package com.example.chatlog_project.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.content.ContentResolver;
import java.io.OutputStream;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.library.baseAdapters.BuildConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.example.chatlog_project.models.MessageModel;
import com.example.chatlog_project.R;
import com.example.chatlog_project.adopters.messagesAdopter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class chat_win extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 332;
    CircleImageView profile_image;
    TextView user_name, user_status;
    public String reciever_name;
    String receiver_img;
    String sender_id;
    String receiver_id;
    String senderRoom;
    String receiverRoom;
    ImageView send_btn;
    EditText message_input;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    public static String senderimg, receiverimg;
    RecyclerView recyclerView;
    ArrayList<MessageModel> messageArrayList;
    com.example.chatlog_project.adopters.messagesAdopter messagesAdopter;
    ImageView back_btn, attachment_btn, camera_btn;
    ProgressDialog progressDialog;
    LinearLayout layout_clickable;
    RecordButton record_button;
    RecordView record_view;
    ImageButton voice_call_btn, video_call_btn, more_btn;
    CardView  message_input_card, send_btn_card;

    //audio
    private MediaRecorder mediaRecorder;
    private String audio_path;
    private String sTime;
    private Uri imageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_win);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // Inside the onCreate() method of chat_win activity:
        RelativeLayout mainLayout = findViewById(R.id.main); // Get the main layout

        LayoutInflater inflater = LayoutInflater.from(this);
        View toolbarLayout = inflater.inflate(R.layout.toolbar_onlongpress, mainLayout, false); // Inflate the toolbar layout

        mainLayout.addView(toolbarLayout); // Add the toolbar layout to the main layout

        toolbarLayout.setVisibility(View.GONE); // Initially hide the toolbar layout
        profile_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.user_name);
        recyclerView = findViewById(R.id.recycler_view);
        message_input = findViewById(R.id.message_input);
        send_btn = findViewById(R.id.send_btn);
        back_btn = findViewById(R.id.back_btn);
        attachment_btn = findViewById(R.id.attachment_btn);
        camera_btn = findViewById(R.id.camera_btn);
        user_status = findViewById(R.id.user_status);
        layout_clickable = findViewById(R.id.layout_clickable);
        record_button = findViewById(R.id.record_button);
        record_view = findViewById(R.id.record_view);
        message_input_card = findViewById(R.id.message_input_card);
        send_btn_card = findViewById(R.id.send_btn_card);
        voice_call_btn = findViewById(R.id.voice_call_btn);
        video_call_btn = findViewById(R.id.video_call_btn);
        more_btn = findViewById(R.id.more_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        {
            reciever_name = getIntent().getStringExtra("name");
            receiver_img = getIntent().getStringExtra("profile_image");
            receiver_id = getIntent().getStringExtra("uid");
            String token = getIntent().getStringExtra("token");
        }
        user_name.setText(reciever_name);
        Picasso.get().load(receiver_img).placeholder(R.drawable.user_w).into(profile_image);

        database.getReference().child("present status").child(receiver_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status_OF = snapshot.getValue(String.class);
                    if (!status_OF.isEmpty()) {
                        if (status_OF.equals("offline")) {
                            user_status.setVisibility(View.GONE);

                        }
                        user_status.setVisibility(View.VISIBLE);
                        user_status.setText(status_OF);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sender_id = auth.getUid();
        senderRoom = sender_id + receiver_id;
        receiverRoom = receiver_id + sender_id;

        messageArrayList = new ArrayList<>();
        messagesAdopter = new messagesAdopter((Activity) chat_win.this, messageArrayList, senderRoom, receiverRoom, reciever_name);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messagesAdopter);


        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("message");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                int currentPosition = linearLayoutManager.findFirstVisibleItemPosition();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messageModel.setMessage_id(dataSnapshot.getKey());
                    messageArrayList.add(messageModel);
                }
                messagesAdopter.notifyDataSetChanged();
                linearLayoutManager.scrollToPositionWithOffset(currentPosition, 0);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderimg = snapshot.child("profile_image").getValue().toString();
                receiverimg = receiver_img;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


        attachment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 25);

            }
        });
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo Open Camera

                checkCameraPermission();

            }
        });
        final Handler handler = new Handler();
        message_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("present status").child(sender_id).setValue("typing...");
                handler.removeCallbacks(null);
                handler.postDelayed(typingStop, 1000);
            }

            Runnable typingStop = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("present status").child(sender_id).setValue("Online");
                }
            };
        });

        message_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = message_input.getText().toString().trim();
                if (message.isEmpty()) {
                    record_view.setVisibility(View.GONE);
                    record_button.setVisibility(View.VISIBLE);
                    send_btn_card.setVisibility(View.GONE);
                } else {
                    record_view.setVisibility(View.GONE);
                    record_button.setVisibility(View.GONE);
                    send_btn_card.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendButtonClick(sender_id, receiver_id);
            }
        });

        layout_clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chat_win.this, User_profile.class);
                intent.putExtra("receiver_id", receiver_id);
                intent.putExtra("receiver_name", reciever_name);
                intent.putExtra("receiver_img", receiver_img);
                startActivity(intent);
            }
        });

        record_button.setRecordView(record_view);
        record_view.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

             if (!checkPermissionFromDevice( ) )
             {
                 String currentId = auth.getUid();
                 database.getReference().child("present status").child(currentId).setValue("Recording");
                 message_input_card.setVisibility( View. GONE) ;
                 record_view.setVisibility( View. VISIBLE) ;
                 startRecord();
                 Vibrator vibrator = (Vibrator) getSystemService(Context. VIBRATOR_SERVICE) ;
                 if ( vibrator != null) {
                     vibrator.vibrate(100) ;}
                 }else {
                 requestPermission();
                 }
            }

            @Override
            public void onCancel() {
                if (mediaRecorder != null) {
                    mediaRecorder.reset();
                }

            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                String currentId = auth.getUid();
                database.getReference().child("present status").child(currentId).setValue("Online");
                message_input_card.setVisibility(View.VISIBLE);
                record_view.setVisibility(View.GONE);

                //stop recording
                try {
                    sTime= getHumanTimeText(recordTime);
                    stopRecord();
                }catch (Exception e) {
                    Toast.makeText(chat_win.this, "finish", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLessThanSecond() {
                String currentId = auth.getUid();
                database.getReference().child("present status").child(currentId).setValue("Online");
                message_input_card.setVisibility(View.VISIBLE);
                record_view.setVisibility(View.GONE);
            }

            @Override
            public void onLock() {

            }
        });
        record_view.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                String currentId = auth.getUid();
                database.getReference().child("present status").child(currentId).setValue("Online");
                message_input_card.setVisibility(View.VISIBLE);
                record_view.setVisibility(View.GONE);
            }
        });

        //buttons for call...
        {
            voice_call_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(chat_win.this, "voice call is temporary unavailable", Toast.LENGTH_SHORT).show();
                }
            });

            video_call_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(chat_win.this, "video call is temporary unavailable", Toast.LENGTH_SHORT).show();

              /*  Intent intent = new Intent(chat_win.this, connecting_call.class);
                intent.putExtra("caller_id", sender_id);
                intent.putExtra("receiver_id", receiver_id);
                intent.putExtra("receiver_name", reciever_name);
                intent.putExtra("receiver_img", receiver_img);
                startActivity(intent);
            }*/
                }
            });

        }
    }


    private void checkCameraPermission() {
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp= new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName="IMG_" + timeStamp +".jpg";
        try {
            File file = File.createTempFile(
                    "IMG_" + timeStamp,
                    ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            );
            imageUri= FileProvider.getUriForFile(this, "com.example.chatlog_project.provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, 440);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        try {
            if ( mediaRecorder != null){
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

                //send voice
                sendVoice(audio_path);
                //upLoadVoice();

            }
        }catch (Exception e){
            Toast.makeText(this, "problem 3"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }


    private void startRecord() {
        String path_save = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.m4a";
        audio_path= path_save;
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(audio_path);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            Toast.makeText(this, "MediaRecorder setup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkPermissionFromDevice(){
        int record_audio_result= ContextCompat.checkSelfPermission(chat_win.this, Manifest.permission.RECORD_AUDIO);
        return record_audio_result== PackageManager.PERMISSION_DENIED;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);
    }

    public void sendVoice(String audio_path){
        Uri uriAudio = Uri.fromFile(new File(audio_path));
        final StorageReference audioRef= storage.getReference().child("chats/voice" + System.currentTimeMillis());
        audioRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot audioSnapshot) {
                Task<Uri> urlTask = audioSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                String voiceUrl = String.valueOf(downloadUrl);

                Date date = Calendar.getInstance().getTime();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String currentdate = formatter.format(date);

                Calendar currentDateTime = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
                String currentTime = df.format(currentDateTime.getTime());
                String timestamp = currentdate + " , " + currentTime;


                MessageModel messageModel = new MessageModel(sender_id, receiver_id, voiceUrl, sTime, currentdate, currentTime, timestamp, "voice");
                String randomKey = database.getReference().push().getKey();
                assert randomKey != null;
                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", "Voice Message");
                lastMsgObj.put("lastMsgTime", currentTime);

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
                messagesAdopter.notifyDataSetChanged();
                database.getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("message")
                        .child(randomKey)
                        .setValue(messageModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats").child(receiverRoom).child("message").child(randomKey).setValue(messageModel)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(chat_win.this, "Audio uploaded successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        });
            }
        });
    }


    private void sendButtonClick(String sender_id, String receiver_id) {
        String message = message_input.getText().toString().trim();
        if (!message.isEmpty()) {
            sendMessage(sender_id, receiver_id, message);
        }
    }

    private void sendMessage(String sender_id, String receiver_id, String message) {

        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String currentdate = formatter.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());
        String timestamp = currentdate + " , " + currentTime;

        MessageModel messageModel = new MessageModel(sender_id, receiver_id, message, currentTime, currentdate, timestamp, "text");
        message_input.setText("");

        String randomKey = database.getReference().push().getKey();
        assert randomKey != null;
        HashMap<String, Object> lastMsgObj = new HashMap<>();
        lastMsgObj.put("lastMsg", messageModel.getMessage());
        lastMsgObj.put("lastMsgTime", currentTime);

        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
        messagesAdopter.notifyDataSetChanged();
        database.getReference()
                .child("chats")
                .child(senderRoom)
                .child("message")
                .child(randomKey)
                .setValue(messageModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("message")
                                .child(randomKey)
                                .setValue(messageModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });


                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    progressDialog.show();
                    Uri selectedImage = data.getData();
                    uploadToFirebase(selectedImage);


                }
            }
        }
        if (requestCode == 440
                && resultCode == RESULT_OK){
            progressDialog.show();
             uploadToFirebase(imageUri);

        }
    }

    private void uploadToFirebase(Uri uri) {
        Calendar calendar = Calendar.getInstance();

        StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
        reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String filePath = uri.toString();
                            String message = message_input.getText().toString();

                            Date date = Calendar.getInstance().getTime();
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                            String currentdate = formatter.format(date);

                            Calendar currentDateTime = Calendar.getInstance();
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
                            String currentTime = df.format(currentDateTime.getTime());
                            String timestamp = currentdate + " , " + currentTime;

                            MessageModel messageModel = new MessageModel(sender_id, receiver_id, message, currentTime, currentdate, timestamp, "photo");
                            messageModel.setMessage("photo");
                            messageModel.setImageUrl(filePath);
                            message_input.setText("");

                            String randomKey = database.getReference().push().getKey();
                            assert randomKey != null;
                            HashMap<String, Object> lastMsgObj = new HashMap<>();
                            lastMsgObj.put("lastMsg", messageModel.getMessage());
                            lastMsgObj.put("lastMsgTime", currentTime);

                            database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                            database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
                            messagesAdopter.notifyDataSetChanged();
                            database.getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("message")
                                    .child(randomKey)
                                    .setValue(messageModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            database.getReference().child("chats").child(receiverRoom).child("message").child(randomKey).setValue(messageModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                }
                                            });


                                        }
                                    });
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        String currentId = auth.getUid();
        database.getReference().child("present status").child(currentId).setValue("Online");
        super.onResume();
    }

    @Override
    protected void onPause() {
        String currentId = auth.getUid();
        database.getReference().child("present status").child(currentId).setValue("Offline");
        super.onPause();
    }
/*
    @Override
    protected void onStart() {
        super.onStart();

        // Listen for incoming calls
        String currentUserId = FirebaseAuth.getInstance().getUid();
       FirebaseDatabase.getInstance().getReference("usersCall").orderByChild("receiverId")
                .equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        for(DataSnapshot snapshot : datasnapshot.getChildren())  {
                            String status = snapshot.child("status").getValue(String.class);

                            // Check if the call is waiting to be accepted
                            assert status != null;
                            if (status.equals("waiting")) {
                                String callerName = snapshot.child("callerName").getValue(String.class);
                                String callerProfilePic = snapshot.child("callerProfilePic").getValue(String.class);
                                String callerId = snapshot.child("createdBy").getValue(String.class);
                                String callId = snapshot.child("callId").getValue(String.class);
                                if (callerId == null){
                                    Toast.makeText(chat_win.this, "null", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(chat_win.this, callerId, Toast.LENGTH_SHORT).show();
                                }

                                // Open Incoming Call Activity
                               Intent intent = new Intent(chat_win.this, incomming_call.class);
                                intent.putExtra("receiverId", currentUserId);
                                intent.putExtra("callId", callId);
                                intent.putExtra("callerId", callerId);
                                intent.putExtra("createdBy", callerName);
                                intent.putExtra("callerProfilePic", callerProfilePic);
                                startActivity(intent);
                            }else if (status.equals("caller ended")){
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }
*/

}