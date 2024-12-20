package com.example.chatlog_project.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.R;

public class Birthday_signup extends AppCompatActivity {

    private RadioGroup genderRadioGroup;
    private EditText dayEditText, monthEditText, yearEditText;
    private String gender, birthday;
    AppCompatButton continue_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_birthday_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        genderRadioGroup = findViewById(R.id.gender_radiogroup);
        dayEditText = findViewById(R.id.day_edittext);
        monthEditText = findViewById(R.id.month_edittext);
        yearEditText = findViewById(R.id.year_edittext);
        continue_btn=findViewById(R.id.continue_btn);


        dayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this functionality
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2) { // Assuming day is 2 digits
                    monthEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this functionality
            }
        });

        monthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this functionality
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2) { // Assuming month is 2 digits
                    yearEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this functionality
            }
        });


        String name = getIntent().getStringExtra("name");
        String Email = getIntent().getStringExtra("email");
        String uname = getIntent().getStringExtra("username");
        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male_radiobutton) {
                    gender = "Male";
                } else if (checkedId == R.id.female_radiobutton) {
                    gender = "Female";
                } else if (checkedId == R.id.prefer_not_to_say_radiobutton) {
                    gender = "Prefer not to say";
                }
            }
        });
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = dayEditText.getText().toString();
                String month = monthEditText.getText().toString();
                String year = yearEditText.getText().toString();
                birthday = day + " / " + month + " / " + year;


                if (gender != null && !gender.isEmpty() && birthday != null && !birthday.isEmpty()) {
                    Toast.makeText(Birthday_signup.this, name, Toast.LENGTH_SHORT).show();
                    Toast.makeText(Birthday_signup.this, imageUriString, Toast.LENGTH_SHORT).show();
                    Toast.makeText(Birthday_signup.this, Email, Toast.LENGTH_SHORT).show();
                    Toast.makeText(Birthday_signup.this, gender, Toast.LENGTH_SHORT).show();
                    Toast.makeText(Birthday_signup.this, birthday, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Birthday_signup.this, password_signup.class);
                    intent.putExtra("name", name);
                    intent.putExtra("email", Email);
                    intent.putExtra("username", uname);
                    intent.putExtra("imageUri", imageUriString);
                    intent.putExtra("gender", gender);
                    intent.putExtra("birthday", birthday);
                    startActivity(intent);
                }else {
                    // Handle the case where gender or birthday is not selected/entered
                    // For example, you could show an error message to the user
                    Toast.makeText(Birthday_signup.this, "Please select gender and enter birthday", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}