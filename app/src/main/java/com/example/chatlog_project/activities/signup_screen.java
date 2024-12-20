/*

package com.example.chatlog_project.activities;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;


public class signup_screen extends AppCompatActivity {

    CircleImageView profile_image;
    TextInputEditText username,email,password,re_enter_password;
    AppCompatButton signin_btn ,already_have_account_btn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();


        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        re_enter_password=findViewById(R.id.re_enter_password);
        signin_btn=findViewById(R.id.signin_btn);
        already_have_account_btn=findViewById(R.id.already_have_account_btn);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating account...");

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);

            }
        });


        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();


                String Username= Objects.requireNonNull(username.getText()).toString();
                String Email= Objects.requireNonNull(email.getText()).toString();
                String Password= Objects.requireNonNull(password.getText()).toString();
                String Re_enter_password= Objects.requireNonNull(re_enter_password.getText()).toString();
                String status="Hey there I am using ChatLog";



                if(Username.isEmpty() || Email.isEmpty() || Password.isEmpty() || Re_enter_password.isEmpty()){
                    progressDialog.dismiss();
                    Toast.makeText(signup_screen.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }

                else if(!Password.equals(Re_enter_password)){
                    progressDialog.dismiss();
                    Toast.makeText(signup_screen.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
                else if(Password.length()<6){
                    progressDialog.dismiss();
                    Toast.makeText(signup_screen.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else if(!Email.contains("@")){
                    progressDialog.dismiss();
                    Toast.makeText(signup_screen.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                }
                else if(!Email.contains(".")){
                    progressDialog.dismiss();
                    Toast.makeText(signup_screen.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                }
                else if(!Email.contains("com")){
                    progressDialog.dismiss();
                    Toast.makeText(signup_screen.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                String userId=auth.getCurrentUser().getUid();
                                DatabaseReference reference=database.getReference().child("user").child(userId);
                                StorageReference storageReference=storage.getReference().child("upload").child(userId);

                                if (imageUri!=null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageUri= Uri.parse(uri.toString());
                                                             Users users = new Users(imageUri, userId, status, Username, Email, Password);                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                     if (task.isSuccessful()){
                                                                         Toast.makeText(signup_screen.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                                                         Intent intent=new Intent(signup_screen.this,MainActivity.class);
                                                                         startActivity(intent);
                                                                         finishAffinity();
                                                                     }
                                                                     else{
                                                                         Toast.makeText(signup_screen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                                    Users users = new Users(imageUri, userId, status, Username, Email, Password);                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(signup_screen.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(signup_screen.this,MainActivity.class);
                                                startActivity(intent);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                finish();
                                            }
                                            else{
                                                progressDialog.dismiss();
                                                Toast.makeText(signup_screen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(signup_screen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        already_have_account_btn.setOnClickListener(view -> {
            progressDialog.show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            if (data != null) {
            imageUri=data.getData();
            profile_image.setImageURI(imageUri);

            }}
        else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
   }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();

    }
}
*/
