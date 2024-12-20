package com.example.chatlog_project.activities;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.media.ExifInterface;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.Common;
import com.example.chatlog_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingScreen extends AppCompatActivity {

    CircleImageView profile_image;
    ImageButton edit_btn;
    LinearLayout name_layout, username_layout, birthday_layout, bio_layout;
    TextView name_here, username_here, birthday_here, bio_here;
    FirebaseAuth auth=FirebaseAuth.getInstance();

    FirebaseDatabase database=FirebaseDatabase.getInstance();

    FirebaseStorage storage=FirebaseStorage.getInstance();
    Uri imageUri;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        // Enable back navigation icon
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Handle back button click
        toolbar.setNavigationOnClickListener(v -> {
            // Finish the current activity
            finish();
        });

        profile_image = findViewById(R.id.profile_image);
        edit_btn = findViewById(R.id.edit_btn);
        name_layout = findViewById(R.id.name_layout);
        username_layout = findViewById(R.id.username_layout);
        birthday_layout = findViewById(R.id.birthday_layout);
        bio_layout = findViewById(R.id.bio_layout);
        name_here = findViewById(R.id.name_here);
        username_here = findViewById(R.id.username_here);
        birthday_here = findViewById(R.id.birthday_here);
        bio_here = findViewById(R.id.bio_here);



        String userId= Objects.requireNonNull(auth.getCurrentUser()).getUid();

        DatabaseReference reference=database.getReference().child("user").child(userId);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name=snapshot.child("name").getValue(String.class);
                    String username=snapshot.child("username").getValue(String.class);
                    String birthday=snapshot.child("birthday").getValue(String.class);
                    String bio=snapshot.child("status").getValue(String.class);
                    String imageUri=snapshot.child("profile_image").getValue(String.class);

                    name_here.setText(name);
                    username_here.setText(username);
                    birthday_here.setText(birthday);
                    bio_here.setText(bio);
                    Picasso.get().load(imageUri).placeholder(R.drawable.user_w).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        name_layout.setOnClickListener(v -> {
            // Get the name to pass
            String name = name_here.getText().toString();

            // Create an intent to navigate to SettingName
            Intent intent = new Intent(SettingScreen.this, setting_name.class);

            // Pass the name using extras
            intent.putExtra("name", name);

            // Start the SettingName activity
            startActivity(intent);
        });
        username_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = username_here.getText().toString();

                // Create an intent to navigate to SettingName
                Intent intent = new Intent(SettingScreen.this, setting_username.class);

                // Pass the name using extras
                intent.putExtra("username", username);

                // Start the SettingName activity
                startActivity(intent);
            }
        });
        bio_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bio=bio_here.getText().toString();
                Intent intent=new Intent(SettingScreen.this,setting_bio.class);
                intent.putExtra("bio",bio);
                startActivity(intent);
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),1 );


            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_image.invalidate();
                Drawable drawable=profile_image.getDrawable();
                // Ensure the drawable is not null and is an instance of BitmapDrawable
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    Common.IMAGE_BITMAP = bitmap;

                    // Transition to the View_image activity
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SettingScreen.this, profile_image, "image");
                    Intent intent = new Intent(SettingScreen.this, View_image.class);
                    startActivity(intent, options.toBundle());
                } else {
                    Toast.makeText(SettingScreen.this, "Unable to extract image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        String userId= Objects.requireNonNull(auth.getCurrentUser()).getUid();
        DatabaseReference reference=database.getReference().child("user").child(userId);
        StorageReference storageReference=storage.getReference().child("upload").child(userId);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try{
                // Convert Uri to Bitmap
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                // Compress the Bitmap
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream); // Compress to 50% quality
                byte[] compressedImageBytes = outputStream.toByteArray();

                // Display compressed image in CircleImageView
                profile_image.setImageBitmap(BitmapFactory.decodeByteArray(compressedImageBytes, 0, compressedImageBytes.length));

                // Save compressed image to a file and update imageUri
                imageUri = saveCompressedImage(compressedImageBytes);

                if (imageUri != null) {
                    progressDialog.show();
                    storageReference.putFile(imageUri).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                reference.child("profile_image").setValue(uri.toString())
                                        .addOnCompleteListener(task1 -> {
                                            progressDialog.dismiss();
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(SettingScreen.this, "Profile image updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(SettingScreen.this, "Failed to update database", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(SettingScreen.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }).addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(SettingScreen.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SettingScreen.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SettingScreen.this, "No image selected", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Image compression failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }
    private Uri saveCompressedImage(byte[] imageBytes) {
        try {
            File tempFile = new File(getCacheDir(), "compressed_image.jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(imageBytes);
            fos.close();
            return Uri.fromFile(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save compressed image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}