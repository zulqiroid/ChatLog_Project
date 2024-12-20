package com.example.chatlog_project.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class name_signup extends AppCompatActivity {
    CircleImageView profile_image;
    TextInputEditText fullname;
    AppCompatButton cont, already_have_account_btn;
    Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_name_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profile_image = findViewById(R.id.profile_image);
        fullname = findViewById(R.id.fullname);
        cont = findViewById(R.id.cont);
        already_have_account_btn = findViewById(R.id.already_have_account_btn);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),1);

            }
        });

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Objects.requireNonNull(fullname.getText()).toString();
                if (name.isEmpty()) {
                    fullname.setError("Please enter your name");
                } else {
                    Intent intent = new Intent(name_signup.this, email_signup.class);
                    intent.putExtra("name", name);
                    if (imageUri != null) {
                        intent.putExtra("imageUri", imageUri.toString());
                    }
                    startActivity(intent);
                }
            }
        });


        already_have_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            if (data != null) {
                imageUri=data.getData();
                try {
                    // Convert Uri to Bitmap
                    Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                    // Compress the Bitmap
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    originalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream); // Compress to 50% quality
                    byte[] compressedImageBytes = outputStream.toByteArray();

                    // Display compressed image in CircleImageView
                    profile_image.setImageBitmap(BitmapFactory.decodeByteArray(compressedImageBytes, 0, compressedImageBytes.length));

                    // Store compressed image bytes for intent
                    imageUri = saveCompressedImage(compressedImageBytes);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Image compression failed", Toast.LENGTH_SHORT).show();
                }

            }}
        else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
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