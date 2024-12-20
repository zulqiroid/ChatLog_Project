package com.example.chatlog_project.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;
import com.example.chatlog_project.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class password_signup extends AppCompatActivity {
    private TextInputEditText password, re_enter_password;
    private String final_password;
    AppCompatButton signin_btn;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        password = findViewById(R.id.password);
        re_enter_password = findViewById(R.id.re_enter_password);
        signin_btn = findViewById(R.id.signin_btn);
        String name = getIntent().getStringExtra("name");
        String imageUriString = getIntent().getStringExtra("imageUri");
        String Email = getIntent().getStringExtra("email");
        String uname = getIntent().getStringExtra("username");
        String gender = getIntent().getStringExtra("gender");
        String birthday = getIntent().getStringExtra("birthday");
        String status="Hey there I am using ChatLog";

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // progressDialog.show();
                String password1 = password.getText().toString();
                String password2 = re_enter_password.getText().toString();

                if(password1.isEmpty() || password2.isEmpty()){
                    progressDialog.dismiss();
                    Toast.makeText(password_signup.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else if(!password1.equals(password2)){
                    progressDialog.dismiss();
                    Toast.makeText(password_signup.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
                else if(password1.length()<6){
                    progressDialog.dismiss();
                    Toast.makeText(password_signup.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }else {
                    final_password = password1;
                    assert Email != null;
                    imageUri=Uri.parse(imageUriString);
                    auth.createUserWithEmailAndPassword(Email,final_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                String userId=auth.getCurrentUser().getUid();
                                DatabaseReference reference=database.getReference().child("user").child(userId);
                                StorageReference storageReference=storage.getReference().child("upload").child(userId);

                                if (imageUri!=null && !imageUri.toString().isEmpty()){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageUri= Uri.parse(uri.toString());
                                                        Users users = new Users(userId, name, imageUri.toString(), uname, gender, birthday, status, Email, final_password);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(password_signup.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                                                    Intent intent=new Intent(password_signup.this,MainActivity.class);
                                                                    startActivity(intent);
                                                                    finishAffinity();
                                                                }
                                                                else{
                                                                    Toast.makeText(password_signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                else {
                                    String status="Hey there I am using ChatLog";
                                    imageUri= Uri.parse("https://firebasestorage.googleapis.com/v0/b/chatlog-c2899.appspot.com/o/user_w.png?alt=media&token=6ed3730d-5c97-4efd-bb80-0b9718a92e62");
                                    Users users = new Users(userId, name, imageUri.toString(), uname, gender, birthday, status, Email, final_password);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(password_signup.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(password_signup.this,MainActivity.class);
                                                startActivity(intent);
                                                finishAffinity();
                                            }
                                            else{
                                                progressDialog.dismiss();
                                                Toast.makeText(password_signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(password_signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}