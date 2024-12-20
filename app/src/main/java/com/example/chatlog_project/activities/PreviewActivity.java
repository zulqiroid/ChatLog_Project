package com.example.chatlog_project.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlog_project.R;
import com.example.chatlog_project.adopters.FriendAdapter;
import com.example.chatlog_project.models.Friend;
import com.example.chatlog_project.models.Users;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    private ImageView previewImage;
    private LinearLayout sendButton;
    private Uri imageUri;
    Bitmap capturedImage;
    FriendAdapter friendAdapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Sending Snap...");
        progressDialog.setCancelable(false);

        previewImage = findViewById(R.id.previewImage);
        sendButton = findViewById(R.id.sendButton);

        // Get the image URI from intent
        imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        // Load and fix the orientation of the image
        capturedImage= loadBitmapWithCorrectOrientation(imageUri);
        previewImage.setImageBitmap(capturedImage);


        // Open bottom sheet dialog on send button click
        sendButton.setOnClickListener(v -> {
            showFriendSelectionDialog();
        });

    }
    private Bitmap loadBitmapWithCorrectOrientation(Uri imageUri) {
        try {
            // Open the image as a stream
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Open the EXIF data
            InputStream exifInputStream = getContentResolver().openInputStream(imageUri);
            ExifInterface exif = new ExifInterface(exifInputStream);
            exifInputStream.close();

            // Get the orientation
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            // Rotate the bitmap if needed
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateBitmap(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateBitmap(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateBitmap(bitmap, 270);
                default:
                    return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void showFriendSelectionDialog() {
        // Create BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_friend_selection, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        RecyclerView friendRecyclerView = bottomSheetView.findViewById(R.id.friendRecyclerView);
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppCompatButton sendBtn = bottomSheetView.findViewById(R.id.sendBtn);

        // Load friends from Firebase
        ArrayList<Users> friendsList = new ArrayList<>();
        ArrayList<String> selectedFriends = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("friends")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friendsList.clear();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            String friendId = friendSnapshot.getKey(); // Friend's UID
                            if (friendId != null) {
                                // Fetch friend's details from "user" node
                                FirebaseDatabase.getInstance().getReference("user")
                                        .child(friendId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                                Users friend = userSnapshot.getValue(Users.class);
                                                if (friend != null) {
                                                    friendsList.add(friend);

                                                    // Update adapter
                                                    friendAdapter = new FriendAdapter(
                                                            PreviewActivity.this, friendsList, selectedFriends, sendBtn, new FriendAdapter.SnapsSendCallback() {
                                                                @Override
                                                                public void onComplete() {
                                                                    // Handle successful completion
                                                                    progressDialog.dismiss();
                                                                    bottomSheetDialog.dismiss();
                                                                    Toast.makeText(PreviewActivity.this, "Snaps sent successfully!", Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void onError(String error) {
                                                                    // Handle errors
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(PreviewActivity.this, "Failed to send snaps: " + error, Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                    friendRecyclerView.setAdapter(friendAdapter);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                // Handle error
                                            }
                                        });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error

                    }
                });

        sendBtn.setOnClickListener(v -> {
            if (selectedFriends.isEmpty()) {
                Toast.makeText(this, "Please select at least one friend", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show the ProgressDialog
            progressDialog.show();

            // Send snaps to selected friends
            friendAdapter.sendSnapsToSelectedFriends(FirebaseDatabase.getInstance(), FirebaseAuth.getInstance(), imageUri, new FriendAdapter.SnapsSendCallback() {
                @Override
                public void onComplete() {
                    // Dismiss the ProgressDialog and close the dialog
                    progressDialog.dismiss();
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(PreviewActivity.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                    Toast.makeText(PreviewActivity.this, "Snaps sent successfully!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    // Handle any errors
                    progressDialog.dismiss();
                    Toast.makeText(PreviewActivity.this, "Failed to send snaps: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });



        bottomSheetDialog.show();
    }

}